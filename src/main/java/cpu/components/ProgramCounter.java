package cpu.components;

import cpu.constants.CpuConstants;
import shared.Component;

public class ProgramCounter implements Component {

    private int value;

    public ProgramCounter() {
        reset();
    }

    @Override
    public void tick(int cycles) {
        // no
    }

    @Override
    public void reset() { value = CpuConstants.INITIAL_PC; }

    @Override
    public String getComponentName() { return "Program Counter"; }

    public int getValue() { return value; }

    public void setValue(int value) { this.value = value & 0xFFFF; }

    public void increment() { value = (value + 1) & 0xFFFF; }

    public void increment(int amount) { value = (value + amount) & 0xFFFF; }

    public void jump(int address) { value = address & 0xFFFF; }

}
