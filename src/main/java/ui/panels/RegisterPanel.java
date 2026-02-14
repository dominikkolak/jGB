package ui.panels;

import javafx.scene.control.Label;

public class RegisterPanel extends TablePanel {

    private Label aValue, bValue, cValue, dValue, eValue, hValue, lValue;
    private Label spValue, pcValue;

    public RegisterPanel() {
        super("CPU Registers",
                new ColumnDefinition(30, CellType.LABEL),
                new ColumnDefinition(40, CellType.VALUE),
                new ColumnDefinition(30, CellType.LABEL),
                new ColumnDefinition(CellType.VALUE)
        );
        buildTable();
    }

    private void buildTable() {
        Label[] row1 = addRow("A", "00", "SP", "0000");
        aValue = row1[0];
        spValue = row1[1];

        Label[] row2 = addRow("B", "00", "PC", "0000");
        bValue = row2[0];
        pcValue = row2[1];

        Label[] row3 = addRow("C", "00", "", "");
        cValue = row3[0];

        Label[] row4 = addRow("D", "00", "", "");
        dValue = row4[0];

        Label[] row5 = addRow("E", "00", "", "");
        eValue = row5[0];

        Label[] row6 = addRow("H", "00", "", "");
        hValue = row6[0];

        Label[] row7 = addRow("L", "00", "", "");
        lValue = row7[0];
    }

    public void updateRegisters(int a, int b, int c, int d, int e, int h, int l, int sp, int pc) {
        aValue.setText(String.format("%02X", a));
        bValue.setText(String.format("%02X", b));
        cValue.setText(String.format("%02X", c));
        dValue.setText(String.format("%02X", d));
        eValue.setText(String.format("%02X", e));
        hValue.setText(String.format("%02X", h));
        lValue.setText(String.format("%02X", l));
        spValue.setText(String.format("%04X", sp));
        pcValue.setText(String.format("%04X", pc));
    }

    public void clear() {
        aValue.setText("00");
        bValue.setText("00");
        cValue.setText("00");
        dValue.setText("00");
        eValue.setText("00");
        hValue.setText("00");
        lValue.setText("00");
        spValue.setText("0000");
        pcValue.setText("0000");
    }
}