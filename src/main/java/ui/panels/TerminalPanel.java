package ui.panels;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ui.panels.TablePanel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TerminalPanel extends TablePanel {

    private TextArea outputArea;
    private PrintStream originalOut;
    private PrintStream originalErr;

    private final StringBuilder serialBuffer = new StringBuilder();

    private static final int MAX_LINES = 1000;

    public TerminalPanel() {
        super("TERMINAL",
                new ColumnDefinition(CellType.VALUE)
        );
        buildTerminal();
        redirectSystemStreams();
    }

    private void buildTerminal() {
        BorderPane terminalContent = new BorderPane();
        terminalContent.setStyle("-fx-background-color: -color-bg-default;");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setStyle(
                "-fx-control-inner-background: derive(-color-bg-default, -10%); " +
                        "-fx-text-fill: -color-fg-default; " +
                        "-fx-font-family: monospace; " +
                        "-fx-font-size: 9px; " +
                        "-fx-border-color: -color-border-default; " +
                        "-fx-border-width: 0;"
        );

        HBox controls = new HBox(8);
        controls.setPadding(new Insets(4));
        controls.setStyle("-fx-background-color: derive(-color-bg-default, -5%); " +
                "-fx-border-color: -color-border-default; " +
                "-fx-border-width: 1 0 0 0;");

        Button clearBtn = new Button("CLEAR");
        clearBtn.setStyle("-fx-font-size: 8px; -fx-font-family: monospace;");
        clearBtn.setOnAction(e -> clear());

        Button saveSerialBtn = new Button("SAVE SERIAL");
        saveSerialBtn.setStyle("-fx-font-size: 8px; -fx-font-family: monospace;");
        saveSerialBtn.setOnAction(e -> saveSerialOutput());

        controls.getChildren().addAll(clearBtn, saveSerialBtn);

        terminalContent.setCenter(outputArea);
        terminalContent.setBottom(controls);

        setTabContent(0, terminalContent);

        append("[TERMINAL] Output console initialized\n");
    }

    private void redirectSystemStreams() {
        originalOut = System.out;
        originalErr = System.err;

        OutputStream out = new OutputStream() {
            private StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                buffer.append((char) b);
                if (b == '\n') {
                    flush();
                }
            }

            @Override
            public void flush() {
                if (buffer.length() > 0) {
                    String text = buffer.toString();
                    buffer.setLength(0);

                    Platform.runLater(() -> append(text));

                    originalOut.print(text);
                }
            }
        };

        OutputStream err = new OutputStream() {
            private StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                buffer.append((char) b);
                if (b == '\n') {
                    flush();
                }
            }

            @Override
            public void flush() {
                if (buffer.length() > 0) {
                    String text = buffer.toString();
                    buffer.setLength(0);

                    Platform.runLater(() -> append("[ERROR] " + text));

                    originalErr.print(text);
                }
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(err, true));
    }

    public void handleSerialOutput(char c) {
        synchronized (serialBuffer) {
            serialBuffer.append(c);
        }

        Platform.runLater(() -> {
            if (outputArea.getText().isEmpty() || outputArea.getText().endsWith("\n")) {
                append("[SERIAL] ");
            }
            append(String.valueOf(c));
        });
    }

    private void saveSerialOutput() {
        String content;
        synchronized (serialBuffer) {
            content = serialBuffer.toString();
        }

        if (content.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Serial Data");
            alert.setHeaderText(null);
            alert.setContentText("No serial output to save.");
            alert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Serial Output");
        fileChooser.setInitialFileName(
                "serial_output_" +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                        ".txt"
        );
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        Stage stage = (Stage) outputArea.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                String output = "=== Game Boy Serial Output ===\n" +
                        "Saved: " + LocalDateTime.now() + "\n" +
                        "==============================\n\n" +
                        content;

                Files.write(file.toPath(), output.getBytes(StandardCharsets.UTF_8));

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Serial Output Saved");
                alert.setHeaderText(null);
                alert.setContentText("Serial output saved to:\n" + file.getAbsolutePath());
                alert.showAndWait();

                append("\n[TERMINAL] Serial output saved to: " + file.getName() + "\n");

            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save Failed");
                alert.setHeaderText("Failed to save serial output");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void append(String text) {
        outputArea.appendText(text);

        String content = outputArea.getText();
        String[] lines = content.split("\n");
        if (lines.length > MAX_LINES) {
            StringBuilder trimmed = new StringBuilder();
            for (int i = lines.length - MAX_LINES; i < lines.length; i++) {
                trimmed.append(lines[i]).append("\n");
            }
            outputArea.setText(trimmed.toString());
        }

        outputArea.setScrollTop(Double.MAX_VALUE);
    }

    public void clear() {
        outputArea.clear();
        append("[TERMINAL] Console cleared\n");

        synchronized (serialBuffer) {
            if (!serialBuffer.isEmpty()) {
                append("[TERMINAL] (Serial buffer not cleared - use SAVE SERIAL to export first)\n");
            }
        }
    }

    public void clearSerialBuffer() {
        synchronized (serialBuffer) {
            serialBuffer.setLength(0);
        }
        append("[TERMINAL] Serial buffer cleared\n");
    }

    public void restore() {
        if (originalOut != null) {
            System.setOut(originalOut);
        }
        if (originalErr != null) {
            System.setErr(originalErr);
        }
    }
}