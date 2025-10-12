package cpu.state;

import shared.Component;

public class TimingState implements Component {

    private long totalCycles;
    private int currentInstructionCycles;

    public TimingState() {
        reset();
    }

    @Override
    public void tick(int cycles) {
        totalCycles += cycles;
    }

    @Override
    public void reset() {
        totalCycles = 0;
        currentInstructionCycles = 0;
    }

    @Override
    public String getComponentName() {
        return "Timing State";
    }

    public long getTotalCycles() {
        return totalCycles;
    }

    public void addCycles(int cycles) {
        totalCycles += cycles;
        currentInstructionCycles = cycles;
    }

    public int getCurrentInstructionCycles() {
        return currentInstructionCycles;
    }

}
