package ui;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;

/**
 * Shared utilities for terminal UI rendering.
 * Provides reusable drawing primitives for dialogs, borders, and text.
 * 
 * Centralizes all common UI operations to eliminate duplication across screens.
 */
public class UIHelpers {
    
    /**
     * Draws a bordered box with the given dimensions.
     * Uses Unicode box-drawing characters for a clean look.
     * 
     * @param screen the Lanterna screen to draw on
     * @param startX left edge of the box
     * @param startY top edge of the box
     * @param width total width including borders
     * @param height total height including borders
     */
    public static void drawBox(Screen screen, int startX, int startY, int width, int height) {
        // Top border
        screen.setCharacter(startX, startY, new TextCharacter('╔'));
        for (int x = 1; x < width - 1; x++) {
            screen.setCharacter(startX + x, startY, new TextCharacter('═'));
        }
        screen.setCharacter(startX + width - 1, startY, new TextCharacter('╗'));
        
        // Middle rows with side borders
        for (int y = 1; y < height - 1; y++) {
            screen.setCharacter(startX, startY + y, new TextCharacter('║'));
            for (int x = 1; x < width - 1; x++) {
                screen.setCharacter(startX + x, startY + y, 
                    new TextCharacter(' ', TextColor.ANSI.WHITE, TextColor.ANSI.BLACK));
            }
            screen.setCharacter(startX + width - 1, startY + y, new TextCharacter('║'));
        }
        
        // Bottom border
        screen.setCharacter(startX, startY + height - 1, new TextCharacter('╚'));
        for (int x = 1; x < width - 1; x++) {
            screen.setCharacter(startX + x, startY + height - 1, new TextCharacter('═'));
        }
        screen.setCharacter(startX + width - 1, startY + height - 1, new TextCharacter('╝'));
    }
    
    /**
     * Draws a box with a horizontal separator line before the bottom border.
     * Useful for dialogs with a distinct footer section.
     * 
     * @param screen the Lanterna screen to draw on
     * @param startX left edge of the box
     * @param startY top edge of the box
     * @param width total width including borders
     * @param height total height including borders
     */
    public static void drawBoxWithFooter(Screen screen, int startX, int startY, int width, int height) {
        // Draw main box first
        drawBox(screen, startX, startY, width, height);
        
        // Add separator before footer (2 rows from bottom)
        screen.setCharacter(startX, startY + height - 2, new TextCharacter('╠'));
        for (int x = 1; x < width - 1; x++) {
            screen.setCharacter(startX + x, startY + height - 2, new TextCharacter('═'));
        }
        screen.setCharacter(startX + width - 1, startY + height - 2, new TextCharacter('╣'));
    }
    
    /**
     * Writes text at the specified position with color.
     * Handles automatic clipping if text exceeds screen width.
     * 
     * @param screen the Lanterna screen to write to
     * @param x starting X coordinate
     * @param y Y coordinate (row)
     * @param text the text to write
     * @param color foreground color for the text
     * @param maxWidth maximum width (usually screen width)
     */
    public static void writeText(Screen screen, int x, int y, String text, TextColor color, int maxWidth) {
        for (int i = 0; i < text.length() && x + i < maxWidth; i++) {
            screen.setCharacter(x + i, y, 
                new TextCharacter(text.charAt(i), color, TextColor.ANSI.BLACK));
        }
    }
    
    public static String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    
    public static int centerTextX(String text, int containerWidth) {
        return (containerWidth - text.length()) / 2;
    }
}