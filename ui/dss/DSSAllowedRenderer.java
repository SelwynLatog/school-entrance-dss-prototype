//DSSAllowedRenderer.java
/**
 Renders allowed item results.
 
 Simple pass-through layout for items that don't violate any policies.
 Shows success message and minimal details.
  
 Visual priority:
 1. Success indicator (green, prominent)
 2. Decision classification
 3. Brief reason
 */
package ui.dss;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.Screen;
import engine.DecisionResult;
import model.Item;
import storage.ItemLog;
import ui.UIHelpers;

import java.time.format.DateTimeFormatter;

public class DSSAllowedRenderer {
    
    private static final DateTimeFormatter TIME_FORMAT = 
        DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private final Screen screen;
    
    public DSSAllowedRenderer(Screen screen) {
        this.screen = screen;
    }
    
    /**
     Renders the allowed item layout.
     */
    public void render(ItemLog.ItemEntry entry, DecisionResult result) {
        renderHeader();
        renderItemHeader(entry);
        renderSuccessBox();
        renderDecisionDetails(entry, result);
        renderPermittedMessage();
        renderFooter();
    }
    
    /**
     Draws the header bar.
     */
    private void renderHeader() {
        DSSVisualHelpers.drawBorderTop(screen, 0);
        
        String title = "DSS ENGINE - EVALUATION RESULT";
        int titleX = UIHelpers.centerTextX(title, DSSVisualHelpers.SCREEN_WIDTH);
        UIHelpers.writeText(screen, titleX, 0, title, 
            TextColor.ANSI.WHITE_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        
        DSSVisualHelpers.drawBorderTop(screen, 1);
    }
    
    /**
     Renders item identification header.
     */
    private void renderItemHeader(ItemLog.ItemEntry entry) {
        Item item = entry.getItem();
        
        // Item name and ID
        String itemLine = String.format("ITEM: %s", item.getItemName());
        String idPadding = " ".repeat(Math.max(0, DSSVisualHelpers.SCREEN_WIDTH - itemLine.length() - 15));
        String fullLine = itemLine + idPadding + String.format("[ID:%d]", entry.getId());
        UIHelpers.writeText(screen, 2, 3, fullLine, 
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
        
        // Student and time
        String studentLine = "STUDENT: N/A";
        String timePadding = " ".repeat(30);
        String timeInfo = "TIME: " + item.getTimestamp().format(TIME_FORMAT);
        UIHelpers.writeText(screen, 2, 4, studentLine + timePadding + timeInfo, 
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
    /**
     Renders the success box.
     */
    private void renderSuccessBox() {
        int centerY = DSSVisualHelpers.SCREEN_HEIGHT / 2;
        int boxY = centerY - 3;
        
        DSSVisualHelpers.drawBox(screen, 2, boxY, DSSVisualHelpers.SCREEN_WIDTH - 4, 3);
        
        String success = "[✓] NO POLICY VIOLATIONS DETECTED";
        int successX = UIHelpers.centerTextX(success, DSSVisualHelpers.SCREEN_WIDTH - 4);
        UIHelpers.writeText(screen, 2 + successX, boxY + 1, success,
            TextColor.ANSI.GREEN_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
    /**
     Renders decision classification details.
     */
    private void renderDecisionDetails(ItemLog.ItemEntry entry, DecisionResult result) {
        Item item = entry.getItem();
        int centerY = DSSVisualHelpers.SCREEN_HEIGHT / 2;
        int row = centerY + 2;
        
        // Decision
        UIHelpers.writeText(screen, 2, row++,
            String.format("Decision:     ✓ %s", result.getDecision()),
            TextColor.ANSI.GREEN_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        
        // Threat level
        UIHelpers.writeText(screen, 2, row++,
            String.format("Threat Level: %s", result.getThreatLevel()),
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
        
        // Category
        UIHelpers.writeText(screen, 2, row++,
            String.format("Category:     %s > %s",
                item.getPrimaryCategory(), item.getSecondaryCategory()),
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
        row++;
        
        // Reason
        UIHelpers.writeText(screen, 2, row, result.getReason(), 
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
    /**
     Renders the permitted message.
     */
    private void renderPermittedMessage() {
        int centerY = DSSVisualHelpers.SCREEN_HEIGHT / 2;
        String permitted = "Item permitted on campus - no action required.";
        int permX = UIHelpers.centerTextX(permitted, DSSVisualHelpers.SCREEN_WIDTH);
        UIHelpers.writeText(screen, permX, centerY + 8, permitted,
            TextColor.ANSI.GREEN, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
    /**
     Renders footer with controls.
     */
    private void renderFooter() {
        DSSVisualHelpers.drawBorderTop(screen, DSSVisualHelpers.SCREEN_HEIGHT - 2);
        UIHelpers.writeText(screen, 2, DSSVisualHelpers.SCREEN_HEIGHT - 1,
            "[ENTER: Log & Continue] [ESC: Back to Queue]",
            TextColor.ANSI.CYAN, DSSVisualHelpers.SCREEN_WIDTH);
    }
}