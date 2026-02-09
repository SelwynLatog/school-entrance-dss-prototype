/*
 Represents a single factor that contributes to the total risk score of an item. 
 RiskFactor is generated from RiskFactor.jav
 Each RiskFactor captures four things:
 1. What aspect was evaluated (factorName: "Usage Type")
 2. What value was found (factorValue: "SINGLE_USE")
 3. How much it affected the score (contribution: +35)
 4. Why it matters (description: "Item is designed for single-use and disposal")
 Sample:
 RiskFactor factor = new RiskFactor(
 "Usage Type",
 "SINGLE_USE",
 35,
 "Item is designed for single-use and disposal"
 );
 */
package engine;

public class RiskFactor {
    private final String factorName;
    private final String factorValue;
    private final int contribution;
    private final String description;
    
    public RiskFactor(String factorName, String factorValue, int contribution, String description) {
        if (factorName == null || factorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Factor name cannot be null or empty");
        }
        if (factorValue == null || factorValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Factor value cannot be null or empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        
        this.factorName = factorName;
        this.factorValue = factorValue;
        this.contribution = contribution;
        this.description = description;
    }
    
    public String getFactorName() {
        return factorName;
    }
    
    public String getFactorValue() {
        return factorValue;
    }
    
    public int getContribution() {
        return contribution;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     @return true if contribution is positive, false otherwise
     */
    public boolean isPositiveContribution() {
        return contribution > 0;
    }
    
    /**
     * Checks if this factor decreased the risk score.
     * 
     * @return true if contribution is negative, false otherwise
     */
    public boolean isNegativeContribution() {
        return contribution < 0;
    }
    
    /**
     @return absolute value of contribution
     */
    public int getAbsoluteImpact() {
        return Math.abs(contribution);
    }
    
    @Override
    public String toString() {
        return String.format("[%s: %s] %s%d - %s",
            factorName,
            factorValue,
            (contribution >= 0 ? "+" : ""),
            contribution,
            description
        );
    }
}