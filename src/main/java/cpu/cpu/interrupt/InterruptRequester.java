package cpu.cpu.interrupt;

import cpu.register.enums.INTERRUPT;

public interface InterruptRequester {

    void request(INTERRUPT interrupt);
    void clear(INTERRUPT interrupt);

}
