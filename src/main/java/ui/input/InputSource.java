package ui.input;

public interface InputSource {

    boolean isPressed(io.Button button);

    String getName();

    boolean isAvailable();

    default void update() {}
}