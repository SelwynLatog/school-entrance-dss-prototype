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

import java.util.ArrayList;
import java.util.List;

public class DSSVisualHelpers {

    // Layout constants
    //NOTE: I can't figure out how to accurately fit risk breakdown context cleanly. The only way I managed to see everything is adjusting and zooming out my terminal size itself. You can try tweaking SCREEN_WIDTH & SCREEN_HEIGHT so the panes look right on your personal terminal. My apologies in advanced.
    public static final int SCREEN_WIDTH = 125;
    public static final int SCREEN_HEIGHT = 35;
    public static final int LEFT_PANEL_WIDTH = 60;

    public static final int RIGHT_PANEL_START_X = LEFT_PANEL_WIDTH + 2;
    public static final int RIGHT_PANEL_USABLE_WIDTH = SCREEN_WIDTH - RIGHT_PANEL_START_X - 2;

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
        for (int i = 1; i < width - 1; i++) {
            screen.setCharacter(x + i, y, new TextCharacter('─'));
            screen.setCharacter(x + i, y + height - 1, new TextCharacter('─'));
        }
        for (int i = 1; i < height - 1; i++) {
            screen.setCharacter(x, y + i, new TextCharacter('│'));
            screen.setCharacter(x + width - 1, y + i, new TextCharacter('│'));
        }
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

    public static List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }

        for (String paragraph : text.split("\n")) {
            if (paragraph.length() <= maxWidth) {
                lines.add(paragraph);
                continue;
            }

            // Word-wrap long paragraphs
            String[] words = paragraph.split(" ");
            StringBuilder current = new StringBuilder();
            for (String word : words) {
                if (current.length() == 0) {
                    current.append(word);
                } else if (current.length() + 1 + word.length() <= maxWidth) {
                    current.append(' ').append(word);
                } else {
                    lines.add(current.toString());
                    current = new StringBuilder(word);
                }
            }
            if (current.length() > 0) {
                lines.add(current.toString());
            }
        }
        return lines;
    }

    public static String getThreatEmoji(ThreatLevel level) {
        switch (level) {
            case CRITICAL: return "[!]";
            case HIGH:     return "[!]";
            case MEDIUM:   return "[*]";
            case LOW:      return "[*]";
            case NONE:     return "[ ]";
            default:       return "[?]";
        }
    }

    public static String getDecisionEmoji(Decision decision) {
        switch (decision) {
            case DISALLOW:    return "[X]";
            case CONDITIONAL: return "[!]";
            case ALLOW:       return "[ ]";
            default:          return "[?]";
        }
    }

    public static TextColor getDecisionColor(Decision decision) {
        switch (decision) {
            case DISALLOW:    return TextColor.ANSI.RED_BRIGHT;
            case CONDITIONAL: return TextColor.ANSI.YELLOW_BRIGHT;
            case ALLOW:       return TextColor.ANSI.GREEN_BRIGHT;
            default:          return TextColor.ANSI.WHITE;
        }
    }

    public static TextColor getThreatColor(ThreatLevel level) {
        switch (level) {
            case CRITICAL: return TextColor.ANSI.RED_BRIGHT;
            case HIGH:     return TextColor.ANSI.RED;
            case MEDIUM:   return TextColor.ANSI.YELLOW_BRIGHT;
            case LOW:      return TextColor.ANSI.YELLOW;
            case NONE:     return TextColor.ANSI.GREEN_BRIGHT;
            default:       return TextColor.ANSI.WHITE;
        }
    }
}