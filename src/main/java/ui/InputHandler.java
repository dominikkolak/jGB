package ui;

import io.Button;
import io.InputProvider;
import javafx.scene.input.KeyCode;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InputHandler implements InputProvider {

    private volatile Set<Button> pressed = EnumSet.noneOf(Button.class);
    private volatile Map<KeyCode, Button> keyMap = new HashMap<>();

    public InputHandler() {
        keyMap.put(KeyCode.RIGHT, Button.RIGHT);
        keyMap.put(KeyCode.LEFT, Button.LEFT);
        keyMap.put(KeyCode.UP, Button.UP);
        keyMap.put(KeyCode.DOWN, Button.DOWN);
        keyMap.put(KeyCode.Y, Button.A);
        keyMap.put(KeyCode.X, Button.B);
        keyMap.put(KeyCode.ENTER, Button.START);
        keyMap.put(KeyCode.BACK_SPACE, Button.SELECT);
    }

    public void keyPressed(KeyCode code) {
        Button button = keyMap.get(code);
        if (button != null) {
            pressed.add(button);
        }
    }

    public void keyReleased(KeyCode code) {
        Button button = keyMap.get(code);
        if (button != null) {
            pressed.remove(button);
        }
    }

    @Override
    public boolean isPressed(Button button) {
        return pressed.contains(button);
    }

}
