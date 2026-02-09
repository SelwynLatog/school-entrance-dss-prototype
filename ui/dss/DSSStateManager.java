//DSSStateManager.java
/**
 Manages state transitions and evaluation context for the DSS dashboard.

   Manages the ff:
 - What state we're in
 - What item we're evaluating
 - How to transition between states
 - How to run evaluations 
 */
package ui.dss;

import engine.DecisionEngine;
import engine.DecisionResult;
import enums.Decision;
import model.Item;
import service.ItemService;
import storage.ItemLog;

import java.util.List;

public class DSSStateManager {
    
    /**
     Possible states in the evaluation workflow.
     */
    public enum EvaluationState {
        QUEUE,          // Browsing pending items
        EVALUATING,     // Running evaluation engine
        RESULT,         // Showing evaluation outcome
        CONFIRMATION    // Showing success feedback
    }
    
    // Services
    private final ItemService itemService;
    
    // Current state
    private EvaluationState currentState;
    
    // Queue management
    private List<ItemLog.ItemEntry> queuedItems;
    private int selectedIndex;
    
    // Evaluation context
    private ItemLog.ItemEntry currentEntry;
    private DecisionResult currentResult;
    
    public DSSStateManager(ItemService itemService) {
        this.itemService = itemService;
        this.currentState = EvaluationState.QUEUE;
        this.selectedIndex = 0;
        refreshQueue();
    }
    
    //State access
    public EvaluationState getCurrentState() {
        return currentState;
    }
    
    public List<ItemLog.ItemEntry> getQueuedItems() {
        return queuedItems;
    }
    
    public int getSelectedIndex() {
        return selectedIndex;
    }
    
    public ItemLog.ItemEntry getCurrentEntry() {
        return currentEntry;
    }
    
    public DecisionResult getCurrentResult() {
        return currentResult;
    }
    //Queue management
    /**
     * Reloads the queue with all HELD items.
     Called when returning to QUEUE state.
     */
    public void refreshQueue() {
        queuedItems = itemService.getAllItemsWithIds().stream()
            .filter(entry -> entry.getItem().getStatus() == enums.ItemStatus.HELD)
            .toList();
        
        // Keep selection valid
        if (queuedItems.isEmpty()) {
            selectedIndex = -1;
        } else if (selectedIndex >= queuedItems.size()) {
            selectedIndex = queuedItems.size() - 1;
        } else if (selectedIndex < 0) {
            selectedIndex = 0;
        }
    }
    
    /**
     Moves selection up in the queue.
     */
    public void moveSelectionUp() {
        if (selectedIndex > 0) {
            selectedIndex--;
        }
    }
    
    /**
     Moves selection down in the queue.
     */
    public void moveSelectionDown() {
        if (selectedIndex < queuedItems.size() - 1) {
            selectedIndex++;
        }
    }
    
    /**
     Checks if queue is not empty
     */
    public boolean hasQueuedItems() {
        return !queuedItems.isEmpty() && selectedIndex >= 0;
    }
    
    //State Transitions
    
    public void transitionTo(EvaluationState newState) {
        currentState = newState;
        
        // Refresh queue when returning to QUEUE state
        if (newState == EvaluationState.QUEUE) {
            refreshQueue();
        }
    }
    
    /**
     Starts evaluation of the currently selected item.
     Transitions from QUEUE to EVALUATING.
     */
    public void startEvaluation() {
        if (!hasQueuedItems()) {
            return;
        }
        
        currentEntry = queuedItems.get(selectedIndex);
        currentResult = null;
        transitionTo(EvaluationState.EVALUATING);
    }
    
    /**
     Runs the decision engine on the current entry.
     Should be called during EVALUATING state.
     */
    public void runEvaluation() {
        if (currentEntry == null) {
            transitionTo(EvaluationState.QUEUE);
            return;
        }
        
        try {
            Item item = currentEntry.getItem();
            currentResult = DecisionEngine.evaluate(item);
        } catch (Exception e) {
            System.err.println("Evaluation failed: " + e.getMessage());
            e.printStackTrace();
            transitionTo(EvaluationState.QUEUE);
        }
    }
    
    public void logDecision() {
        if (currentResult == null || currentEntry == null) {
            return;
        }
        
        // Items that pass can be released
        if (currentResult.getDecision() == Decision.ALLOW) {
            itemService.releaseItem(currentEntry.getId());
        }
        
    }
    
   //stats
    /**
     * Returns count of recently processed items not in que.
     */
    public long getProcessedCount() {
        long totalItems = itemService.getAllItemsWithIds().size();
        return totalItems - queuedItems.size();
    }
}