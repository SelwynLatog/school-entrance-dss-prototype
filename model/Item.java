package model;

import enums.*;
import java.time.LocalDateTime;

/**
 * Represents a physical item logged at the school entrance.
 * Immutable value object with comprehensive classification metadata.
 * 
 * Each item is fully described by its category, function, usage context,
 * and current status. Items cannot be modified after creation - use withX()
 * methods to create updated copies with changed fields.
 */
public class Item {
    private final String itemName;
    private final String brand;
    private final PrimaryCategory primaryCategory;
    private final SecondaryCategory secondaryCategory;
    private final ItemFunction function;
    private final ConsumptionContext context;
    private final UsageType usageType;
    private final Replaceability replaceability;
    private final ItemStatus status;
    private final int quantity;
    private final LocalDateTime timestamp;
    private final String studentId;
    
    public Item(
            String itemName,
            String brand,
            PrimaryCategory primaryCategory,
            SecondaryCategory secondaryCategory,
            ItemFunction function,
            ConsumptionContext context,
            UsageType usageType,
            Replaceability replaceability,
            ItemStatus status,
            int quantity,
            LocalDateTime timestamp,
            String studentId 
    ) {
        // Item validates its own integrity - fails fast with clear errors
        if (itemName == null || itemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty");
        }
        if (primaryCategory == null) {
            throw new IllegalArgumentException("Primary category cannot be null");
        }
        if (secondaryCategory == null) {
            throw new IllegalArgumentException("Secondary category cannot be null");
        }
        if (function == null) {
            throw new IllegalArgumentException("Item function cannot be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("Consumption context cannot be null");
        }
        if (usageType == null) {
            throw new IllegalArgumentException("Usage type cannot be null");
        }
        if (replaceability == null) {
            throw new IllegalArgumentException("Replaceability cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Item status cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0, got: " + quantity);
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        
        this.itemName = itemName;
        this.brand = brand;
        this.primaryCategory = primaryCategory;
        this.secondaryCategory = secondaryCategory;
        this.function = function;
        this.context = context;
        this.usageType = usageType;
        this.replaceability = replaceability;
        this.status = status;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.studentId = studentId;
    }
    
    // Getters
    public String getItemName() {
        return itemName;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public PrimaryCategory getPrimaryCategory() {
        return primaryCategory;
    }
    
    public SecondaryCategory getSecondaryCategory() {
        return secondaryCategory;
    }
    
    public ItemFunction getFunction() {
        return function;
    }
    
    public ConsumptionContext getContext() {
        return context;
    }
    
    public UsageType getUsageType() {
        return usageType;
    }
    
    public Replaceability getReplaceability() {
        return replaceability;
    }
    
    public ItemStatus getStatus() {
        return status;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    /**
     * Creates a new Item with updated status.
     * Original item remains unchanged (immutable pattern).
     * 
     * @param newStatus the new status to apply
     * @return a new Item instance with the updated status
     */
    public Item withStatus(ItemStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        return new Item(
                this.itemName,
                this.brand,
                this.primaryCategory,
                this.secondaryCategory,
                this.function,
                this.context,
                this.usageType,
                this.replaceability,
                newStatus,
                this.quantity,
                this.timestamp,
                this.studentId
        );
    }
    
    /**
     * Creates a new Item with updated quantity.
     * Useful for inventory adjustments.
     * 
     * @param newQuantity the new quantity (must be > 0)
     * @return a new Item instance with the updated quantity
     */
    public Item withQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0, got: " + newQuantity);
        }
        
        return new Item(
                this.itemName,
                this.brand,
                this.primaryCategory,
                this.secondaryCategory,
                this.function,
                this.context,
                this.usageType,
                this.replaceability,
                this.status,
                newQuantity,
                this.timestamp,
                this.studentId
        );
    }
    
    @Override
    public String toString() {
        return itemName + " [" +
                primaryCategory + " | " +
                secondaryCategory + " | " +
                function + " | " +
                context + " | " +
                usageType + " | " +
                replaceability + " | " +
                status + " | " +
                "Qty: " + quantity + " | " +
                timestamp.toLocalDate() +
                (brand != null ? " | " + brand : "") +
                (studentId != null ? " | Student: " + studentId : "") + 
                "]";
    }
}