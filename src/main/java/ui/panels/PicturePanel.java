package ui.panels;

import javafx.scene.control.Label;
import ui.panels.TablePanel;

public class PicturePanel extends TablePanel {

    private Label lcdcValue, statValue, scyValue, scxValue;
    private Label lyValue, lycValue, bgpValue, obp0Value;
    private Label obp1Value, wyValue, wxValue, modeValue;

    public PicturePanel() {
        super("PPU REGISTERS",
                new ColumnDefinition(40, CellType.LABEL),
                new ColumnDefinition(35, CellType.VALUE),
                new ColumnDefinition(40, CellType.LABEL),
                new ColumnDefinition(CellType.VALUE)
        );
        buildTable();
    }

    private void buildTable() {
        Label[] row1 = addRow("LCDC", "00", "LY", "00");
        lcdcValue = row1[0];
        lyValue = row1[1];

        Label[] row2 = addRow("STAT", "00", "LYC", "00");
        statValue = row2[0];
        lycValue = row2[1];

        Label[] row3 = addRow("SCY", "00", "BGP", "00");
        scyValue = row3[0];
        bgpValue = row3[1];

        Label[] row4 = addRow("SCX", "00", "OBP0", "00");
        scxValue = row4[0];
        obp0Value = row4[1];

        Label[] row5 = addRow("WY", "00", "OBP1", "00");
        wyValue = row5[0];
        obp1Value = row5[1];

        Label[] row6 = addRow("WX", "00", "MODE", "OAM");
        wxValue = row6[0];
        modeValue = row6[1];
    }

    public void updateRegisters(int lcdc, int stat, int scy, int scx, int ly, int lyc,
                                int bgp, int obp0, int obp1, int wy, int wx, String mode) {
        lcdcValue.setText(String.format("%02X", lcdc));
        statValue.setText(String.format("%02X", stat));
        scyValue.setText(String.format("%02X", scy));
        scxValue.setText(String.format("%02X", scx));
        lyValue.setText(String.format("%02X", ly));
        lycValue.setText(String.format("%02X", lyc));
        bgpValue.setText(String.format("%02X", bgp));
        obp0Value.setText(String.format("%02X", obp0));
        obp1Value.setText(String.format("%02X", obp1));
        wyValue.setText(String.format("%02X", wy));
        wxValue.setText(String.format("%02X", wx));
        modeValue.setText(mode != null ? mode : "---");
    }

    public void updateFromSnapshot(int lcdc, int stat, int scy, int scx, int ly, int lyc,
                                   int bgp, int obp0, int obp1, int wy, int wx, Object mode) {
        lcdcValue.setText(String.format("%02X", lcdc));
        statValue.setText(String.format("%02X", stat));
        scyValue.setText(String.format("%02X", scy));
        scxValue.setText(String.format("%02X", scx));
        lyValue.setText(String.format("%02X", ly));
        lycValue.setText(String.format("%02X", lyc));
        bgpValue.setText(String.format("%02X", bgp));
        obp0Value.setText(String.format("%02X", obp0));
        obp1Value.setText(String.format("%02X", obp1));
        wyValue.setText(String.format("%02X", wy));
        wxValue.setText(String.format("%02X", wx));

        if (mode != null) {
            modeValue.setText(mode.toString());
        } else {
            modeValue.setText("---");
        }
    }

    public void clear() {
        lcdcValue.setText("00");
        statValue.setText("00");
        scyValue.setText("00");
        scxValue.setText("00");
        lyValue.setText("00");
        lycValue.setText("00");
        bgpValue.setText("00");
        obp0Value.setText("00");
        obp1Value.setText("00");
        wyValue.setText("00");
        wxValue.setText("00");
        modeValue.setText("OAM");
    }
}