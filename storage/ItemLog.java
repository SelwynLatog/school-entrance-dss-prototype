package storage;

import model.Item;
import enums.PrimaryCategory;
import enums.ItemStatus;
import java.util.*;
import java.util.function.Predicate;

/**
 * In-memory storage for Items in the school entrance DSS.
 * Acts as a virtual log book with sequential entry IDs.
 * 
 * Responsibilities:
 * - Store and retrieve Item objects
 * - Assign unique sequential IDs (like physical log book entries)
 * - Provide search and analytics capabilities
 * - Manage item state updates
 */
public class ItemLog {
    
    /**
     * Simple container class to hold an Item with its assigned ID.
     * Used for returning items from the log with their IDs intact.
     */
    public static class ItemEntry {
        private final int id;
        private final Item item;
        
        public ItemEntry(int id, Item item) {
            this.id = id;
            this.item = item;
        }
        
        public int getId() {
            return id;
        }
        
        public Item getItem() {
            return item;
        }
    }
    
    private final Map<Integer, Item> itemsById;
    private int nextId;  // Starts at 1 to match physical log book conventions
    
    /**
     * Initializes an empty ItemLog with ID counter starting at 1.
     */
    public ItemLog() {
        this.itemsById = new HashMap<>();
        this.nextId = 1;
    }
    
    public int addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        int assignedId = nextId;
        itemsById.put(assignedId, item);
        nextId++;
        return assignedId;
    }
    
    /**
     * Removes an item from the log.
     * @return true if item existed and was removed, false if ID not found
     */
    public boolean removeItemById(int id) {
        return itemsById.remove(id) != null;
    }
    
    /**
     * Updates the status of an item in the log.
     * This is the proper way to modify item state - keeps mutation logic
     * centralized in the storage layer rather than scattered across services.
     * 
     * @param id the unique ID of the item to update
     * @param newStatus the new status to apply
     * @return true if item was found and updated, false if ID doesn't exist
     */
    public boolean updateItemStatus(int id, ItemStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        Item oldItem = itemsById.get(id);
        if (oldItem == null) {
            return false;
        }
        
        Item updatedItem = oldItem.withStatus(newStatus);
        itemsById.put(id, updatedItem);  // Replace old with new
        return true;
    }
    
    public Optional<Item> findItemById(int id) {
        return Optional.ofNullable(itemsById.get(id));
    }
    
    public List<Item> getAllItems() {
        return Collections.unmodifiableList(new ArrayList<>(itemsById.values()));
    }
    
    public List<ItemEntry> getAllItemsWithIds() {
        return itemsById.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new ItemEntry(entry.getKey(), entry.getValue()))
                .toList();
    }
    
    public List<Item> findItemsByCategory(PrimaryCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        return itemsById.values().stream()
                .filter(item -> item.getPrimaryCategory() == category)
                .toList();
    }
    
    public List<Item> findItemsByStatus(ItemStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        return itemsById.values().stream()
                .filter(item -> item.getStatus() == status)
                .toList();
    }
    
    public List<Item> findItemsByBrand(String brand) {
        if (brand == null) {
            throw new IllegalArgumentException("Brand cannot be null");
        }
        
        return itemsById.values().stream()
                .filter(item -> item.getBrand() != null && 
                               item.getBrand().equalsIgnoreCase(brand))
                .toList();
    }
    
    public List<Item> findItemsBy(Predicate<Item> criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("Criteria cannot be null");
        }
        
        return itemsById.values().stream()
                .filter(criteria)
                .toList();
    }
}