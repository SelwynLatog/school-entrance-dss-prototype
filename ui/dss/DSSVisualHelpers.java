//DSSVisualHelpers.java
/**
 Shared visual components for DSS dashboard. 
 Provides reusable rendering utilities:
 - Border drawing
 - Score bars
 - Status emojis
 - Color mapping
  
 All methods are static - this is a utility class, not a component.
 */
package ui.dss;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.Screen;
import engine.ThreatLevel;
import enums.Decision;

public class DSSVisualHelpers {
    
    // Layout constants
    public static final int SCREEN_WIDTH = 130;
    public static final int SCREEN_HEIGHT = 30;
    public static final int LEFT_PANEL_WIDTH = 60;
    
    private DSSVisualHelpers() {
        throw new AssertionError("Cannot load util class.");
    }
    
    public static void drawBorderTop(Screen screen, int row) {
        for (int x = 0; x < SCREEN_WIDTH; x++) {
            screen.setCharacter(x, row, new TextCharacter('═'));
        }
    }
    
    public static void drawBorderMiddle(Screen screen, int row) {
        for (int x = 0; x < SCREEN_WIDTH; x++) {
            screen.setCharacter(x, row, new TextCharacter('─'));
        }
    }
    
    public static void drawVerticalSeparator(Screen screen, int x, int startY, int endY) {
        for (int y = startY; y < endY; y++) {
            screen.setCharacter(x, y, new TextCharacter('║'));
        }
    }
    
    public static void drawBox(Screen screen, int x, int y, int width, int height) {
        // Top and bottom borders
        for (int i = 1; i < width - 1; i++) {
            screen.setCharacter(x + i, y, new TextCharacter('─'));
            screen.setCharacter(x + i, y + height - 1, new TextCharacter('─'));
        }
        
        // Left and right borders
        for (int i = 1; i < height - 1; i++) {
            screen.setCharacter(x, y + i, new TextCharacter('│'));
            screen.setCharacter(x + width - 1, y + i, new TextCharacter('│'));
        }
        
        // Corners
        screen.setCharacter(x, y, new TextCharacter('┌'));
        screen.setCharacter(x + width - 1, y, new TextCharacter('┐'));
        screen.setCharacter(x, y + height - 1, new TextCharacter('└'));
        screen.setCharacter(x + width - 1, y + height - 1, new TextCharacter('┘'));
    }
    
    public static String generateScoreBar(int value, int max) {
        int barLength = 20;
        int filled = Math.min(barLength, (int) ((double) value / max * barLength));
        
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filled ? '▓' : '░');
        }
        return bar.toString();
    }
    
    public static String getThreatEmoji(ThreatLevel level) {
        switch (level) {
            case CRITICAL: return "[!]";
            case HIGH: return "[!]";
            case MEDIUM: return "[*]";
            case LOW: return "[*]";
            case NONE: return "[]";
            default: return "[?]";
        }
    }
    
    public static String getDecisionEmoji(Decision decision) {
        switch (decision) {
            case DISALLOW: return "[X]";
            case CONDITIONAL: return "[!]";
            case ALLOW: return "[]";
            default: return "[?]";
        }
    }
    
    //Decison Color
    public static TextColor getDecisionColor(Decision decision) {
        switch (decision) {
            case DISALLOW: return TextColor.ANSI.RED_BRIGHT;
            case CONDITIONAL: return TextColor.ANSI.YELLOW_BRIGHT;
            case ALLOW: return TextColor.ANSI.GREEN_BRIGHT;
            default: return TextColor.ANSI.WHITE;
        }
    }
    //Threat Color
    public static TextColor getThreatColor(ThreatLevel level) {
        switch (level) {
            case CRITICAL: return TextColor.ANSI.RED_BRIGHT;
            case HIGH: return TextColor.ANSI.RED;
            case MEDIUM: return TextColor.ANSI.YELLOW_BRIGHT;
            case LOW: return TextColor.ANSI.YELLOW;
            case NONE: return TextColor.ANSI.GREEN_BRIGHT;
            default: return TextColor.ANSI.WHITE;
        }
    }
}