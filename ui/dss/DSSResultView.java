//DSSResultView.java
/**
 Coordinates rendering of the RESULT state. 
  Renders the ff:
 - Hard policy violations → DSSHardPolicyRenderer
 - Plastic policy scoring → DSSRiskScoreRenderer
 - Allowed items → DSSAllowedRenderer
 
 */
package ui.dss;

import com.googlecode.lanterna.screen.Screen;
import engine.DecisionResult;
import storage.ItemLog;

public class DSSResultView {
    
    private final Screen screen;
    private final DSSHardPolicyRenderer hardPolicyRenderer;
    private final DSSRiskScoreRenderer riskScoreRenderer;
    private final DSSAllowedRenderer allowedRenderer;
    
    public DSSResultView(Screen screen) {
        this.screen = screen;
        this.hardPolicyRenderer = new DSSHardPolicyRenderer(screen);
        this.riskScoreRenderer = new DSSRiskScoreRenderer(screen);
        this.allowedRenderer = new DSSAllowedRenderer(screen);
    }
    
    public void render(ItemLog.ItemEntry entry, DecisionResult result) {
        if (result == null || entry == null) {
            return;
        }
        
        if (result.isHardPolicyViolation()) {
            hardPolicyRenderer.render(entry, result);
        } else if (result.hasRiskScore()) {
            riskScoreRenderer.render(entry, result);
        } else {
            allowedRenderer.render(entry, result);
        }
    }
}