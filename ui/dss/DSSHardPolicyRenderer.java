//DSSHardPolicyRenderer.java
/**
 Renders hard policy violation results. 
 Used for dangerous/prohibited items (weapons, vaping, alcohol).
 Layout emphasizes threat level and required actions.

  Visual priority:
  1. Alert box (red, prominent)
  2. Threat classification
  3. Required actions
  4. Item details (secondary)
 */
package ui.dss;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.Screen;
import engine.DecisionResult;
import engine.ThreatLevel;
import model.Item;
import storage.ItemLog;
import ui.UIHelpers;

import java.time.format.DateTimeFormatter;

public class DSSHardPolicyRenderer {
    
    private static final DateTimeFormatter TIME_FORMAT = 
        DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private final Screen screen;
    
    public DSSHardPolicyRenderer(Screen screen) {
        this.screen = screen;
    }
    
    /**
     Renders the hard policy violation layout.
     */
    public void render(ItemLog.ItemEntry entry, DecisionResult result) {
        renderHeader();
        renderItemHeader(entry);
        renderAlertBox();
        renderDecisionDetails(entry, result);
        renderActionBox(result);
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
        String studentInfo = item.getStudentId() != null ? item.getStudentId() : "N/A";
        String studentLine = "STUDENT: " + studentInfo;
        String timePadding = " ".repeat(30);
        String timeInfo = "TIME: " + item.getTimestamp().format(TIME_FORMAT);
        UIHelpers.writeText(screen, 2, 4, studentLine + timePadding + timeInfo, 
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
    /**
     Renders the prominent alert box.
     */
    private void renderAlertBox() {
        int boxY = 6;
        DSSVisualHelpers.drawBox(screen, 2, boxY, DSSVisualHelpers.SCREEN_WIDTH - 4, 3);
        
        String alert = "HARD POLICY VIOLATION - IMMEDIATE ACTION REQUIRED";
        int alertX = UIHelpers.centerTextX(alert, DSSVisualHelpers.SCREEN_WIDTH - 4);
        UIHelpers.writeText(screen, 2 + alertX, boxY + 1, alert, 
            TextColor.ANSI.RED_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
    /**
     Renders decision classification details.
     */
    private void renderDecisionDetails(ItemLog.ItemEntry entry, DecisionResult result) {
        Item item = entry.getItem();
        int row = 9;
        // Decision
        String decisionEmoji = DSSVisualHelpers.getThreatEmoji(result.getThreatLevel());
        UIHelpers.writeText(screen, 2, row++,
            String.format("Decision:     %s %s", decisionEmoji, result.getDecision()),
            TextColor.ANSI.RED_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        
        // Threat level
        UIHelpers.writeText(screen, 2, row++,
            String.format("Threat Level: %s %s", decisionEmoji, result.getThreatLevel()),
            DSSVisualHelpers.getThreatColor(result.getThreatLevel()), 
            DSSVisualHelpers.SCREEN_WIDTH);
        
        // Category
        UIHelpers.writeText(screen, 2, row++,
            String.format("Category:     %s > %s",
                item.getPrimaryCategory(),
                item.getSecondaryCategory()),
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
        row++;
        
        // Reason - make more compact
        UIHelpers.writeText(screen, 2, row, "Reason: " + result.getReason(), 
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
    }
    /**
     Renders the required actions box.
     */
    private void renderActionBox(DecisionResult result) {
        int availableHeight = DSSVisualHelpers.SCREEN_HEIGHT - 17; 
        int boxHeight = Math.min(12, availableHeight); // Dynamic height
        int boxY = DSSVisualHelpers.SCREEN_HEIGHT - boxHeight - 3; // Position from bottom
        
        DSSVisualHelpers.drawBox(screen, 2, boxY, DSSVisualHelpers.SCREEN_WIDTH - 4, boxHeight);
        
        UIHelpers.writeText(screen, 4, boxY + 1, "REQUIRED ACTIONS:",
            TextColor.ANSI.CYAN_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        
        String[] actionLines = result.getActionRecommendation().split("\n");
        int row = boxY + 3;
        int maxLines = boxHeight - 4;
        
        for (int i = 0; i < Math.min(actionLines.length, maxLines); i++) {
            String line = actionLines[i].trim();
            if (!line.isEmpty()) {
                UIHelpers.writeText(screen, 4, row++, line,
                    TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH - 8);
            }
        }
    }
    
    /**
     Renders footer with controls.
     */
    private void renderFooter() {
        DSSVisualHelpers.drawBorderTop(screen, DSSVisualHelpers.SCREEN_HEIGHT - 2);
        UIHelpers.writeText(screen, 2, DSSVisualHelpers.SCREEN_HEIGHT - 1,
            "[ENTER: Confirm & Log Decision] [O: Override] [ESC: Back to Queue]",
            TextColor.ANSI.CYAN, DSSVisualHelpers.SCREEN_WIDTH);
    }
}