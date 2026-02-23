//DSSQiewView.java
/**
 Renders the QUEUE state. 
  Shows all pending HELD items awaiting evaluation.
  Displays selection indicator, item summary, and queue statistics. .
 */
package ui.dss;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.Screen;
import model.Item;
import storage.ItemLog;
import ui.UIHelpers;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DSSQueueView {
    
    private static final DateTimeFormatter TIME_FORMAT = 
        DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private final Screen screen;
    
    public DSSQueueView(Screen screen) {
        this.screen = screen;
    }
    
    /**
     Renders the complete queue view.
      
     @param queuedItems list of items in queue
     @param selectedIndex currently selected item index
     @param processedCount number of items processed today
     */
    public void render(List<ItemLog.ItemEntry> queuedItems, int selectedIndex, long processedCount) {
        renderHeader();
        renderQueueStatus(queuedItems.size());
        
        if (queuedItems.isEmpty()) {
            renderEmptyQueue();
        } else {
            renderItemList(queuedItems, selectedIndex);
            renderStatistics(queuedItems.size(), processedCount);
        }
        
        renderFooter(queuedItems.isEmpty());
    }
    
    /**
     Draws the header bar.
     */
    private void renderHeader() {
        DSSVisualHelpers.drawBorderTop(screen, 0);
        
        String title = "DSS ENGINE - QUEUE";
        int titleX = UIHelpers.centerTextX(title, DSSVisualHelpers.SCREEN_WIDTH);
        UIHelpers.writeText(screen, titleX, 0, title, 
            TextColor.ANSI.WHITE_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        
        DSSVisualHelpers.drawBorderTop(screen, 1);
    }
    
    /**
     Renders queue status line.
     */
    private void renderQueueStatus(int queueSize) {
        String status = String.format("PENDING EVALUATIONS: %d items", queueSize);
        UIHelpers.writeText(screen, 2, 3, status, 
            TextColor.ANSI.CYAN_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
   //Renders msg when queue is empty
    private void renderEmptyQueue() {
        UIHelpers.writeText(screen, 2, 5, 
            "No items in queue. All HELD items have been evaluated.",
            TextColor.ANSI.YELLOW, DSSVisualHelpers.SCREEN_WIDTH);
        
        UIHelpers.writeText(screen, 2, 7, 
            "Press [Q] to return to main menu.",
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
    //Renders queue item list
    private void renderItemList(List<ItemLog.ItemEntry> queuedItems, int selectedIndex) {
        int startRow = 5;
        int maxVisible = DSSVisualHelpers.SCREEN_HEIGHT - 10;
        
        for (int i = 0; i < Math.min(queuedItems.size(), maxVisible); i++) {
            renderQueueItem(queuedItems.get(i), i, selectedIndex, startRow + i);
        }
    }
    
   //Renders individual queued items
    private void renderQueueItem(ItemLog.ItemEntry entry, int index, int selectedIndex, int row) {
        Item item = entry.getItem();
        
        String indicator = (index == selectedIndex) ? "►" : " ";
        String line = String.format("%s %d. [ID:%-3d] %-25s %s",
            indicator,
            index + 1,
            entry.getId(),
            UIHelpers.truncate(item.getItemName(), 25),
            item.getTimestamp().format(TIME_FORMAT)
        );
        
        TextColor color = (index == selectedIndex) 
            ? TextColor.ANSI.YELLOW_BRIGHT 
            : TextColor.ANSI.WHITE;
        
        UIHelpers.writeText(screen, 2, row, line, color, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
    //Renders statistical summary
    private void renderStatistics(int queueSize, long processedCount) {
        int summaryRow = DSSVisualHelpers.SCREEN_HEIGHT - 5;
        
        DSSVisualHelpers.drawBorderMiddle(screen, summaryRow);
        
        UIHelpers.writeText(screen, 2, summaryRow + 1,
            String.format("Recently Processed: %d today", processedCount),
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
   //footer
    private void renderFooter(boolean queueEmpty) {
        DSSVisualHelpers.drawBorderTop(screen, DSSVisualHelpers.SCREEN_HEIGHT - 2);
        
        String controls = queueEmpty 
            ? "[Q: Quit]"
            : "[↑↓: Navigate] [ENTER: Evaluate Selected] [L: View Log] [S: View Students] [A: Add Item] [Q: Quit]";
        
        UIHelpers.writeText(screen, 2, DSSVisualHelpers.SCREEN_HEIGHT - 1, 
            controls, TextColor.ANSI.CYAN, DSSVisualHelpers.SCREEN_WIDTH);
    }
}