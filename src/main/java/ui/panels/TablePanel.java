package ui.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TablePanel extends VBox {

    protected GridPane grid;
    private int currentRow = 0;
    private ColumnDefinition[] columnDefs;
    private TabPane tabPane;

    public TablePanel(String title, ColumnDefinition... columns) {
        this.columnDefs = columns;
        initUIWithTabs(new String[]{title}, columns);
    }

    public TablePanel(String[] tabNames, ColumnDefinition... columns) {
        this.columnDefs = columns;
        initUIWithTabs(tabNames, columns);
    }

    private void initUIWithTabs(String[] tabNames, ColumnDefinition[] columns) {
        tabPane = new TabPane();
        tabPane.setStyle("-fx-border-color: -color-border-default; " +
                "-fx-border-width: 1 1 0 1;");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        for (String tabName : tabNames) {
            Tab tab = new Tab(tabName.toUpperCase());
            tab.setStyle("-fx-font-size: 8px; -fx-font-family: monospace;");
            tabPane.getTabs().add(tab);
        }

        initGrid(columns);

        tabPane.getTabs().get(0).setContent(grid);

        this.setStyle("-fx-background-color: -color-bg-default;");
        this.getChildren().add(tabPane);

        VBox.setVgrow(tabPane, Priority.ALWAYS);
    }

    private void initGrid(ColumnDefinition[] columns) {
        grid = new GridPane();
        grid.setPadding(new Insets(0));
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setAlignment(Pos.TOP_LEFT);

        for (ColumnDefinition colDef : columns) {
            ColumnConstraints cc = new ColumnConstraints();
            if (colDef.isGrow()) {
                cc.setHgrow(Priority.ALWAYS);
                cc.setFillWidth(true);
            } else {
                cc.setHgrow(Priority.NEVER);
                cc.setPrefWidth(colDef.getWidth());
                cc.setMinWidth(colDef.getWidth());
                cc.setMaxWidth(colDef.getWidth());
            }
            grid.getColumnConstraints().add(cc);
        }
    }

    protected Label[] addRow(Object... cells) {
        int valueCount = 0;
        for (int i = 0; i < cells.length; i++) {
            if (columnDefs[i].getType() == CellType.VALUE) valueCount++;
        }

        Label[] valueLabels = new Label[valueCount];
        int valueIndex = 0;

        for (int i = 0; i < cells.length; i++) {
            String text = cells[i].toString();
            Label cell = createCell(text, columnDefs[i].getType(), i);
            grid.add(cell, i, currentRow);
            GridPane.setHgrow(cell, columnDefs[i].isGrow() ? Priority.ALWAYS : Priority.NEVER);

            if (columnDefs[i].getType() == CellType.VALUE) {
                valueLabels[valueIndex++] = cell;
            }
        }
        currentRow++;
        return valueLabels;
    }

    private Label createCell(String text, CellType type, int columnIndex) {
        Label cell = new Label(text);
        cell.setMaxWidth(Double.MAX_VALUE);
        cell.setMaxHeight(Double.MAX_VALUE);
        cell.setMinHeight(18);

        String borderWidth = (columnIndex == 0) ? "1 1 1 1" : "1 1 1 0";

        String baseStyle = "-fx-font-family: monospace; " +
                "-fx-border-color: -color-border-default; " +
                "-fx-border-width: " + borderWidth + "; ";

        switch (type) {
            case LABEL:
                cell.setAlignment(Pos.CENTER);
                cell.setStyle(baseStyle +
                        "-fx-font-size: 8px; " +
                        "-fx-text-fill: -color-fg-default; " +
                        "-fx-background-color: derive(-color-bg-default, -3%); " +
                        "-fx-padding: 3;");
                break;

            case VALUE:
                cell.setAlignment(Pos.CENTER);
                cell.setStyle(baseStyle +
                        "-fx-font-size: 9px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: -color-accent-fg; " +
                        "-fx-background-color: derive(-color-bg-default, -10%); " +
                        "-fx-padding: 3;");
                break;
        }

        return cell;
    }

    protected int getCurrentTab() {
        return tabPane != null ? tabPane.getSelectionModel().getSelectedIndex() : -1;
    }

    protected void setTabContent(int tabIndex, Region content) {
        if (tabPane != null && tabIndex < tabPane.getTabs().size()) {
            tabPane.getTabs().get(tabIndex).setContent(content);
        }
    }

    public static class ColumnDefinition {
        private final double width;
        private final CellType type;
        private final boolean grow;

        public ColumnDefinition(double width, CellType type) {
            this.width = width;
            this.type = type;
            this.grow = false;
        }

        public ColumnDefinition(CellType type) {
            this.width = 0;
            this.type = type;
            this.grow = true;
        }

        public double getWidth() { return width; }
        public CellType getType() { return type; }
        public boolean isGrow() { return grow; }
    }

    public enum CellType {
        LABEL,
        VALUE
    }
}