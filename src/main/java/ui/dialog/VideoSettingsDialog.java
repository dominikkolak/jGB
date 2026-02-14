package ui.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.panels.ScreenPanel;

public class VideoSettingsDialog extends Stage {

    private static final String[] PALETTE_NAMES = {
            "ORIGINAL", "GRAYSCALE", "CLASSIC", "MUTED",
            "OCEAN", "SUNSET", "CRIMSON", "VAPOR", "FOREST", "PURPLE"
    };

    private int selectedPalette;
    private ScreenPanel screenPanel;

    public VideoSettingsDialog(Stage owner, ScreenPanel screenPanel) {
        this.screenPanel = screenPanel;
        this.selectedPalette = screenPanel.getPalette();

        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        setTitle("Video Settings");
        setResizable(false);

        VBox root = new VBox(16);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: -color-bg-default;");

        Label title = new Label("DISPLAY PALETTE");
        title.setStyle("-fx-font-size: 12px; " +
                "-fx-font-family: monospace; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: -color-fg-default;");

        Label currentLabel = new Label("Current: " + PALETTE_NAMES[selectedPalette]);
        currentLabel.setStyle("-fx-font-size: 10px; " +
                "-fx-font-family: monospace; " +
                "-fx-text-fill: -color-fg-muted;");

        VBox paletteBox = new VBox(8);
        paletteBox.setMaxWidth(300);

        for (int i = 0; i < ScreenPanel.PALETTES.length; i++) {
            HBox paletteRow = createPaletteRow(i, currentLabel);
            paletteBox.getChildren().add(paletteRow);
        }

        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button applyBtn = new Button("APPLY");
        applyBtn.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");
        applyBtn.setOnAction(e -> {
            screenPanel.setPalette(selectedPalette);
            close();
        });

        Button cancelBtn = new Button("CANCEL");
        cancelBtn.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");
        cancelBtn.setOnAction(e -> close());

        buttons.getChildren().addAll(cancelBtn, applyBtn);

        root.getChildren().addAll(title, currentLabel, paletteBox, buttons);

        Scene scene = new Scene(root);
        setScene(scene);
    }

    private HBox createPaletteRow(int index, Label currentLabel) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        HBox preview = new HBox(2);
        for (int color : ScreenPanel.PALETTES[index]) {
            Region colorSquare = new Region();
            colorSquare.setPrefSize(24, 24);
            colorSquare.setStyle(String.format(
                    "-fx-background-color: #%06X; " +
                            "-fx-border-color: -color-border-default; " +
                            "-fx-border-width: 1;",
                    color & 0xFFFFFF
            ));
            preview.getChildren().add(colorSquare);
        }

        Button selectBtn = new Button(PALETTE_NAMES[index]);
        selectBtn.setStyle("-fx-font-size: 9px; -fx-font-family: monospace;");
        selectBtn.setPrefWidth(120);
        selectBtn.setOnAction(e -> {
            selectedPalette = index;
            currentLabel.setText("Current: " + PALETTE_NAMES[index]);
        });

        row.getChildren().addAll(preview, selectBtn);

        return row;
    }
}