package ui;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.input.*;
import model.Item;
import service.ItemService;
import storage.ItemLog;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * Dual-pane view for browsing the item log.
 * Left panel: Item list with navigation
 * Right panel: Detailed view of selected item
 */
public class ViewLogScreen {
    
    /**
     * Filter modes for viewing items.
     */
    private enum FilterMode {
        ALL("All Items"),
        HELD_ONLY("Held Only"),
        RELEASED_ONLY("Released Only");
        
        private final String displayName;
        
        FilterMode(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private final Screen screen;
    private final ItemService itemService;
    private List<ItemLog.ItemEntry> itemEntries;
    private List<ItemLog.ItemEntry> filteredEntries;
    private int selectedIndex;
    private boolean running;
    private FilterMode currentFilter;
    
    // Layout constants
    private static final int LEFT_PANEL_WIDTH = 50;
    private static final int SCREEN_HEIGHT = 24;
    private static final int SCREEN_WIDTH = 120;
    
    // Date formatter
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    
    public ViewLogScreen(ItemService itemService) throws IOException {
        this.itemService = itemService;
        this.selectedIndex = 0;
        this.running = false;
        this.currentFilter = FilterMode.ALL;
        
        // Initialize Lanterna terminal
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        this.screen = new TerminalScreen(terminal);
    }
    
    public void show() throws IOException {
        screen.startScreen();
        running = true;
        
        try {
            while (running) {
                // Refresh item list from service (with IDs)
                itemEntries = itemService.getAllItemsWithIds();
                
                // Apply filter
                applyFilter();
                
                // Ensure valid selection
                if (filteredEntries.isEmpty()) {
                    selectedIndex = -1;
                } else if (selectedIndex >= filteredEntries.size()) {
                    selectedIndex = filteredEntries.size() - 1;
                } else if (selectedIndex < 0) {
                    selectedIndex = 0;
                }
                
                // Render the screen
                render();
                
                // Handle input
                handleInput();
            }
        } finally {
            screen.stopScreen();
        }
    }
    
    /**
     * Applies the current filter to the item list.
     */
    private void applyFilter() {
        switch (currentFilter) {
            case HELD_ONLY:
                filteredEntries = itemEntries.stream()
                    .filter(entry -> entry.getItem().getStatus().toString().equals("HELD"))
                    .toList();
                break;
                
            case RELEASED_ONLY:
                filteredEntries = itemEntries.stream()
                    .filter(entry -> entry.getItem().getStatus().toString().equals("RELEASED"))
                    .toList();
                break;
                
            case ALL:
            default:
                filteredEntries = new ArrayList<>(itemEntries);
                break;
        }
    }
    
    private void render() throws IOException {
        screen.clear();
        
        // Draw header
        drawHeader();
        
        // Draw vertical separator
        drawVerticalSeparator();
        
        // Draw left panel (item list)
        drawItemList();
        
        // Draw right panel (item details)
        drawItemDetails();
        
        // Draw footer
        drawFooter();
        
        screen.refresh();
    }
    
    /**
     * Draws the header bar.
     */
    private void drawHeader() {
        String title = "ITEM LOG VIEWER";
        int startX = UIHelpers.centerTextX(title, SCREEN_WIDTH);
        
        // Draw top border
        for (int x = 0; x < SCREEN_WIDTH; x++) {
            screen.setCharacter(x, 0, new TextCharacter('═'));
        }
        
        // Draw title
        UIHelpers.writeText(screen, startX, 0, title, TextColor.ANSI.WHITE_BRIGHT, SCREEN_WIDTH);
    }
    
    /**
     * Draws vertical separator between panels.
     */
    private void drawVerticalSeparator() {
        for (int y = 1; y < SCREEN_HEIGHT - 2; y++) {
            screen.setCharacter(LEFT_PANEL_WIDTH, y, new TextCharacter('║'));
        }
        
        // Draw junction points
        screen.setCharacter(LEFT_PANEL_WIDTH, 1, new TextCharacter('╦'));
        screen.setCharacter(LEFT_PANEL_WIDTH, SCREEN_HEIGHT - 2, new TextCharacter('╩'));
    }
    
    /**
     * Draws the left panel with item list.
     */
    private void drawItemList() {
        UIHelpers.writeText(screen, 2, 2, "ITEM LIST", TextColor.ANSI.CYAN_BRIGHT, SCREEN_WIDTH);
        
        if (filteredEntries.isEmpty()) {
            String emptyMessage = currentFilter == FilterMode.ALL 
                ? "No items in log" 
                : "No " + currentFilter.getDisplayName().toLowerCase();
            UIHelpers.writeText(screen, 2, 4, emptyMessage, TextColor.ANSI.YELLOW, SCREEN_WIDTH);
            UIHelpers.writeText(screen, 2, 5, "Press [A] to add items", TextColor.ANSI.WHITE, SCREEN_WIDTH);
            return;
        }
        
        // Draw items (starting from row 4)
        int startRow = 4;
        int maxVisible = SCREEN_HEIGHT - 8;
        
        for (int i = 0; i < Math.min(filteredEntries.size(), maxVisible); i++) {
            ItemLog.ItemEntry entry = filteredEntries.get(i);
            Item item = entry.getItem();
            int row = startRow + i;
            
            // Selection indicator
            String indicator = (i == selectedIndex) ? "►" : " ";
            
            // Format: "► 1. [ID:1]  Vape Pen    HELD"
            String line = String.format("%s %d. [ID:%-2d] %-12s %s",
                indicator,
                i + 1,
                entry.getId(),
                UIHelpers.truncate(item.getItemName(), 12),
                item.getStatus().toString()
            );
            
            // Highlight selected item, dim released items when showing ALL
            TextColor color;
            if (i == selectedIndex) {
                color = TextColor.ANSI.YELLOW_BRIGHT;
            } else if (currentFilter == FilterMode.ALL && 
                       item.getStatus().toString().equals("RELEASED")) {
                color = TextColor.ANSI.WHITE;
            } else {
                color = TextColor.ANSI.WHITE;
            }
            
            UIHelpers.writeText(screen, 2, row, line, color, SCREEN_WIDTH);
        }
        
        // Draw summary stats
        long heldCount = itemEntries.stream()
            .filter(entry -> entry.getItem().getStatus().toString().equals("HELD"))
            .count();
        
        int summaryRow = SCREEN_HEIGHT - 5;
        UIHelpers.writeText(screen, 2, summaryRow, "─────────────────────────────", 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        UIHelpers.writeText(screen, 2, summaryRow + 1, 
            String.format("Total: %d items", itemEntries.size()), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        UIHelpers.writeText(screen, 2, summaryRow + 2, 
            String.format("Held: %d | Released: %d", heldCount, itemEntries.size() - heldCount), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
    }
    
    /**
     * Draws the right panel with detailed item information.
     */
    private void drawItemDetails() {
        int startX = LEFT_PANEL_WIDTH + 2;
        
        if (filteredEntries.isEmpty() || selectedIndex < 0) {
            UIHelpers.writeText(screen, startX, 2, "DETAILS: No item selected", 
                TextColor.ANSI.CYAN_BRIGHT, SCREEN_WIDTH);
            return;
        }
        
        ItemLog.ItemEntry entry = filteredEntries.get(selectedIndex);
        Item item = entry.getItem();
        
        UIHelpers.writeText(screen, startX, 2, String.format("DETAILS: Entry #%d", entry.getId()), 
            TextColor.ANSI.CYAN_BRIGHT, SCREEN_WIDTH);
        
        int row = 4;
        
        // Item details
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Item Name:    %s", item.getItemName()), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Brand:        %s", item.getBrand() != null ? item.getBrand() : "N/A"), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        row++;
        
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Category:     %s > %s", item.getPrimaryCategory(), item.getSecondaryCategory()), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Function:     %s", item.getFunction()), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Context:      %s", item.getContext()), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Usage:        %s", item.getUsageType()), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Replace:      %s", item.getReplaceability()), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        row++;
        
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Quantity:     %d", item.getQuantity()), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        
        // Status with color
        TextColor statusColor = item.getStatus().toString().equals("HELD") 
            ? TextColor.ANSI.YELLOW_BRIGHT 
            : TextColor.ANSI.GREEN_BRIGHT;
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Status:       %s", item.getStatus()), 
            statusColor, SCREEN_WIDTH);
        
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Logged:       %s", item.getTimestamp().format(DATE_FORMATTER)), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
    }
    
    /**
     * Draws the footer with controls.
     */
    private void drawFooter() {
        int footerRow = SCREEN_HEIGHT - 1;
        
        // Draw bottom border
        for (int x = 0; x < SCREEN_WIDTH; x++) {
            screen.setCharacter(x, SCREEN_HEIGHT - 2, new TextCharacter('═'));
        }
        
        // Controls with filter status
        String controls = String.format("[↑↓: Nav] [F: Filter:%s] [A: Add] [R: Release] [D: Del] [Q: Quit]",
            currentFilter.getDisplayName());
        UIHelpers.writeText(screen, 2, footerRow, controls, TextColor.ANSI.CYAN, SCREEN_WIDTH);
    }
    
    private void handleInput() throws IOException {
        KeyStroke keyStroke = screen.readInput();
        
        if (keyStroke == null) {
            return;
        }
        
        switch (keyStroke.getKeyType()) {
            case ArrowUp:
                if (selectedIndex > 0) {
                    selectedIndex--;
                }
                break;
                
            case ArrowDown:
                if (selectedIndex < filteredEntries.size() - 1) {
                    selectedIndex++;
                }
                break;
                
            case Character:
                handleCharacterInput(keyStroke.getCharacter());
                break;
                
            default:
                break;
        }
    }
    
    /**
     Handles character key presses.
     */
    private void handleCharacterInput(Character c) throws IOException {
        switch (Character.toLowerCase(c)) {
            case 'q':
                running = false;
                break;
                
            case 'f':
                toggleFilter();
                break;
                
            case 'a':
                handleAddItem();
                break;
                
            case 'r':
                handleReleaseItem();
                break;
                
            case 'd':
                handleDeleteItem();
                break;
        }
    }
    
    /**
     Toggles through filter modes.
     */
    private void toggleFilter() {
        switch (currentFilter) {
            case ALL:
                currentFilter = FilterMode.HELD_ONLY;
                break;
            case HELD_ONLY:
                currentFilter = FilterMode.RELEASED_ONLY;
                break;
            case RELEASED_ONLY:
                currentFilter = FilterMode.ALL;
                break;
        }
        
        // Reset selection when filter changes
        selectedIndex = 0;
    }
    
    private void handleAddItem() throws IOException {
        AddItemScreen addScreen = new AddItemScreen(screen, itemService);
        boolean saved = addScreen.show();
        
        if (saved) {
            selectedIndex = 0;
        }
    }
    
    private void handleReleaseItem() throws IOException {
        if (filteredEntries.isEmpty() || selectedIndex < 0) {
            showMessageDialog("No item selected", false);
            return;
        }
        
        ItemLog.ItemEntry entry = filteredEntries.get(selectedIndex);
        Item item = entry.getItem();
        
        if (item.getStatus().toString().equals("RELEASED")) {
            showMessageDialog("Item already released", false);
            return;
        }
        
        String itemName = UIHelpers.truncate(item.getItemName(), 25);
        boolean confirmed = showConfirmDialog(
            "RELEASE ITEM",
            "Release \"" + itemName + "\"?"
        );
        
        if (!confirmed) {
            return;
        }
        
        boolean success = itemService.releaseItem(entry.getId());
        
        if (success) {
            showMessageDialog("Item released successfully", true);
        } else {
            showMessageDialog("Failed to release item", false);
        }
    }
    
    private void handleDeleteItem() throws IOException {
        if (filteredEntries.isEmpty() || selectedIndex < 0) {
            showMessageDialog("No item selected", false);
            return;
        }
        
        ItemLog.ItemEntry entry = filteredEntries.get(selectedIndex);
        Item item = entry.getItem();
        
        String itemName = UIHelpers.truncate(item.getItemName(), 25);
        boolean confirmed = showConfirmDialog(
            "DELETE ITEM",
            "Permanently delete \"" + itemName + "\"?"
        );
        
        if (!confirmed) {
            return;
        }
        
        boolean success = itemService.removeItemById(entry.getId());
        
        if (success) {
            if (selectedIndex >= filteredEntries.size() - 1 && selectedIndex > 0) {
                selectedIndex--;
            }
            showMessageDialog("Item deleted successfully", true);
        } else {
            showMessageDialog("Failed to delete item", false);
        }
    }
    
    private boolean showConfirmDialog(String title, String message) throws IOException {
        final int DIALOG_WIDTH = 50;
        final int DIALOG_HEIGHT = 7;
        final int startX = (SCREEN_WIDTH - DIALOG_WIDTH) / 2;
        final int startY = (SCREEN_HEIGHT - DIALOG_HEIGHT) / 2;
        
        UIHelpers.drawBox(screen, startX, startY, DIALOG_WIDTH, DIALOG_HEIGHT);
        
        int titleX = startX + UIHelpers.centerTextX(title, DIALOG_WIDTH);
        UIHelpers.writeText(screen, titleX, startY + 1, title, TextColor.ANSI.CYAN_BRIGHT, SCREEN_WIDTH);
        
        int messageX = startX + UIHelpers.centerTextX(message, DIALOG_WIDTH);
        UIHelpers.writeText(screen, messageX, startY + 3, message, TextColor.ANSI.WHITE, SCREEN_WIDTH);
        
        String options = "[Y] Yes    [N] No";
        int optionsX = startX + UIHelpers.centerTextX(options, DIALOG_WIDTH);
        UIHelpers.writeText(screen, optionsX, startY + 5, options, TextColor.ANSI.YELLOW, SCREEN_WIDTH);
        
        screen.refresh();
        
        while (true) {
            KeyStroke keyStroke = screen.readInput();
            
            if (keyStroke == null) {
                continue;
            }
            
            if (keyStroke.getKeyType() == KeyType.Character) {
                char c = Character.toLowerCase(keyStroke.getCharacter());
                if (c == 'y') {
                    return true;
                } else if (c == 'n') {
                    return false;
                }
            } else if (keyStroke.getKeyType() == KeyType.Escape) {
                return false;
            }
        }
    }
    
    private void showMessageDialog(String message, boolean isSuccess) throws IOException {
        final int DIALOG_WIDTH = message.length() + 6;
        final int DIALOG_HEIGHT = 3;
        final int startX = (SCREEN_WIDTH - DIALOG_WIDTH) / 2;
        final int startY = (SCREEN_HEIGHT - DIALOG_HEIGHT) / 2;
        
        TextColor messageColor = isSuccess ? TextColor.ANSI.GREEN_BRIGHT : TextColor.ANSI.RED_BRIGHT;
        String icon = isSuccess ? "✓" : "X";
        String fullMessage = icon + " " + message;
        
        UIHelpers.drawBox(screen, startX, startY, DIALOG_WIDTH, DIALOG_HEIGHT);
        
        int messageX = startX + UIHelpers.centerTextX(fullMessage, DIALOG_WIDTH);
        UIHelpers.writeText(screen, messageX, startY + 1, fullMessage, messageColor, SCREEN_WIDTH);
        
        screen.refresh();
        
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            // Ignore interruption
        }
    }
}