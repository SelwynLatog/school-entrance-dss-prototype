//DSSConfirmationView.java
/**
 Renders the CONFIRMATION state. 
 Shows brief success feedback after a decision is logged.
 Displays decision summary before returning to queue. 
 This is a pure renderer with no business logic.
 */
package ui.dss;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.Screen;
import engine.DecisionResult;
import engine.ThreatLevel;
import storage.ItemLog;
import ui.UIHelpers;

public class DSSConfirmationView {
    
    private final Screen screen;
    
    public DSSConfirmationView(Screen screen) {
        this.screen = screen;
    }

    public void render(ItemLog.ItemEntry entry, DecisionResult result) {
        renderHeader();
        renderSuccessMessage(entry, result);
        renderFooter();
    }
    
    /**
     Draws the header bar.
     */
    private void renderHeader() {
        DSSVisualHelpers.drawBorderTop(screen, 0);
        
        String title = "DECISION SUPPORT SYSTEM";
        int titleX = UIHelpers.centerTextX(title, DSSVisualHelpers.SCREEN_WIDTH);
        UIHelpers.writeText(screen, titleX, 0, title, 
            TextColor.ANSI.WHITE_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        
        DSSVisualHelpers.drawBorderTop(screen, 1);
    }
    
    /**
     Renders the success message and decision summary.
     */
    private void renderSuccessMessage(ItemLog.ItemEntry entry, DecisionResult result) {
        int centerY = DSSVisualHelpers.SCREEN_HEIGHT / 2;
        
        // Success message
        String success = "[✓] Decision Logged Successfully";
        int successX = UIHelpers.centerTextX(success, DSSVisualHelpers.SCREEN_WIDTH);
        UIHelpers.writeText(screen, successX, centerY - 2, success,
            TextColor.ANSI.GREEN_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        
        // Decision details
        if (entry != null && result != null) {
            String details = buildDecisionSummary(entry, result);
            int detailsX = UIHelpers.centerTextX(details, DSSVisualHelpers.SCREEN_WIDTH);
            UIHelpers.writeText(screen, detailsX, centerY, details,
                TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
        }
        
        // Return message
        String returning = "Returning to queue...";
        int returnX = UIHelpers.centerTextX(returning, DSSVisualHelpers.SCREEN_WIDTH);
        UIHelpers.writeText(screen, returnX, centerY + 2, returning,
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
    /**
     Builds the decision summary text.
     */
    private String buildDecisionSummary(ItemLog.ItemEntry entry, DecisionResult result) {
        String threatInfo = result.getThreatLevel() != ThreatLevel.NONE 
            ? result.getThreatLevel().toString() 
            : "No Threat";
        
        return String.format("Item #%d: %s (%s)",
            entry.getId(),
            result.getDecision(),
            threatInfo
        );
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