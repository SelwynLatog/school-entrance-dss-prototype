//DSSAdminScreen.java
/**
 Main coordinator for the DSS Admin Dashboard. 
 ff actions:
 1. Create the screen
 2. Create the state manager
 3. Create the view renderers
 4. Run the main loop (render + handle input)
 */
package ui.dss;

import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;
import service.ItemService;
import ui.ViewLogScreen;
import ui.AddItemScreen;

import java.io.IOException;

public class DSSAdminScreen {
    
    private final Screen screen;
    private final ItemService itemService;
    private final DSSStateManager stateManager;
    
    // View renderers
    private final DSSQueueView queueView;
    private final DSSEvaluatingView evaluatingView;
    private final DSSResultView resultView;
    private final DSSConfirmationView confirmationView;
    
    // Control flag
    private boolean running;
    
    public DSSAdminScreen(ItemService itemService) throws IOException {
        this.itemService = itemService;
        
        // Initialize Lanterna
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        this.screen = new TerminalScreen(terminal);
        
        // Initialize state manager
        this.stateManager = new DSSStateManager(itemService);
        
        // Initialize view renderers
        this.queueView = new DSSQueueView(screen);
        this.evaluatingView = new DSSEvaluatingView(screen);
        this.resultView = new DSSResultView(screen);
        this.confirmationView = new DSSConfirmationView(screen);
        
        this.running = false;
    }
    
    public boolean show() throws IOException {
        screen.startScreen();
        running = true;
        
        try {
            mainLoop();
            return true;
        } finally {
            screen.stopScreen();
        }
    }
    
    /**
     Main event loop.
     Each iteration: render current state, handle input.
     */
    private void mainLoop() throws IOException {
        while (running) {
            renderCurrentState();
            handleCurrentStateInput();
        }
    }
    
    //rendering
    private void renderCurrentState() throws IOException {
        screen.clear();
        
        switch (stateManager.getCurrentState()) {
            case QUEUE:
                queueView.render(
                    stateManager.getQueuedItems(),
                    stateManager.getSelectedIndex(),
                    stateManager.getProcessedCount()
                );
                break;
            
            case EVALUATING:
                evaluatingView.render(stateManager.getCurrentEntry());
                break;
            
            case RESULT:
                resultView.render(
                    stateManager.getCurrentEntry(),
                    stateManager.getCurrentResult()
                );
                break;
            
            case CONFIRMATION:
                confirmationView.render(
                    stateManager.getCurrentEntry(),
                    stateManager.getCurrentResult()
                );
                break;
        }
        
        screen.refresh();
    }
    //input handling
    //input on current state
    private void handleCurrentStateInput() throws IOException {
        switch (stateManager.getCurrentState()) {
            case QUEUE:
                handleQueueInput();
                break;
            
            case EVALUATING:
                handleEvaluatingInput();
                break;
            
            case RESULT:
                handleResultInput();
                break;
            
            case CONFIRMATION:
                handleConfirmationInput();
                break;
        }
    }
    
    //input in queue state
    private void handleQueueInput() throws IOException {
        KeyStroke key = screen.readInput();
        if (key == null) return;
        
        switch (key.getKeyType()) {
            case ArrowUp:
                stateManager.moveSelectionUp();
                break;
            
            case ArrowDown:
                stateManager.moveSelectionDown();
                break;
            
            case Enter:
                if (stateManager.hasQueuedItems()) {
                    stateManager.startEvaluation();
                }
                break;
            
            case Character:
                handleQueueCharacterInput(key.getCharacter());
                break;
            
            default:
                break;
        }
    }
    
   //char in queue state
    private void handleQueueCharacterInput(char c) throws IOException {
        switch (Character.toLowerCase(c)) {
            case 'q':
                running = false;
                break;
            
            case 'l':
                openLogViewer();
                break;
            case 'a':
                openAddItemScreen();
        }
    }
    
    //input in evaluation state
    private void handleEvaluatingInput() throws IOException {
        //processing time eff
        sleep(1500);
        //run eval
        stateManager.runEvaluation();
        //transition -> result
        stateManager.transitionTo(DSSStateManager.EvaluationState.RESULT);
    }
    
    private void handleResultInput() throws IOException {
        KeyStroke key = screen.readInput();
        if (key == null) return;
        
        switch (key.getKeyType()) {
            case Enter:
                confirmDecision();
                break;
            
            case Escape:
                stateManager.transitionTo(DSSStateManager.EvaluationState.QUEUE);
                break;
            
            case Character:
                handleResultCharacterInput(key.getCharacter());
                break;
            
            default:
                break;
        }
    }
    
    //char input in result state
    private void handleResultCharacterInput(char c) {
        switch (Character.toLowerCase(c)) {
            case 'o':
                // TODO:Override functionality
                break;
            
            case 'd':
                // TODO:Detailed view
                break;
        }
    }
    
    private void handleConfirmationInput() throws IOException {
        sleep(2000);
        stateManager.transitionTo(DSSStateManager.EvaluationState.QUEUE);
    }
    
    //actions
    private void confirmDecision() {
        stateManager.logDecision();
        stateManager.transitionTo(DSSStateManager.EvaluationState.CONFIRMATION);
    }
    
    private void openLogViewer() throws IOException {
        ViewLogScreen logScreen = new ViewLogScreen(itemService);
        logScreen.show();
        
        // Refresh queue when returning
        stateManager.refreshQueue();
    }
    private void openAddItemScreen() throws IOException {
        AddItemScreen addScreen = new AddItemScreen(screen, itemService);
        boolean saved = addScreen.show();
        
        if (saved) {
            // Refresh queue to show new item status
            stateManager.refreshQueue();
        }
    }
    
    //utils
    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}