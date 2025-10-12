package cpu.state;

public enum CpuState {
    RUNNING,
    HALTED, // waiting for interrupt
    STOPPED // waiting for button press
}
