package cart.rtc;

import shared.Clocked;
import shared.Component;

public interface RealTimeClock extends Clocked, Component {

    int read(int register);
    void write(int register, int value);
    void latch();

}
