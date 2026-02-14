package io;

public enum Button {

    RIGHT(0, true),
    LEFT(1, true),
    UP(2, true),
    DOWN(3, true),

    A(0, false),
    B(1, false),
    SELECT(2, false),
    START(3, false);

    private final int bit;
    private final boolean isDPad;

    Button(int bit, boolean isDPad) {
        this.bit = bit;
        this.isDPad = isDPad;
    }

    public int bit() { return bit; }

    public int mask() { return 1 << bit; }
    public boolean isDPad() { return isDPad; }
    public boolean isAction() { return !isDPad; }

}
