package ui.panels;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import ui.panels.TablePanel;

public class FlagInterruptPanel extends TablePanel {

    private GridPane flagsGrid;
    private Label zValue, nValue, hValue, cValue;

    private GridPane interruptsGrid;
    private Label ieValue, ifValue;
    private Label vblankValue, lcdValue, timerValue, serialValue, joypadValue;

    public FlagInterruptPanel() {
        super(new String[]{"FLAGS", "INTERRUPTS"},
                new ColumnDefinition(60, CellType.LABEL),
                new ColumnDefinition(CellType.VALUE)
        );
        buildTabs();
    }

    private void buildTabs() {
        flagsGrid = createGrid();
        buildFlagsTab(flagsGrid);
        setTabContent(0, flagsGrid);

        interruptsGrid = createGrid();
        buildInterruptsTab(interruptsGrid);
        setTabContent(1, interruptsGrid);
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(0));
        grid.setHgap(0);
        grid.setVgap(0);

        javafx.scene.layout.ColumnConstraints col0 = new javafx.scene.layout.ColumnConstraints();
        col0.setPrefWidth(60);
        col0.setMinWidth(60);
        col0.setMaxWidth(60);
        col0.setHgrow(Priority.NEVER);

        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setFillWidth(true);

        grid.getColumnConstraints().addAll(col0, col1);

        return grid;
    }

    private void buildFlagsTab(GridPane grid) {
        int row = 0;

        Label zLabel = createLabelCell("Z (Zero)");
        zValue = createValueCell("0");
        grid.add(zLabel, 0, row);
        grid.add(zValue, 1, row);
        row++;

        Label nLabel = createLabelCell("N (Sub)");
        nValue = createValueCell("0");
        grid.add(nLabel, 0, row);
        grid.add(nValue, 1, row);
        row++;

        Label hLabel = createLabelCell("H (Half)");
        hValue = createValueCell("0");
        grid.add(hLabel, 0, row);
        grid.add(hValue, 1, row);
        row++;

        Label cLabel = createLabelCell("C (Carry)");
        cValue = createValueCell("0");
        grid.add(cLabel, 0, row);
        grid.add(cValue, 1, row);
    }

    private void buildInterruptsTab(GridPane grid) {
        int row = 0;

        Label ieLabel = createLabelCell("IE");
        ieValue = createValueCell("00");
        grid.add(ieLabel, 0, row);
        grid.add(ieValue, 1, row);
        row++;

        Label ifLabel = createLabelCell("IF");
        ifValue = createValueCell("00");
        grid.add(ifLabel, 0, row);
        grid.add(ifValue, 1, row);
        row++;

        Label sep1 = createLabelCell("");
        Label sep2 = createValueCell("");
        grid.add(sep1, 0, row);
        grid.add(sep2, 1, row);
        row++;

        Label vblankLabel = createLabelCell("VBlank");
        vblankValue = createValueCell("OFF");
        grid.add(vblankLabel, 0, row);
        grid.add(vblankValue, 1, row);
        row++;

        Label lcdLabel = createLabelCell("LCD STAT");
        lcdValue = createValueCell("OFF");
        grid.add(lcdLabel, 0, row);
        grid.add(lcdValue, 1, row);
        row++;

        Label timerLabel = createLabelCell("Timer");
        timerValue = createValueCell("OFF");
        grid.add(timerLabel, 0, row);
        grid.add(timerValue, 1, row);
        row++;

        Label serialLabel = createLabelCell("Serial");
        serialValue = createValueCell("OFF");
        grid.add(serialLabel, 0, row);
        grid.add(serialValue, 1, row);
        row++;

        Label joypadLabel = createLabelCell("Joypad");
        joypadValue = createValueCell("OFF");
        grid.add(joypadLabel, 0, row);
        grid.add(joypadValue, 1, row);
    }

    private Label createLabelCell(String text) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        label.setPadding(new Insets(3, 6, 3, 6));
        label.setStyle("-fx-font-size: 8px; " +
                "-fx-font-family: monospace; " +
                "-fx-text-fill: -color-fg-muted; " +
                "-fx-background-color: derive(-color-bg-default, -5%); " +
                "-fx-border-color: -color-border-default; " +
                "-fx-border-width: 1;");
        return label;
    }

    private Label createValueCell(String text) {
        Label label = new Label(text);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        label.setAlignment(javafx.geometry.Pos.CENTER);
        label.setPadding(new Insets(3, 6, 3, 6));
        label.setStyle("-fx-font-size: 9px; " +
                "-fx-font-family: monospace; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: -color-fg-default; " +
                "-fx-background-color: -color-bg-default; " +
                "-fx-border-color: -color-border-default; " +
                "-fx-border-width: 1 1 1 0;");
        return label;
    }

    public void updateFlags(boolean z, boolean n, boolean h, boolean c) {
        zValue.setText(z ? "1" : "0");
        nValue.setText(n ? "1" : "0");
        hValue.setText(h ? "1" : "0");
        cValue.setText(c ? "1" : "0");
    }

    public void updateInterrupts(int ie, int iff) {
        ieValue.setText(String.format("%02X", ie));
        ifValue.setText(String.format("%02X", iff));

        boolean vblank = ((ie & 0x01) != 0) && ((iff & 0x01) != 0);
        boolean lcd = ((ie & 0x02) != 0) && ((iff & 0x02) != 0);
        boolean timer = ((ie & 0x04) != 0) && ((iff & 0x04) != 0);
        boolean serial = ((ie & 0x08) != 0) && ((iff & 0x08) != 0);
        boolean joypad = ((ie & 0x10) != 0) && ((iff & 0x10) != 0);

        vblankValue.setText(vblank ? "ON" : "OFF");
        lcdValue.setText(lcd ? "ON" : "OFF");
        timerValue.setText(timer ? "ON" : "OFF");
        serialValue.setText(serial ? "ON" : "OFF");
        joypadValue.setText(joypad ? "ON" : "OFF");

        String onStyle = "-fx-font-size: 9px; -fx-font-family: monospace; -fx-font-weight: bold; " +
                "-fx-text-fill: -color-success-fg; -fx-background-color: -color-bg-default; " +
                "-fx-border-color: -color-border-default; -fx-border-width: 1 1 1 0;";
        String offStyle = "-fx-font-size: 9px; -fx-font-family: monospace; -fx-font-weight: bold; " +
                "-fx-text-fill: -color-fg-muted; -fx-background-color: -color-bg-default; " +
                "-fx-border-color: -color-border-default; -fx-border-width: 1 1 1 0;";

        vblankValue.setStyle(vblank ? onStyle : offStyle);
        lcdValue.setStyle(lcd ? onStyle : offStyle);
        timerValue.setStyle(timer ? onStyle : offStyle);
        serialValue.setStyle(serial ? onStyle : offStyle);
        joypadValue.setStyle(joypad ? onStyle : offStyle);
    }

    public void clear() {
        zValue.setText("0");
        nValue.setText("0");
        hValue.setText("0");
        cValue.setText("0");

        ieValue.setText("00");
        ifValue.setText("00");

        String offStyle = "-fx-font-size: 9px; -fx-font-family: monospace; -fx-font-weight: bold; " +
                "-fx-text-fill: -color-fg-muted; -fx-background-color: -color-bg-default; " +
                "-fx-border-color: -color-border-default; -fx-border-width: 1 1 1 0;";

        vblankValue.setText("OFF");
        vblankValue.setStyle(offStyle);

        lcdValue.setText("OFF");
        lcdValue.setStyle(offStyle);

        timerValue.setText("OFF");
        timerValue.setStyle(offStyle);

        serialValue.setText("OFF");
        serialValue.setStyle(offStyle);

        joypadValue.setText("OFF");
        joypadValue.setStyle(offStyle);
    }
}