//DSSRiskScoreRenderer.java
/**
 Renders risk scoring results for plastic items.
 
  Uses dual-pane layout:
 - Left: Item profile details
 - Right: Risk breakdown with visual bars
 - Bottom: Decision summary and recommendations
 */
package ui.dss;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.Screen;
import engine.DecisionResult;
import engine.RiskBreakdown;
import engine.RiskFactor;
import enums.Decision;
import model.Item;
import storage.ItemLog;
import ui.UIHelpers;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DSSRiskScoreRenderer {
    
    private static final DateTimeFormatter TIME_FORMAT = 
        DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private final Screen screen;
    
    public DSSRiskScoreRenderer(Screen screen) {
        this.screen = screen;
    }
    
   //Renders risk scoring layout
    public void render(ItemLog.ItemEntry entry, DecisionResult result) {
        renderHeader();
        renderLeftPanel(entry);
        renderVerticalSeparator();
        renderRightPanel(result);
        renderDecisionSummary(result);
        renderFooter();
    }
    
    private void renderHeader() {
        DSSVisualHelpers.drawBorderTop(screen, 0);
        
        String title = "DSS ENGINE - RISK ANALYSIS";
        int titleX = UIHelpers.centerTextX(title, DSSVisualHelpers.SCREEN_WIDTH);
        UIHelpers.writeText(screen, titleX, 0, title, 
            TextColor.ANSI.WHITE_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        
        DSSVisualHelpers.drawBorderMiddle(screen, 1);
    }
    
   //Renders left panel
    private void renderLeftPanel(ItemLog.ItemEntry entry) {
        Item item = entry.getItem();
        int row = 3;
        
        UIHelpers.writeText(screen, 2, row++, "ITEM PROFILE", 
            TextColor.ANSI.CYAN_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        DSSVisualHelpers.drawBorderMiddle(screen, row++);
        row++;
        
        // Item name and ID
        UIHelpers.writeText(screen, 2, row++,
            String.format("%-25s #%d", 
                UIHelpers.truncate(item.getItemName(), 25), 
                entry.getId()),
            TextColor.ANSI.WHITE_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        row++;
        
        // Item attributes
        renderAttribute(2, row++, "Brand:", 
            item.getBrand() != null ? item.getBrand() : "Generic");
        renderAttribute(2, row++, "Category:", 
            item.getPrimaryCategory().toString());
        renderAttribute(2, row++, "Function:", 
            item.getFunction().toString());
        renderAttribute(2, row++, "Context:", 
            item.getContext().toString());
        renderAttribute(2, row++, "Quantity:", 
            String.valueOf(item.getQuantity()));
        row++;
        
        String studentInfo = item.getStudentId() != null ? item.getStudentId() : "N/A";
        renderAttribute(2, row++, "Student:", studentInfo);
        renderAttribute(2, row, "Time:", 
            item.getTimestamp().format(TIME_FORMAT));
    }
    
    private void renderAttribute(int x, int y, String label, String value) {
        UIHelpers.writeText(screen, x, y, 
            String.format("%-14s%s", label, value),
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
    //vertical separator for left/right panels
    private void renderVerticalSeparator() {
        DSSVisualHelpers.drawVerticalSeparator(screen, 
            DSSVisualHelpers.LEFT_PANEL_WIDTH, 
            2, 
            DSSVisualHelpers.SCREEN_HEIGHT - 2);
    }
    
    //Renders right panel
    private void renderRightPanel(DecisionResult result) {
        if (!result.hasRiskScore()) {
            return;
        }
        
        RiskBreakdown breakdown = result.getBreakdown();
        int startX = DSSVisualHelpers.LEFT_PANEL_WIDTH + 2;
        int row = 3;
        
        UIHelpers.writeText(screen, startX, row++, "RISK BREAKDOWN",
            TextColor.ANSI.CYAN_BRIGHT, DSSVisualHelpers.SCREEN_WIDTH);
        DSSVisualHelpers.drawBorderMiddle(screen, row++);
        row++;
        
        // Overall score
        renderOverallScore(startX, row, result.getDecision(), breakdown.getTotalScore());
        row += 3;
        
        // Individual factors
        renderRiskFactors(startX, row, breakdown);
    }
    
    //visual score bar
    private void renderOverallScore(int x, int y, Decision decision, int score) {
        String scoreBar = DSSVisualHelpers.generateScoreBar(score, 100);
        String decisionEmoji = DSSVisualHelpers.getDecisionEmoji(decision);
        
        UIHelpers.writeText(screen, x, y,
            String.format("Score: %d/100  %s %s", score, decisionEmoji, decision),
            DSSVisualHelpers.getDecisionColor(decision), DSSVisualHelpers.SCREEN_WIDTH);
        
        UIHelpers.writeText(screen, x, y + 1, scoreBar,
            TextColor.ANSI.YELLOW, DSSVisualHelpers.SCREEN_WIDTH);
    }
    
    private void renderRiskFactors(int x, int startY, RiskBreakdown breakdown) {
        List<RiskFactor> topFactors = breakdown.getTopContributors(4);
        int row = startY;
        
        for (RiskFactor factor : topFactors) {
            String factorBar = DSSVisualHelpers.generateScoreBar(
                Math.abs(factor.getContribution()), 35);
            
            UIHelpers.writeText(screen, x, row++,
                String.format("%-20s", factor.getFactorName() + ":"),
                TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
            
            UIHelpers.writeText(screen, x, row++,
                String.format("%s %+d", factorBar, factor.getContribution()),
                TextColor.ANSI.YELLOW, DSSVisualHelpers.SCREEN_WIDTH);
            
            // Add description for transparency
            UIHelpers.writeText(screen, x, row++,
                String.format("  → %s", factor.getDescription()),
                TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
        }
    }
    
    //Renders decision summary
    private void renderDecisionSummary(DecisionResult result) {
        
        
        int row = DSSVisualHelpers.SCREEN_HEIGHT - 10;
        Decision decision = result.getDecision();
        
        // Decision line
        String decisionEmoji = DSSVisualHelpers.getDecisionEmoji(decision);
        UIHelpers.writeText(screen, 2, row++,
            String.format("DECISION: %s %s", decisionEmoji, decision),
            DSSVisualHelpers.getDecisionColor(decision), DSSVisualHelpers.SCREEN_WIDTH);
        
        // Reason
        UIHelpers.writeText(screen, 2, row++, result.getReason(),
            TextColor.ANSI.WHITE, DSSVisualHelpers.SCREEN_WIDTH);
        row++; // blank line
        
        // Recommended action
        String actionText = result.getActionRecommendation();
        if (actionText != null && !actionText.trim().isEmpty()) {
            String[] actionLines = actionText.split("\n");
           
            int linesToShow = actionLines.length;
            
            for (int i = 0; i < linesToShow; i++) {
                String line = actionLines[i].trim();
                    // First line is the header in cyan
                    TextColor color = (i == 0) ? TextColor.ANSI.CYAN_BRIGHT : TextColor.ANSI.WHITE;
                    UIHelpers.writeText(screen, 2, row++, line, color, DSSVisualHelpers.SCREEN_WIDTH);
                
            }
        }
    }
    
    private void renderFooter() {
        DSSVisualHelpers.drawBorderTop(screen, DSSVisualHelpers.SCREEN_HEIGHT - 2);
        UIHelpers.writeText(screen, 2, DSSVisualHelpers.SCREEN_HEIGHT - 1,
            "[ENTER: Accept Decision] [O: Override] [D: View Details] [ESC: Cancel]",
            TextColor.ANSI.CYAN, DSSVisualHelpers.SCREEN_WIDTH);
    }
}