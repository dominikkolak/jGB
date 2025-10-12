package cpu.components;

import cpu.constants.CpuConstants;
import shared.Component;

public class StackPointer implements Component {
    private int value;

    public StackPointer() {
        reset();
    }

    @Override
    public void tick(int cycles) {
        // no
    }

    @Override
    public void reset() {
        value = CpuConstants.INITIAL_SP;
    }

    @Override
    public String getComponentName() {
        return "Stack Pointer";
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value & 0xFFFF;
    }

    public void increment() {
        value = (value + 1) & 0xFFFF;
    }

    public void decrement() {
        value = (value - 1) & 0xFFFF;
    }

    public void add(int offset) {
        value = (value + offset) & 0xFFFF;
    }
}
