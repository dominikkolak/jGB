package ui.input;

import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class KeyboardInputSource implements InputSource {

    private final Set<KeyCode> pressedKeys = ConcurrentHashMap.newKeySet();
    private volatile Map<io.Button, KeyCode> buttonToKeyMap;
    private volatile Map<KeyCode, io.Button> keyToButtonMap;

    public KeyboardInputSource() {
        setDefaultMapping();
    }

    public KeyboardInputSource(Map<io.Button, KeyCode> mapping) {
        setMapping(mapping);
    }

    public void setDefaultMapping() {
        Map<io.Button, KeyCode> defaults = new HashMap<>();
        defaults.put(io.Button.RIGHT, KeyCode.RIGHT);
        defaults.put(io.Button.LEFT, KeyCode.LEFT);
        defaults.put(io.Button.UP, KeyCode.UP);
        defaults.put(io.Button.DOWN, KeyCode.DOWN);
        defaults.put(io.Button.A, KeyCode.Z);
        defaults.put(io.Button.B, KeyCode.X);
        defaults.put(io.Button.START, KeyCode.SPACE);
        defaults.put(io.Button.SELECT, KeyCode.SHIFT);
        setMapping(defaults);
    }

    public void setMapping(Map<io.Button, KeyCode> mapping) {
        this.buttonToKeyMap = new HashMap<>(mapping);
        this.keyToButtonMap = new HashMap<>();
        mapping.forEach((button, key) -> keyToButtonMap.put(key, button));
    }

    public Map<io.Button, KeyCode> getMapping() {
        return new HashMap<>(buttonToKeyMap);
    }

    public KeyCode getKeyFor(io.Button button) {
        return buttonToKeyMap.get(button);
    }

    public void handleKeyPress(KeyCode code) {
        if (code != null) {
            pressedKeys.add(code);
        }
    }

    public void handleKeyRelease(KeyCode code) {
        if (code != null) {
            pressedKeys.remove(code);
        }
    }

    public void clearPressed() {
        pressedKeys.clear();
    }

    @Override
    public boolean isPressed(io.Button button) {
        KeyCode key = buttonToKeyMap.get(button);
        return key != null && pressedKeys.contains(key);
    }

    @Override
    public String getName() {
        return "Keyboard";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    public boolean isKeyPressed(KeyCode code) {
        return pressedKeys.contains(code);
    }
}