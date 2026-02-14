package ui.input;

import io.InputProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputManager implements InputProvider {

    private final List<InputSource> sources = new CopyOnWriteArrayList<>();
    private volatile boolean enabled = true;

    public InputManager() {
    }

    public void addSource(InputSource source) {
        if (source != null && !sources.contains(source)) {
            sources.add(source);
        }
    }

    public void removeSource(InputSource source) {
        sources.remove(source);
    }

    public List<InputSource> getSources() {
        return new ArrayList<>(sources);
    }

    public void update() {
        if (enabled) {
            sources.forEach(InputSource::update);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isPressed(io.Button button) {
        if (!enabled) {
            return false;
        }

        return sources.stream()
                .filter(InputSource::isAvailable)
                .anyMatch(source -> source.isPressed(button));
    }
}