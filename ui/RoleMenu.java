//Role Menu-Security/Admin
package ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import java.util.List;
import test.TestLoader;
import ui.dss.DSSAdminScreen;
import service.ItemService;
import storage.ItemLog;

public class RoleMenu {
    private final Screen screen;
    private final List<String> roles = List.of("SECURITY", "ADMIN");
    private int selectedIndex = 0;
    private boolean running = true;
    
    public RoleMenu() throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        this.screen = new com.googlecode.lanterna.screen.TerminalScreen(terminal);
    }
    
    public String show() throws IOException {
        screen.startScreen();
        try {
            while (running) {
                render();
                handleInput();
            }
        } finally {
            screen.stopScreen();
        }
        return roles.get(selectedIndex);
    }
    
    private void render() throws IOException {
        screen.clear();
        int boxX = 50;
        int boxY = 6;
        int boxW = 20;
        int boxH = 7;
        
        UIHelpers.drawBox(screen, boxX, boxY, boxW, boxH);
        UIHelpers.writeText(
            screen,
            boxX + UIHelpers.centerTextX("ROLE MENU", boxW),
            boxY + 1,
            "ROLE MENU",
            TextColor.ANSI.CYAN_BRIGHT,
            120
        );
        
        for (int i = 0; i < roles.size(); i++) {
            boolean sel = i == selectedIndex;
            UIHelpers.writeText(
                screen,
                boxX + 3,
                boxY + 3 + i,
                (sel ? "► " : "  ") + roles.get(i),
                sel ? TextColor.ANSI.YELLOW_BRIGHT : TextColor.ANSI.WHITE,
                120
            );
        }
        screen.refresh();
    }
    
    private void handleInput() throws IOException {
        KeyStroke key = screen.readInput();
        if (key == null) return;
        if (key.getKeyType() == KeyType.ArrowUp && selectedIndex > 0) selectedIndex--;
        if (key.getKeyType() == KeyType.ArrowDown && selectedIndex < roles.size() - 1) selectedIndex++;
        if (key.getKeyType() == KeyType.Enter || key.getKeyType() == KeyType.Escape) running = false;
    }
    
    public static void start() throws Exception {
        // Create shared ItemLog and ItemService
        ItemLog itemLog = new ItemLog();
        ItemService itemService = new ItemService(itemLog);
        
        //sample items
        TestLoader.addSampleItems(itemService);
        
        RoleMenu menu = new RoleMenu();
        String role = menu.show();
        if (role == null) return;
        
        switch (role) {
            case "SECURITY" -> {
                ViewLogScreen logScreen = new ViewLogScreen(itemService);
                logScreen.show();
            }
            case "ADMIN" -> {
                DSSAdminScreen dssScreen = new DSSAdminScreen(itemService);
                dssScreen.show();
            }
        }
    }
}