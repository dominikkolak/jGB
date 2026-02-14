package ui.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import java.util.Map;
import java.util.TreeMap;

public class MemoryPanel extends TablePanel {

    private GridPane stackGrid;
    private GridPane codeGrid;
    private GridPane customGrid;

    private TextField startField;
    private TextField endField;
    private Button applyButton;

    private RangeCallback rangeCallback;

    private int currentSP = -1;
    private int currentPC = -1;

    @FunctionalInterface
    public interface RangeCallback {
        boolean onRangeChanged(int start, int end);
    }

    public MemoryPanel() {
        super(new String[]{"STACK", "CODE", "CUSTOM"},
                new ColumnDefinition(80, CellType.LABEL),  // Address
                new ColumnDefinition(CellType.VALUE)       // Value
        );
        buildTabs();
        initWithMockData();
    }

    private void initWithMockData() {
        Map<Integer, Integer> mockStack = new TreeMap<>();
        for (int i = 0; i < 32; i++) {
            mockStack.put(0xFFE0 + i, 0x00);
        }

        Map<Integer, Integer> mockCode = new TreeMap<>();
        for (int i = 0; i < 48; i++) {
            mockCode.put(0x00E0 + i, 0x00);
        }

        Map<Integer, Integer> mockCustom = new TreeMap<>();
        for (int i = 0; i <= 0x3F; i++) {
            mockCustom.put(i, 0x00);
        }

        updateMemory(mockStack, mockCode, mockCustom, 0xFFF0, 0x0100);
    }

    private void buildTabs() {
        stackGrid = createMemoryGrid();
        codeGrid = createMemoryGrid();

        ScrollPane stackScroll = wrapInScrollPane(stackGrid);
        ScrollPane codeScroll = wrapInScrollPane(codeGrid);

        VBox customTab = new VBox();
        customTab.setStyle("-fx-background-color: -color-bg-default;");

        HBox controls = createCustomControls();
        customGrid = createMemoryGrid();
        ScrollPane customScroll = wrapInScrollPane(customGrid);

        customTab.getChildren().addAll(controls, customScroll);
        VBox.setVgrow(customScroll, Priority.ALWAYS);

        setTabContent(0, stackScroll);
        setTabContent(1, codeScroll);
        setTabContent(2, customTab);
    }

    private ScrollPane wrapInScrollPane(GridPane grid) {
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: -color-bg-default; " +
                "-fx-background: -color-bg-default;");
        return scroll;
    }

    private HBox createCustomControls() {
        HBox controls = new HBox(6);
        controls.setPadding(new Insets(4));
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setStyle("-fx-background-color: derive(-color-bg-default, -8%); " +
                "-fx-border-color: -color-border-default; " +
                "-fx-border-width: 1;");

        Label startLabel = new Label("START:");
        startLabel.setStyle("-fx-font-size: 8px; -fx-font-family: monospace; -fx-text-fill: -color-fg-muted;");

        startField = new TextField("0000");
        startField.setPrefWidth(60);
        startField.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");

        Label endLabel = new Label("END:");
        endLabel.setStyle("-fx-font-size: 8px; -fx-font-family: monospace; -fx-text-fill: -color-fg-muted;");

        endField = new TextField("003F");  // 64 bytes (0x40) is more reasonable
        endField.setPrefWidth(60);
        endField.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");

        applyButton = new Button("SET");
        applyButton.setStyle("-fx-font-size: 8px; -fx-font-family: monospace;");
        applyButton.setOnAction(e -> applyCustomRange());

        controls.getChildren().addAll(startLabel, startField, endLabel, endField, applyButton);

        return controls;
    }

    private GridPane createMemoryGrid() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0));
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setAlignment(Pos.TOP_LEFT);

        ColumnConstraints col1 = new ColumnConstraints(80);
        col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private void applyCustomRange() {
        try {
            int start = Integer.parseInt(startField.getText(), 16);
            int end = Integer.parseInt(endField.getText(), 16);

            boolean success = false;

            if (rangeCallback != null) {
                success = rangeCallback.onRangeChanged(start, end);
            } else {
                success = (start >= 0 && end >= 0 && start <= 0xFFFF && end <= 0xFFFF &&
                        end > start && (end - start) <= 0x0040);
            }

            if (success) {
                startField.setStyle("-fx-font-size: 9px; -fx-font-family: monospace; -fx-border-color: green; -fx-border-width: 2;");
                endField.setStyle("-fx-font-size: 9px; -fx-font-family: monospace; -fx-border-color: green; -fx-border-width: 2;");

                javafx.application.Platform.runLater(() -> {
                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                    startField.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");
                    endField.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");
                });
            } else {
                startField.setStyle("-fx-font-size: 9px; -fx-font-family: monospace; -fx-border-color: red; -fx-border-width: 2;");
                endField.setStyle("-fx-font-size: 9px; -fx-font-family: monospace; -fx-border-color: red; -fx-border-width: 2;");
            }

        } catch (NumberFormatException e) {
            startField.setStyle("-fx-font-size: 9px; -fx-font-family: monospace; -fx-border-color: red; -fx-border-width: 2;");
            endField.setStyle("-fx-font-size: 9px; -fx-font-family: monospace; -fx-border-color: red; -fx-border-width: 2;");
        }
    }

    public void updateMemory(Map<Integer, Integer> stackWindow,
                             Map<Integer, Integer> codeWindow,
                             Map<Integer, Integer> customWindow,
                             int sp, int pc) {
        currentSP = sp;
        currentPC = pc;

        updateGrid(stackGrid, stackWindow, sp, "SP");
        updateGrid(codeGrid, codeWindow, pc, "PC");
        updateGrid(customGrid, customWindow, -1, null);
    }

    private void updateGrid(GridPane grid, Map<Integer, Integer> memory, int highlight, String marker) {
        grid.getChildren().clear();

        TreeMap<Integer, Integer> sorted = new TreeMap<>(memory);

        int row = 0;
        for (Map.Entry<Integer, Integer> entry : sorted.entrySet()) {
            int addr = entry.getKey();
            int value = entry.getValue();

            boolean isHighlight = (addr == highlight);

            String addrText = String.format("0x%04X", addr);
            if (isHighlight && marker != null) {
                addrText += " ← " + marker;
            }
            Label addrLabel = createMemoryCell(addrText, true, isHighlight);
            grid.add(addrLabel, 0, row);
            GridPane.setHgrow(addrLabel, Priority.NEVER);

            String valueText = String.format("%02X", value);
            Label valueLabel = createMemoryCell(valueText, false, isHighlight);
            grid.add(valueLabel, 1, row);
            GridPane.setHgrow(valueLabel, Priority.ALWAYS);

            row++;
        }
    }

    private Label createMemoryCell(String text, boolean isLabel, boolean highlight) {
        Label cell = new Label(text);
        cell.setMaxWidth(Double.MAX_VALUE);
        cell.setMaxHeight(Double.MAX_VALUE);
        cell.setMinHeight(18);

        String borderWidth = isLabel ? "1 1 1 1" : "1 1 1 0";

        if (isLabel) {
            cell.setAlignment(Pos.CENTER_LEFT);
            cell.setStyle("-fx-font-size: 8px; " +
                    "-fx-font-family: monospace; " +
                    "-fx-text-fill: " + (highlight ? "-color-accent-fg" : "-color-fg-default") + "; " +
                    "-fx-background-color: " + (highlight ? "derive(-color-accent-emphasis, -20%)" : "derive(-color-bg-default, -3%)") + "; " +
                    "-fx-border-color: -color-border-default; " +
                    "-fx-border-width: " + borderWidth + "; " +
                    "-fx-padding: 3;");
        } else {
            cell.setAlignment(Pos.CENTER);
            cell.setStyle("-fx-font-size: 9px; " +
                    "-fx-font-family: monospace; " +
                    "-fx-font-weight: bold; " +
                    "-fx-text-fill: " + (highlight ? "-color-accent-fg" : "-color-accent-fg") + "; " +
                    "-fx-background-color: " + (highlight ? "derive(-color-accent-emphasis, -20%)" : "derive(-color-bg-default, -10%)") + "; " +
                    "-fx-border-color: -color-border-default; " +
                    "-fx-border-width: " + borderWidth + "; " +
                    "-fx-padding: 3;");
        }

        return cell;
    }

    public void clear() {
        stackGrid.getChildren().clear();
        codeGrid.getChildren().clear();
        customGrid.getChildren().clear();
        startField.setText("0000");
        endField.setText("003F");
    }

    public void setRangeCallback(RangeCallback callback) {
        this.rangeCallback = callback;
    }
}