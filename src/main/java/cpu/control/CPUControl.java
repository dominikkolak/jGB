package cpu.control;

public interface CPUControl {

    void halt();
    void stop();
    void scheduleEnableInterrupts();
    void disableInterrupts();
    void enableInterrupts();

}
