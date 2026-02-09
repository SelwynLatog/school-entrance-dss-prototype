/**
Contains the complete risk analysis for an evaluated item.
RiskBreakdown provides full transparency into how a risk score was calculated.
It stores all contributing factors and provides analysis methods to identify
the most impactful elements of the decision. 
This is the "receipt" that makes the DSS explainable - admins can see exactly
which factors drove the risk score up or down.
TLDR- Risk Score Explainability
 */
package engine;

import java.util.*;
import java.util.stream.Collectors;

public class RiskBreakdown {
    private final List<RiskFactor> factors;
    private final int totalScore;
    
    /**
     * Creates a new RiskBreakdown with validation.
     * 
     * @param factors list of all risk factors that contributed to the score
     * @param totalScore the sum of all factor contributions
     * @throws IllegalArgumentException if factors is null/empty or if totalScore
     *         doesn't match the sum of factor contributions
     */
    public RiskBreakdown(List<RiskFactor> factors, int totalScore) {
        if (factors == null || factors.isEmpty()) {
            throw new IllegalArgumentException("Factors list cannot be null or empty");
        }
        
        // Validate that totalScore matches the sum of contributions
        int calculatedTotal = factors.stream()
                                    .mapToInt(RiskFactor::getContribution)
                                    .sum();
        if (calculatedTotal != totalScore) {
            throw new IllegalArgumentException(
                String.format("Total score mismatch: provided %d, calculated %d", 
                             totalScore, calculatedTotal)
            );
        }
        
        this.factors = new ArrayList<>(factors); // Defensive copy
        this.totalScore = totalScore;
    }
    
    /**
     * Returns all risk factors in this breakdown.
     * 
     * @return unmodifiable list of all factors
     */
    public List<RiskFactor> getAllFactors() {
        return Collections.unmodifiableList(factors);
    }
    
    /**
     * Returns the total risk score.
     * 
     * @return sum of all factor contributions
     */
    public int getTotalScore() {
        return totalScore;
    }
    
    /**
     * Returns all factors that increased the risk score.
     * 
     * @return list of factors with positive contributions, sorted by impact (highest first)
     */
    public List<RiskFactor> getPositiveContributors() {
        return factors.stream()
                     .filter(RiskFactor::isPositiveContribution)
                     .sorted(Comparator.comparingInt(RiskFactor::getContribution).reversed())
                     .collect(Collectors.toList());
    }
    
    /**
     * Returns all factors that decreased the risk score.
     * 
     * @return list of factors with negative contributions, sorted by absolute impact (largest first)
     */
    public List<RiskFactor> getNegativeContributors() {
        return factors.stream()
                     .filter(RiskFactor::isNegativeContribution)
                     .sorted(Comparator.comparingInt(RiskFactor::getAbsoluteImpact).reversed())
                     .collect(Collectors.toList());
    }
    
    /**
     * Returns the top N factors that increased risk.
     * Useful for highlighting primary drivers in explanations.
     * 
     * @param n number of top contributors to return
     * @return list of top N positive contributors (or all if fewer than N exist)
     */
    public List<RiskFactor> getTopContributors(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("N must be positive, got: " + n);
        }
        
        return getPositiveContributors().stream()
                                       .limit(n)
                                       .collect(Collectors.toList());
    }
    
    public Optional<RiskFactor> getLargestMitigatingFactor() {
        return getNegativeContributors().stream()
                                       .findFirst(); // Already sorted by absolute impact
    }
    
    /**
      Generates a human-readable explanation of the risk score.
      The explanation includes:
        - Total score and decision context
        - Top 2-3 positive contributors with descriptions
        - Largest mitigating factor (if any)
        - Summary statement 
          @return formatted explanation text for admin review
     */
    public String generateExplanation() {
        StringBuilder explanation = new StringBuilder();
        
        // Header with total score
        explanation.append(String.format("Risk Score: %d\n\n", totalScore));
        
        // Positive contributors section
        List<RiskFactor> topPositive = getTopContributors(3);
        if (!topPositive.isEmpty()) {
            explanation.append("Primary Risk Drivers:\n");
            for (RiskFactor factor : topPositive) {
                explanation.append(String.format("  • %s (%s): %+d\n",
                    factor.getFactorName(),
                    factor.getFactorValue(),
                    factor.getContribution()
                ));
                explanation.append(String.format("    → %s\n",
                    factor.getDescription()
                ));
            }
            explanation.append("\n");
        }
        
        // Mitigating factors section
        Optional<RiskFactor> mitigator = getLargestMitigatingFactor();
        if (mitigator.isPresent()) {
            RiskFactor factor = mitigator.get();
            explanation.append("Mitigating Factor:\n");
            explanation.append(String.format("  • %s (%s): %+d\n",
                factor.getFactorName(),
                factor.getFactorValue(),
                factor.getContribution()
            ));
            explanation.append(String.format("    → %s\n\n",
                factor.getDescription()
            ));
        } else {
            explanation.append("No mitigating factors identified.\n\n");
        }
        
        // Summary based on score range
        if (totalScore >= 71) {
            explanation.append("The item significantly exceeds the policy threshold and should be disallowed.");
        } else if (totalScore >= 31) {
            explanation.append("The item shows moderate policy concern and requires conditional review.");
        } else {
            explanation.append("The item falls within acceptable risk parameters.");
        }
        
        return explanation.toString();
    }
    
    @Override
    public String toString() {
        return String.format("RiskBreakdown[totalScore=%d, factors=%d]",
                           totalScore,
                           factors.size());
    }
}