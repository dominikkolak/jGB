package io;

import java.util.Arrays;

@FunctionalInterface
public interface InputProvider {

    boolean isPressed(Button button);

    static InputProvider none() {
        return button -> false;
    }

    static InputProvider fixed(Button... buttons) {
        java.util.Set<Button> set = java.util.EnumSet.noneOf(Button.class);
        set.addAll(Arrays.asList(buttons));
        return set::contains;
    }

}
