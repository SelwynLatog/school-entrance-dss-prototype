package ui;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;
import com.googlecode.lanterna.input.*;
import model.Student;
import service.StudentService;
import enums.StudentStatus;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * Dual-pane view for browsing the student log.
 * Left panel: Student list with navigation
 * Right panel: Detailed view of selected student with violation history
 */
public class ViewStudentScreen {
    
    /**
     * Filter modes for viewing students.
     */
    private enum FilterMode {
        ALL("All Students"),
        ENROLLED("Enrolled Only"),
        SUSPENDED("Suspended Only"),
        OUTSIDERS("Outsiders Only"),
        WITH_VIOLATIONS("Violators Only");
        
        private final String displayName;
        
        FilterMode(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private final Screen screen;
    private final StudentService studentService;
    private List<Student> students;
    private List<Student> filteredStudents;
    private int selectedIndex;
    private boolean running;
    private FilterMode currentFilter;
    
    // Layout constants
    private static final int LEFT_PANEL_WIDTH = 50;
    private static final int SCREEN_HEIGHT = 26;
    private static final int SCREEN_WIDTH = 125;
    
    // Date formatter
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("MMM dd, yyyy");
    
    public ViewStudentScreen(StudentService studentService) throws IOException {
        this.studentService = studentService;
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
                // Refresh student list from service
                students = studentService.getAllStudents();
                
                // Apply filter
                applyFilter();
                
                // Ensure valid selection
                if (filteredStudents.isEmpty()) {
                    selectedIndex = -1;
                } else if (selectedIndex >= filteredStudents.size()) {
                    selectedIndex = filteredStudents.size() - 1;
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
     * Applies the current filter to the student list.
     */
    private void applyFilter() {
        switch (currentFilter) {
            case ENROLLED:
                filteredStudents = students.stream()
                    .filter(s -> s.getStatus() == StudentStatus.ENROLLED)
                    .toList();
                break;
                
            case SUSPENDED:
                filteredStudents = students.stream()
                    .filter(s -> s.getStatus() == StudentStatus.SUSPENDED)
                    .toList();
                break;
                
            case OUTSIDERS:
                filteredStudents = students.stream()
                    .filter(s -> s.getStatus() == StudentStatus.OUTSIDER)
                    .toList();
                break;
                
            case WITH_VIOLATIONS:
                filteredStudents = students.stream()
                    .filter(Student::hasViolations)
                    .toList();
                break;
                
            case ALL:
            default:
                filteredStudents = new ArrayList<>(students);
                break;
        }
    }
    
    private void render() throws IOException {
        screen.clear();
        
        // Draw header
        drawHeader();
        
        // Draw vertical separator
        drawVerticalSeparator();
        
        // Draw left panel (student list)
        drawStudentList();
        
        // Draw right panel (student details)
        drawStudentDetails();
        
        // Draw footer
        drawFooter();
        
        screen.refresh();
    }
    
    /**
     * Draws the header bar.
     */
    private void drawHeader() {
        String title = "STUDENT LOG VIEWER";
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
     * Draws the left panel with student list.
     */
    private void drawStudentList() {
        UIHelpers.writeText(screen, 2, 2, "STUDENT LIST", TextColor.ANSI.CYAN_BRIGHT, SCREEN_WIDTH);
        
        if (filteredStudents.isEmpty()) {
            String emptyMessage = currentFilter == FilterMode.ALL 
                ? "No students registered" 
                : "No " + currentFilter.getDisplayName().toLowerCase();
            UIHelpers.writeText(screen, 2, 4, emptyMessage, TextColor.ANSI.YELLOW, SCREEN_WIDTH);
            return;
        }
        
        // Draw students (starting from row 4)
        int startRow = 4;
        int maxVisible = SCREEN_HEIGHT - 8;
        
        for (int i = 0; i < Math.min(filteredStudents.size(), maxVisible); i++) {
            Student student = filteredStudents.get(i);
            int row = startRow + i;
            
            // Selection indicator
            String indicator = (i == selectedIndex) ? "►" : " ";
            
            // Format: "► 1. [2024-1001] Juan Dela Cruz  (3 violations)"
            String violationInfo = student.hasViolations() 
                ? String.format("(%d)", student.getViolationCount())
                : "";
            
            String line = String.format("%s %d. [%s] %-20s %s",
                indicator,
                i + 1,
                UIHelpers.truncate(student.getStudentId(), 10),
                UIHelpers.truncate(student.getName(), 20),
                violationInfo
            );
            
            // Color based on status
            TextColor color;
            if (i == selectedIndex) {
                color = TextColor.ANSI.YELLOW_BRIGHT;
            } else if (student.isSuspended()) {
                color = TextColor.ANSI.RED;
            } else if (student.hasViolations()) {
                color = TextColor.ANSI.YELLOW;
            } else {
                color = TextColor.ANSI.WHITE;
            }
            
            UIHelpers.writeText(screen, 2, row, line, color, SCREEN_WIDTH);
        }
        
        // Draw summary stats
        long enrolledCount = students.stream()
            .filter(s -> s.getStatus() == StudentStatus.ENROLLED)
            .count();
        long violatorCount = students.stream()
            .filter(Student::hasViolations)
            .count();
        
        int summaryRow = SCREEN_HEIGHT - 5;
        UIHelpers.writeText(screen, 2, summaryRow, "─────────────────────────────", 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        UIHelpers.writeText(screen, 2, summaryRow + 1, 
            String.format("Total: %d students", students.size()), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        UIHelpers.writeText(screen, 2, summaryRow + 2, 
            String.format("Enrolled: %d | Violators: %d", enrolledCount, violatorCount), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
    }
    
    /**
     * Draws the right panel with detailed student information.
     */
    private void drawStudentDetails() {
        int startX = LEFT_PANEL_WIDTH + 2;
        
        if (filteredStudents.isEmpty() || selectedIndex < 0) {
            UIHelpers.writeText(screen, startX, 2, "DETAILS: No student selected", 
                TextColor.ANSI.CYAN_BRIGHT, SCREEN_WIDTH);
            return;
        }
        
        Student student = filteredStudents.get(selectedIndex);
        
        UIHelpers.writeText(screen, startX, 2, "STUDENT DETAILS", 
            TextColor.ANSI.CYAN_BRIGHT, SCREEN_WIDTH);
        
        int row = 4;
        
        // Student identity
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Student ID:   %s", student.getStudentId()), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Name:         %s", student.getName()), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Course:       %s", student.getCourse()), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Year Level:   %s", student.getYear() == 0 ? "N/A" : String.valueOf(student.getYear())), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        row++;
        
        // Status with color
        TextColor statusColor;
        switch (student.getStatus()) {
            case ENROLLED:
                statusColor = TextColor.ANSI.GREEN_BRIGHT;
                break;
            case SUSPENDED:
                statusColor = TextColor.ANSI.RED_BRIGHT;
                break;
            case OUTSIDER:
                statusColor = TextColor.ANSI.YELLOW;
                break;
            default:
                statusColor = TextColor.ANSI.WHITE;
        }
        
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Status:       %s", student.getStatus()), 
            statusColor, SCREEN_WIDTH);
        
        UIHelpers.writeText(screen, startX, row++, 
            String.format("Enrolled:     %s", student.getEnrollmentDate().format(DATE_FORMATTER)), 
            TextColor.ANSI.WHITE, SCREEN_WIDTH);
        row++;
        
        // Violation history
        UIHelpers.writeText(screen, startX, row++, 
            "VIOLATION HISTORY:", 
            TextColor.ANSI.CYAN_BRIGHT, SCREEN_WIDTH);
        
        if (student.hasViolations()) {
            UIHelpers.writeText(screen, startX, row++, 
                String.format("Total Violations: %d", student.getViolationCount()), 
                TextColor.ANSI.RED, SCREEN_WIDTH);
            
            // List item IDs
            List<Integer> itemIds = student.getItemIds();
            UIHelpers.writeText(screen, startX, row++, 
                "Confiscated Items:", 
                TextColor.ANSI.WHITE, SCREEN_WIDTH);
            
            int maxItemsToShow = Math.min(itemIds.size(), 5);
            for (int i = 0; i < maxItemsToShow; i++) {
                UIHelpers.writeText(screen, startX + 2, row++, 
                    String.format("• Item ID: %d", itemIds.get(i)), 
                    TextColor.ANSI.YELLOW, SCREEN_WIDTH);
            }
            
            if (itemIds.size() > maxItemsToShow) {
                UIHelpers.writeText(screen, startX + 2, row++, 
                    String.format("... and %d more", itemIds.size() - maxItemsToShow), 
                    TextColor.ANSI.WHITE, SCREEN_WIDTH);
            }
        } else {
            UIHelpers.writeText(screen, startX, row++, 
                "No violations recorded", 
                TextColor.ANSI.GREEN, SCREEN_WIDTH);
        }
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
        String controls = String.format("[↑↓: Nav] [F: Filter:%s] [Q: Quit]",
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
                if (selectedIndex < filteredStudents.size() - 1) {
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
     * Handles character key presses.
     */
    private void handleCharacterInput(Character c) throws IOException {
        switch (Character.toLowerCase(c)) {
            case 'q':
                running = false;
                break;
                
            case 'f':
                toggleFilter();
                break;
        }
    }
    
    /**
     * Toggles through filter modes.
     */
    private void toggleFilter() {
        switch (currentFilter) {
            case ALL:
                currentFilter = FilterMode.ENROLLED;
                break;
            case ENROLLED:
                currentFilter = FilterMode.SUSPENDED;
                break;
            case SUSPENDED:
                currentFilter = FilterMode.OUTSIDERS;
                break;
            case OUTSIDERS:
                currentFilter = FilterMode.WITH_VIOLATIONS;
                break;
            case WITH_VIOLATIONS:
                currentFilter = FilterMode.ALL;
                break;
        }
        
        // Reset selection when filter changes
        selectedIndex = 0;
    }
}