package cpu.interfaces;

public interface InterruptController {

    boolean handleInterrupts(BUS bus);

    void requestInterrupt(int interruptBit);

}
