package io;

import cpu.interrupt.InterruptRequester;
import cpu.register.enums.INTERRUPT;
import mem.MemoryConstants;
import shared.Addressable;
import shared.Component;

public class JoyPad implements Addressable, Component {

    private final InterruptRequester interrupts;
    private InputProvider inputProvider;


    private int select;
    private int previousState;

    public JoyPad(InterruptRequester interrupts) {
        this.interrupts = interrupts;
        this.inputProvider = InputProvider.none();
        reset();
    }

    public void setInputProvider(InputProvider provider) {
        this.inputProvider = provider != null ? provider : InputProvider.none();
    }

    private int getDPadState() {
        int state = 0x0F;

        if (inputProvider.isPressed(Button.RIGHT)) state &= ~Button.RIGHT.mask();
        if (inputProvider.isPressed(Button.LEFT)) state &= ~Button.LEFT.mask();
        if (inputProvider.isPressed(Button.UP)) state &= ~Button.UP.mask();
        if (inputProvider.isPressed(Button.DOWN)) state &= ~Button.DOWN.mask();

        return state;
    }

    private int getButtonState() {
        int state = 0x0F;

        if (inputProvider.isPressed(Button.A)) state &= ~Button.A.mask();
        if (inputProvider.isPressed(Button.B)) state &= ~Button.B.mask();
        if (inputProvider.isPressed(Button.SELECT)) state &= ~Button.SELECT.mask();
        if (inputProvider.isPressed(Button.START)) state &= ~Button.START.mask();

        return state;
    }

    private int getInputState() {
        int state = 0x0F;

        if ((select & MemoryConstants.SELECT_DPAD) == 0) {
            state &= getDPadState();
        }

        if ((select & MemoryConstants.SELECT_BUTTONS) == 0) {
            state &= getButtonState();
        }

        return state;
    }

    public void update() {
        int currentState = getInputState();

        int pressed = previousState & ~currentState;

        if (pressed != 0) {
            interrupts.request(INTERRUPT.JOYPAD);
        }

        previousState = currentState;
    }

    public boolean isAnyButtonPressed() {
        return inputProvider.isPressed(Button.A)
                || inputProvider.isPressed(Button.B)
                || inputProvider.isPressed(Button.SELECT)
                || inputProvider.isPressed(Button.START)
                || inputProvider.isPressed(Button.UP)
                || inputProvider.isPressed(Button.DOWN)
                || inputProvider.isPressed(Button.LEFT)
                || inputProvider.isPressed(Button.RIGHT);
    }

    @Override
    public boolean accepts(int address) {
        return address == MemoryConstants.P1_JOYP;
    }

    @Override
    public void reset() {
        select = 0x30;
        previousState = 0x0F;
    }

    @Override
    public byte read(int address) {
        if (address == MemoryConstants.P1_JOYP) {
            return (byte) (0xC0 | select | getInputState());
        }
        return (byte) 0xFF;
    }

    @Override
    public void write(int address, int value) {
        if (address == MemoryConstants.P1_JOYP) {
            select = value & 0x30;
            previousState = getInputState();
        }
    }
}
