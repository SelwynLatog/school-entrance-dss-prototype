//DSSEvaluatingView.java
/**
 Renders the EVALUATING state. 
 Shows a brief processing animation while the decision engine runs.
 Displays item name and a progress indicator for user feedback. 
 This is a pure renderer with no business logic.
 */
package ui.dss;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.Screen;
import model.Item;
import storage.ItemLog;
import ui.UIHelpers;

public class DSSEvaluatingView {
    
    private final Screen screen;
    
    public DSSEvaluatingView(Screen screen) {
        this.screen = screen;
    }
    
    public void render(ItemLog.ItemEntry entry) {
        if (entry == null) {
            return;
        }
        
        renderHeader();
        renderProcessingMessage(entry);
        renderFooter();
    }
    
    /**
      Draws the header bar.
     */
    private void renderHeader() {
        DSSVisualHelpers.drawBorderTop(screen, 0);
        
        String title = "DSS ENGINE";
        int titleX = UIHelpers.centerTextX(title, DSSVisualHelpers.SCREEN_WIDTH);
        UIHelpers.writeText(screen, titleX, 0, title, 
            TextColor.ANSI.WHITE_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        
        DSSVisualHelpers.drawBorderTop(screen, 1);
    }
    
    /**
      Renders the processing animation and item info.
     */
    private void renderProcessingMessage(ItemLog.ItemEntry entry) {
        Item item = entry.getItem();
        int centerY = DSSVisualHelpers.SCREEN_HEIGHT / 2;
        
        // Processing message
        String processing = "⟳ Evaluating Item #" + entry.getId() + "...";
        int procX = UIHelpers.centerTextX(processing, DSSVisualHelpers.SCREEN_WIDTH);
        UIHelpers.writeText(screen, procX, centerY - 2, processing, 
            TextColor.ANSI.CYAN_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        
        // Item name with brand if available
        String itemName = item.getItemName();
        if (item.getBrand() != null && !item.getBrand().isEmpty()) {
            itemName += " (" + item.getBrand() + ")";
        }
        int nameX = UIHelpers.centerTextX(itemName, DSSVisualHelpers.SCREEN_WIDTH);
        UIHelpers.writeText(screen, nameX, centerY, itemName, 
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
        
        // Progress bar
        String progress = "▓▓▓▓▓▓░░░░░░░░░░░░";
        int progX = UIHelpers.centerTextX(progress, DSSVisualHelpers.SCREEN_WIDTH);
        UIHelpers.writeText(screen, progX, centerY + 2, progress, 
            TextColor.ANSI.YELLOW, DSSVisualHelpers.SCREEN_WIDTH);
        
        // Status text
        String status = "Running policy checks...";
        int statusX = UIHelpers.centerTextX(status, DSSVisualHelpers.SCREEN_WIDTH);
        UIHelpers.writeText(screen, statusX, centerY + 4, status, 
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
    /**
      Renders footer.
     */
    private void renderFooter() {
        DSSVisualHelpers.drawBorderTop(screen, DSSVisualHelpers.SCREEN_HEIGHT - 2);
        UIHelpers.writeText(screen, 2, DSSVisualHelpers.SCREEN_HEIGHT - 1, 
            "Please wait...", TextColor.ANSI.CYAN, DSSVisualHelpers.SCREEN_WIDTH);
    }
}