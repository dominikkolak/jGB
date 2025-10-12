package cpu.cpu.state;

public enum CPUState {
    RUNNING, HALTED, STOPPED;

    public boolean isRunning() { return this == RUNNING; }
    public boolean isHalted() { return this == HALTED; }
    public boolean isStopped() { return this == STOPPED; }

    public boolean shouldAdvanceClock() { return this != STOPPED; }
}
