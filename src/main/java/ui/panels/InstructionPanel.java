package ui.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import snapshot.DisassembledInstruction;
import ui.panels.TablePanel;

import java.util.*;

public class InstructionPanel extends TablePanel {

    private GridPane instructionGrid;
    private GridPane breakpointGrid;

    private TextField addressField;
    private Button addButton;
    private Button removeButton;

    private Map<Integer, Boolean> breakpoints = new TreeMap<>();

    private BreakpointCallback breakpointCallback;

    private int currentPC = -1;

    @FunctionalInterface
    public interface BreakpointCallback {
        void onBreakpointChanged(int address, boolean enabled);
    }

    public InstructionPanel() {
        super(new String[]{"INSTRUCTIONS", "BREAKPOINTS"},
                new ColumnDefinition(60, CellType.LABEL),   // Address
                new ColumnDefinition(80, CellType.LABEL),   // Bytes
                new ColumnDefinition(CellType.VALUE)        // Mnemonic (grows)
        );
        buildTabs();
    }

    private void buildTabs() {
        instructionGrid = createInstructionGrid();
        ScrollPane instructionScroll = wrapInScrollPane(instructionGrid);

        VBox breakpointTab = new VBox();
        breakpointTab.setStyle("-fx-background-color: -color-bg-default;");

        HBox controls = createBreakpointControls();
        breakpointGrid = createBreakpointGrid();
        ScrollPane breakpointScroll = wrapInScrollPane(breakpointGrid);

        breakpointTab.getChildren().addAll(controls, breakpointScroll);
        VBox.setVgrow(breakpointScroll, Priority.ALWAYS);

        setTabContent(0, instructionScroll);
        setTabContent(1, breakpointTab);
    }

    private HBox createBreakpointControls() {
        HBox controls = new HBox(6);
        controls.setPadding(new Insets(4));
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setStyle("-fx-background-color: derive(-color-bg-default, -8%); " +
                "-fx-border-color: -color-border-default; " +
                "-fx-border-width: 1;");

        Label addrLabel = new Label("ADDR:");
        addrLabel.setStyle("-fx-font-size: 8px; -fx-font-family: monospace; -fx-text-fill: -color-fg-muted;");

        addressField = new TextField();
        addressField.setPromptText("0000");
        addressField.setPrefWidth(60);
        addressField.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");

        addButton = new Button("ADD");
        addButton.setStyle("-fx-font-size: 8px; -fx-font-family: monospace;");
        addButton.setOnAction(e -> addBreakpoint());

        removeButton = new Button("REMOVE");
        removeButton.setStyle("-fx-font-size: 8px; -fx-font-family: monospace;");
        removeButton.setOnAction(e -> removeBreakpoint());

        controls.getChildren().addAll(addrLabel, addressField, addButton, removeButton);

        return controls;
    }

    private GridPane createInstructionGrid() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0));
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setAlignment(Pos.TOP_LEFT);

        ColumnConstraints col1 = new ColumnConstraints(60);
        col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints(80);
        col2.setHgrow(Priority.NEVER);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2, col3);

        return grid;
    }

    private GridPane createBreakpointGrid() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0));
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setAlignment(Pos.TOP_LEFT);

        ColumnConstraints col1 = new ColumnConstraints(100);
        col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private ScrollPane wrapInScrollPane(GridPane grid) {
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: -color-bg-default; " +
                "-fx-background: -color-bg-default;");
        return scroll;
    }

    public void updateInstructions(DisassembledInstruction current,
                                   List<DisassembledInstruction> lookahead,
                                   int pc) {
        currentPC = pc;
        instructionGrid.getChildren().clear();

        int row = 0;

        addInstructionRow(instructionGrid, row++, current, true, false);

        boolean grayOut = isJumpOrBranch(current.mnemonic());

        for (DisassembledInstruction instr : lookahead) {
            addInstructionRow(instructionGrid, row++, instr, false, grayOut);

            if (isJumpOrBranch(instr.mnemonic())) {
                grayOut = true;
            }
        }
    }

    private void addInstructionRow(GridPane grid, int row,
                                   DisassembledInstruction instr,
                                   boolean highlight, boolean grayed) {
        String addrText = String.format("0x%04X", instr.address());
        if (highlight) {
            addrText += " →";
        }
        Label addrLabel = createInstructionCell(addrText, 0, highlight, grayed);
        grid.add(addrLabel, 0, row);

        StringBuilder bytes = new StringBuilder();
        bytes.append(String.format("%02X", instr.opcode()));
        for (int operand : instr.operands()) {
            bytes.append(String.format(" %02X", operand));
        }
        Label bytesLabel = createInstructionCell(bytes.toString(), 1, highlight, grayed);
        grid.add(bytesLabel, 1, row);

        Label mnemonicLabel = createInstructionCell(instr.mnemonic(), 2, highlight, grayed);
        grid.add(mnemonicLabel, 2, row);
    }

    private Label createInstructionCell(String text, int column, boolean highlight, boolean grayed) {
        Label cell = new Label(text);
        cell.setMaxWidth(Double.MAX_VALUE);
        cell.setMaxHeight(Double.MAX_VALUE);
        cell.setMinHeight(18);

        String borderWidth = (column == 0) ? "1 1 1 1" : "1 1 1 0";
        String alignment = (column == 2) ? "CENTER_LEFT" : "CENTER";

        String textColor;
        String bgColor;

        if (highlight) {
            textColor = "-color-accent-fg";
            bgColor = "derive(-color-accent-emphasis, -20%)";
        } else if (grayed) {
            textColor = "-color-fg-muted";
            bgColor = "derive(-color-bg-default, -5%)";
        } else {
            textColor = (column == 2) ? "-color-fg-default" : "-color-fg-default";
            bgColor = (column == 2) ? "derive(-color-bg-default, -3%)" : "derive(-color-bg-default, -3%)";
        }

        cell.setAlignment(alignment.equals("CENTER_LEFT") ? Pos.CENTER_LEFT : Pos.CENTER);
        cell.setStyle("-fx-font-size: 8px; " +
                "-fx-font-family: monospace; " +
                "-fx-text-fill: " + textColor + "; " +
                "-fx-background-color: " + bgColor + "; " +
                "-fx-border-color: -color-border-default; " +
                "-fx-border-width: " + borderWidth + "; " +
                "-fx-padding: 3;");

        return cell;
    }

    private boolean isJumpOrBranch(String mnemonic) {
        String upper = mnemonic.toUpperCase();
        return upper.startsWith("JP") ||
                upper.startsWith("JR") ||
                upper.startsWith("CALL") ||
                upper.startsWith("RET") ||
                upper.startsWith("RST");
    }

    private void addBreakpoint() {
        try {
            String text = addressField.getText().trim();
            int address = Integer.parseInt(text, 16);

            if (address < 0 || address > 0xFFFF) {
                return;
            }

            breakpoints.put(address, true);
            updateBreakpointGrid();
            addressField.clear();

            if (breakpointCallback != null) {
                breakpointCallback.onBreakpointChanged(address, true);
            }

        } catch (NumberFormatException e) {}
    }

    private void removeBreakpoint() {
        try {
            String text = addressField.getText().trim();
            int address = Integer.parseInt(text, 16);

            breakpoints.remove(address);
            updateBreakpointGrid();
            addressField.clear();

            if (breakpointCallback != null) {
                breakpointCallback.onBreakpointChanged(address, false);
            }

        } catch (NumberFormatException e) {}
    }

    private void updateBreakpointGrid() {
        breakpointGrid.getChildren().clear();

        int row = 0;
        for (Map.Entry<Integer, Boolean> entry : breakpoints.entrySet()) {
            int address = entry.getKey();
            boolean enabled = entry.getValue();

            Label addrLabel = createBreakpointCell(String.format("0x%04X", address), 0);
            breakpointGrid.add(addrLabel, 0, row);

            CheckBox toggle = new CheckBox("Enabled");
            toggle.setSelected(enabled);
            toggle.setStyle("-fx-font-size: 8px; -fx-font-family: monospace;");
            toggle.setOnAction(e -> {
                breakpoints.put(address, toggle.isSelected());
                if (breakpointCallback != null) {
                    breakpointCallback.onBreakpointChanged(address, toggle.isSelected());
                }
            });

            HBox toggleBox = new HBox(toggle);
            toggleBox.setAlignment(Pos.CENTER);
            toggleBox.setStyle("-fx-border-color: -color-border-default; " +
                    "-fx-border-width: 1 1 1 0; " +
                    "-fx-padding: 3;");
            toggleBox.setMaxWidth(Double.MAX_VALUE);
            toggleBox.setMinHeight(18);
            breakpointGrid.add(toggleBox, 1, row);
            GridPane.setHgrow(toggleBox, Priority.ALWAYS);

            row++;
        }
    }

    private Label createBreakpointCell(String text, int column) {
        Label cell = new Label(text);
        cell.setMaxWidth(Double.MAX_VALUE);
        cell.setMaxHeight(Double.MAX_VALUE);
        cell.setMinHeight(18);
        cell.setAlignment(Pos.CENTER);

        String borderWidth = (column == 0) ? "1 1 1 1" : "1 1 1 0";

        cell.setStyle("-fx-font-size: 8px; " +
                "-fx-font-family: monospace; " +
                "-fx-text-fill: -color-fg-default; " +
                "-fx-background-color: derive(-color-bg-default, -3%); " +
                "-fx-border-color: -color-border-default; " +
                "-fx-border-width: " + borderWidth + "; " +
                "-fx-padding: 3;");

        return cell;
    }

    public Set<Integer> getEnabledBreakpoints() {
        Set<Integer> enabled = new HashSet<>();
        for (Map.Entry<Integer, Boolean> entry : breakpoints.entrySet()) {
            if (entry.getValue()) {
                enabled.add(entry.getKey());
            }
        }
        return enabled;
    }

    public void setBreakpointCallback(BreakpointCallback callback) {
        this.breakpointCallback = callback;
    }

    public void syncBreakpoints(Map<Integer, Boolean> emulatorBreakpoints) {
        breakpoints = new TreeMap<>(emulatorBreakpoints);
        updateBreakpointGrid();
    }

    public void clear() {
        instructionGrid.getChildren().clear();
        breakpoints.clear();
        updateBreakpointGrid();
        addressField.clear();
    }
}