//DecisionEngine.java
/**
  Main orchestrator for the Decision Support System.

  Evaluation Pipeline:
  1. Hard policy gate check (weapons, alcohol, etc.)
  2. Scope check (is it plastic?)
  3. Risk evaluation (if plastic)
  4. Decision band mapping
  5. Result assembly 
  This is the only class that UI and Service layers interact with directly.
 */
package engine;

import model.Item;
import enums.PrimaryCategory;
import java.util.Optional;
import enums.Decision;

public class DecisionEngine {
    
    // Decision thresholds from rubric
    private static final int ALLOW_THRESHOLD = 30;
    private static final int CONDITIONAL_THRESHOLD = 70;
    
    /**
     Evaluates an item through the complete decision pipeline.
     
     This method orchestrates all evaluation steps and produces a comprehensive
     DecisionResult containing the decision, risk analysis (if applicable),
     threat classification, and administrative recommendations.
     
      The evaluation process:
      1. Checks hard policy violations (immediate disallow)
      2. Checks if item is within plastic policy scope
      3. Performs risk scoring for plastic items
      4. Maps score to decision band
      5. Assembles complete result with admin guidance
     */
    public static DecisionResult evaluate(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        //STEP 1: HARD POLICY GATE
        Optional<String> policyViolation = PolicyGate.checkHardPolicy(item);
        
        if (policyViolation.isPresent()) {
            // Item violates hard policy - immediate disallow
            return handleHardPolicyViolation(item, policyViolation.get());
        }
        
        //STEP 2: SCOPE CHECK
        if (item.getPrimaryCategory() != PrimaryCategory.SINGLE_USE_PLASTIC) {
            // Item not within scope of plastic policy - allow
            return handleOutOfScopeItem(item);
        }
        
        //STEP 3: RISK EVALUATION
        // Item is plastic and passed hard policy - evaluate risk
        RiskBreakdown breakdown = RiskEvaluator.evaluate(item);
        int riskScore = breakdown.getTotalScore();
        
        //STEP 4: DECISION BAND MAPPING
        Decision decision = mapScoreToDecision(riskScore);
        String reason = generatePlasticPolicyReason(decision, riskScore);
        
        //STEP 5: ASSEMBLE RESULT WITH PLASTIC POLICY ACTIONS
        String actionRec = ActionResolver.getPlasticPolicyAction(decision, riskScore);
        
        return new DecisionResult(
            item,
            decision,
            reason,
            breakdown,
            ThreatLevel.NONE,  // Plastic policy violations have no threat level
            actionRec,         // Now uses plastic-specific recommendations
            false  // Plastic policy violations don't trigger alerts
        );
    }
    
    //HELPER METHODS
    
    /**
     Handles items that violate hard campus policies.
      
     Creates a DecisionResult for items blocked by PolicyGate (weapons,
     * alcohol, tobacco, etc.). These items get threat classification and
     appropriate admin recommendations.
     */
    private static DecisionResult handleHardPolicyViolation(Item item, String violationReason) {
        ThreatLevel threat = ThreatClassifier.classify(item);
        String actionRec = ActionResolver.getActionRecommendation(threat, item);
        boolean requiresAlert = ActionResolver.requiresImmediateAlert(threat);
        
        return new DecisionResult(
            item,
            Decision.DISALLOW,
            violationReason,
            null,  // No risk scoring for hard policy violations
            threat,
            actionRec,
            requiresAlert
        );
    }
    
    /**
     Handles items that are outside the plastic policy scope.
      
     Creates a DecisionResult for items that passed PolicyGate but aren't
     subject to plastic policy evaluation
     */
    private static DecisionResult handleOutOfScopeItem(Item item) {
        return new DecisionResult(
            item,
            Decision.ALLOW,
            "Item not within scope of plastic policy",
            null,  // No risk scoring needed
            ThreatLevel.NONE,
            "[✓] NO HARD POLICY VIOLATION\nItem permitted on campus.",
            false
        );
    }
    
    /**
     Maps a risk score to the appropriate decision band.
    
     Decision Bands:
     - Score ≤ 30: ALLOW (low risk)
     - Score 31-70: CONDITIONAL (moderate risk, policy review recommended)
     - Score ≥ 71: DISALLOW (high risk, clear violation)
     */
    private static Decision mapScoreToDecision(int score) {
        if (score <= ALLOW_THRESHOLD) {
            return Decision.ALLOW;
        } else if (score <= CONDITIONAL_THRESHOLD) {
            return Decision.CONDITIONAL;
        } else {
            return Decision.DISALLOW;
        }
    }
    
    /**
     Generates a reason string for plastic policy decisions. 
     */
    private static String generatePlasticPolicyReason(Decision decision, int score) {
        switch (decision) {
            case ALLOW:
                return String.format(
                    "Item within acceptable risk parameters (score: %d ≤ %d)",
                    score,
                    ALLOW_THRESHOLD
                );
            
            case CONDITIONAL:
                return String.format(
                    "Item shows moderate policy concern (score: %d).",
                    score
                );
            
            case DISALLOW:
                return String.format(
                    "Item violates plastic policy threshold (score: %d ≥ %d)",
                    score,
                    CONDITIONAL_THRESHOLD + 1
                );
            
            default:
                throw new IllegalStateException("Unknown decision: " + decision);
        }
    }
}