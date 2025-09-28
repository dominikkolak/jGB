package cpu.interrupt;

import cpu.register.enums.INTERRUPT;

public interface InterruptProvider {

    boolean isIMEnabled();
    boolean hasPending();
    boolean shouldDispatch();

    INTERRUPT acknowledgeInterrupt();

    void updateIME();

}
