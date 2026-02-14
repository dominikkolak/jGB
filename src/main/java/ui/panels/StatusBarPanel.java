package ui.panels;

import core.Overlord;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class StatusBarPanel extends HBox {

    private final Label fpsLabel = new Label("FPS: --");
    private final Label frameLabel = new Label("FRAME: 0");
    private final Overlord emulator;

    public StatusBarPanel(Overlord emulator) {
        this.emulator = emulator;

        setPrefHeight(24);
        setMinHeight(24);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(4, 12, 4, 12));
        setStyle("-fx-background-color: derive(-color-bg-default, -8%); " +
                "-fx-border-color: -color-border-default; " +
                "-fx-border-width: 1 0 0 0;");

        fpsLabel.setStyle("-fx-font-size: 9px; " +
                "-fx-font-family: monospace; " +
                "-fx-text-fill: -color-fg-muted;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        frameLabel.setStyle("-fx-font-size: 9px; " +
                "-fx-font-family: monospace; " +
                "-fx-text-fill: -color-fg-muted;");

        getChildren().addAll(fpsLabel, spacer, frameLabel);

        AnimationTimer updateTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 250_000_000) { // 250ms
                    updateStatus();
                    lastUpdate = now;
                }
            }
        };
        updateTimer.start();
    }

    private void updateStatus() {
        if (emulator.isCartridgeLoaded()) {
            double fps = emulator.getCore().getFPS();
            long frames = emulator.getFrameCount();

            fpsLabel.setText(String.format("FPS: %.1f", fps));
            frameLabel.setText(String.format("FRAME: %d", frames));
        } else {
            fpsLabel.setText("FPS: --");
            frameLabel.setText("FRAME: 0");
        }
    }
}