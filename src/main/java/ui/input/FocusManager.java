package ui.input;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;

public class FocusManager {

    private final Scene scene;
    private final KeyboardInputSource keyboardSource;
    private volatile boolean captureInput = true;

    public FocusManager(Scene scene, KeyboardInputSource keyboardSource) {
        this.scene = scene;
        this.keyboardSource = keyboardSource;
        installEventFilters();
    }

    private void installEventFilters() {

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (shouldCaptureInput(event)) {
                keyboardSource.handleKeyPress(event.getCode());
                event.consume();
            }
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (shouldCaptureInput(event)) {
                keyboardSource.handleKeyRelease(event.getCode());
                event.consume();
            }
        });

        scene.getWindow().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                keyboardSource.clearPressed();
            }
        });
    }

    private boolean shouldCaptureInput(KeyEvent event) {
        if (!captureInput) {
            return false;
        }

        Node focused = scene.getFocusOwner();
        if (focused instanceof TextInputControl) {
            return false;
        }

        return keyboardSource.getMapping().containsValue(event.getCode());
    }

    public void setCaptureEnabled(boolean enabled) {
        this.captureInput = enabled;
        if (!enabled) {
            keyboardSource.clearPressed();
        }
    }

    public boolean isCaptureEnabled() {
        return captureInput;
    }
}