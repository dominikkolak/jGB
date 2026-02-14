package ui.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ui.input.KeyboardInputSource;

import java.util.HashMap;
import java.util.Map;

public class InputSettingsDialog extends Stage {

    private final KeyboardInputSource keyboardSource;
    private final Map<io.Button, Label> keyLabels = new HashMap<>();
    private io.Button currentlyMapping = null;

    public InputSettingsDialog(Stage owner, KeyboardInputSource keyboardSource) {
        this.keyboardSource = keyboardSource;

        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Input Settings");
        setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label title = new Label("Keyboard Mapping");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label instructions = new Label("Click a button to remap it, then press the desired key");
        instructions.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 11px;");

        GridPane grid = createMappingGrid();

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button resetButton = new Button("Reset to Defaults");
        resetButton.setOnAction(e -> resetToDefaults());

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> close());

        buttonBox.getChildren().addAll(resetButton, closeButton);

        root.getChildren().addAll(title, instructions, grid, buttonBox);

        Scene scene = new Scene(root);
        setScene(scene);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);
    }

    private GridPane createMappingGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(100);
        col1.setHalignment(javafx.geometry.HPos.RIGHT);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setMinWidth(150);

        grid.getColumnConstraints().addAll(col1, col2);

        io.Button[] buttons = {
                io.Button.UP, io.Button.DOWN, io.Button.LEFT, io.Button.RIGHT,
                io.Button.A, io.Button.B, io.Button.START, io.Button.SELECT
        };

        Map<io.Button, KeyCode> currentMapping = keyboardSource.getMapping();

        int row = 0;
        for (io.Button button : buttons) {
            Label nameLabel = new Label(button.name() + ":");
            nameLabel.setStyle("-fx-font-family: monospace;");

            KeyCode currentKey = currentMapping.get(button);
            Label keyLabel = new Label(currentKey != null ? currentKey.getName() : "NONE");
            keyLabel.setStyle(
                    "-fx-font-family: monospace; " +
                            "-fx-padding: 5 10; " +
                            "-fx-background-color: -color-bg-subtle; " +
                            "-fx-border-color: -color-border-default; " +
                            "-fx-border-width: 1; " +
                            "-fx-cursor: hand;"
            );

            keyLabels.put(button, keyLabel);

            keyLabel.setOnMouseClicked(e -> startMapping(button, keyLabel));

            grid.add(nameLabel, 0, row);
            grid.add(keyLabel, 1, row);
            row++;
        }

        return grid;
    }

    private void startMapping(io.Button button, Label label) {
        if (currentlyMapping != null) {
            Label prevLabel = keyLabels.get(currentlyMapping);
            resetLabelStyle(prevLabel);
        }

        currentlyMapping = button;
        label.setText("Press a key...");
        label.setStyle(
                "-fx-font-family: monospace; " +
                        "-fx-padding: 5 10; " +
                        "-fx-background-color: -color-accent-emphasis; " +
                        "-fx-text-fill: -color-fg-on-emphasis; " +
                        "-fx-border-color: -color-border-default; " +
                        "-fx-border-width: 1; " +
                        "-fx-cursor: hand;"
        );
    }

    private void handleKeyPress(KeyEvent event) {
        if (currentlyMapping == null) {
            return;
        }

        KeyCode newKey = event.getCode();

        if (isModifierKey(newKey) || newKey == KeyCode.ESCAPE) {
            if (newKey == KeyCode.ESCAPE) {
                cancelMapping();
            }
            return;
        }

        Map<io.Button, KeyCode> currentMapping = keyboardSource.getMapping();

        io.Button conflicting = null;
        for (Map.Entry<io.Button, KeyCode> entry : currentMapping.entrySet()) {
            if (entry.getValue() == newKey && entry.getKey() != currentlyMapping) {
                conflicting = entry.getKey();
                break;
            }
        }

        if (conflicting != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Key Conflict");
            alert.setHeaderText("This key is already mapped to " + conflicting.name());
            alert.setContentText("Do you want to swap the mappings?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                KeyCode oldKey = currentMapping.get(currentlyMapping);
                currentMapping.put(conflicting, oldKey);
                currentMapping.put(currentlyMapping, newKey);
                keyboardSource.setMapping(currentMapping);

                keyLabels.get(conflicting).setText(oldKey != null ? oldKey.getName() : "NONE");
                resetLabelStyle(keyLabels.get(conflicting));
            } else {
                cancelMapping();
                return;
            }
        } else {
            currentMapping.put(currentlyMapping, newKey);
            keyboardSource.setMapping(currentMapping);
        }

        Label label = keyLabels.get(currentlyMapping);
        label.setText(newKey.getName());
        resetLabelStyle(label);

        currentlyMapping = null;
        event.consume();
    }

    private void cancelMapping() {
        if (currentlyMapping != null) {
            Label label = keyLabels.get(currentlyMapping);
            KeyCode currentKey = keyboardSource.getKeyFor(currentlyMapping);
            label.setText(currentKey != null ? currentKey.getName() : "NONE");
            resetLabelStyle(label);
            currentlyMapping = null;
        }
    }

    private void resetLabelStyle(Label label) {
        label.setStyle(
                "-fx-font-family: monospace; " +
                        "-fx-padding: 5 10; " +
                        "-fx-background-color: -color-bg-subtle; " +
                        "-fx-border-color: -color-border-default; " +
                        "-fx-border-width: 1; " +
                        "-fx-cursor: hand;"
        );
    }

    private void resetToDefaults() {
        keyboardSource.setDefaultMapping();
        Map<io.Button, KeyCode> mapping = keyboardSource.getMapping();

        keyLabels.forEach((button, label) -> {
            KeyCode key = mapping.get(button);
            label.setText(key != null ? key.getName() : "NONE");
            resetLabelStyle(label);
        });

        currentlyMapping = null;
    }

    private boolean isModifierKey(KeyCode code) {
        return code == KeyCode.SHIFT || code == KeyCode.CONTROL ||
                code == KeyCode.ALT || code == KeyCode.META ||
                code == KeyCode.COMMAND || code == KeyCode.WINDOWS;
    }
}