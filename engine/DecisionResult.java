//DecisionResult.java
/*Responsibility- gives detailed insights on why the item is held

/**
 Contains the complete evaluation result for a single item. 
 DecisionResult is an immutable data container produced by DecisionEngine.
 It packages the final decision, risk analysis, threat classification, and
 administrative guidance into a single object.
  
 This is a pure data structure with no formatting or UI logic - presentation
 is handled by the UI layer.
 */
package engine;

import model.Item;
import enums.Decision;

public class DecisionResult {
    
    // Core decision data
    private final Item item;
    private final Decision decision;
    private final String reason;
    
    // Risk analysis (null if item !scored)
    private final RiskBreakdown breakdown;
    
    // Threat classification
    private final ThreatLevel threatLevel;
    private final String actionRecommendation;
    
    // Alert flag for critical items
    private final boolean requiresImmediateAlert;
    
    /**
     Creates a complete decision result.
     */
    public DecisionResult(
            Item item,
            Decision decision,
            String reason,
            RiskBreakdown breakdown,
            ThreatLevel threatLevel,
            String actionRecommendation,
            boolean requiresImmediateAlert
    ) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (decision == null) {
            throw new IllegalArgumentException("Decision cannot be null");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be null or empty");
        }
        if (threatLevel == null) {
            throw new IllegalArgumentException("Threat level cannot be null");
        }
        if (actionRecommendation == null) {
            throw new IllegalArgumentException("Action recommendation cannot be null");
        }
        
        this.item = item;
        this.decision = decision;
        this.reason = reason;
        this.breakdown = breakdown; // Allowed to be null
        this.threatLevel = threatLevel;
        this.actionRecommendation = actionRecommendation;
        this.requiresImmediateAlert = requiresImmediateAlert;
    }
    
    //GETTERS
    /**
     @return the evaluated item
     */
    public Item getItem() {
        return item;
    }
    
    /**
     @return ALLOW, CONDITIONAL, or DISALLOW
     */
    public Decision getDecision() {
        return decision;
    }
    
    /**
     @return decision reason (e.g., "Item violates plastic policy threshold")
     */
    public String getReason() {
        return reason;
    }
    
    /**
     Returns detailed risk analysis if item was scored.
     This will be null for items that:
     - Were blocked by hard policy gates (no scoring needed)
     - Were allowed without scoring (not in plastic policy scope)
      @return RiskBreakdown with scoring details, or null if not scored
     */
    public RiskBreakdown getBreakdown() {
        return breakdown;
    }
    
    /**
     Returns the threat severity classification.
     @return threat level (NONE, LOW, MEDIUM, HIGH, or CRITICAL)
     */
    public ThreatLevel getThreatLevel() {
        return threatLevel;
    }
    
    /**
     Returns administrative action recommendations.
     @return detailed guidance text for admin follow-up
     */
    public String getActionRecommendation() {
        return actionRecommendation;
    }
    
    /**
     Indicates whether this result requires immediate admin notification.
     @return true if item is CRITICAL or HIGH threat requiring urgent response
     */
    public boolean requiresImmediateAlert() {
        return requiresImmediateAlert;
    }
    
    //CONVENIENCE METHODS
    /**
     Checks if this item was scored (has risk breakdown).
     @return true if risk scoring was performed
     */
    public boolean hasRiskScore() {
        return breakdown != null;
    }
    
    /**
     Returns the risk score if available.
     @return risk score, or -1 if item was not scored
     */
    public int getRiskScore() {
        return hasRiskScore() ? breakdown.getTotalScore() : -1;
    }
    
    /**
     Checks if this result represents a hard policy violation.
     Hard policy violations have threat level > NONE and no risk score.
     @return true if blocked by hard policy gate
     */
    public boolean isHardPolicyViolation() {
        return threatLevel != ThreatLevel.NONE && !hasRiskScore();
    }
    
    /**
     Checks if this result represents a plastic policy violation. 
     Plastic policy violations have risk scores and CONDITIONAL/DISALLOW decisions.
     @return true if blocked by plastic policy scoring
     */
    public boolean isPlasticPolicyViolation() {
        return hasRiskScore() && decision != Decision.ALLOW;
    }
    
    @Override
    public String toString() {
        return String.format(
            "DecisionResult[item=%s, decision=%s, threat=%s, score=%s]",
            item.getItemName(),
            decision,
            threatLevel,
            hasRiskScore() ? breakdown.getTotalScore() : "N/A"
        );
    }
}