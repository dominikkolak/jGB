package cart.rtc;

public class RTC implements RealTimeClock {

    @Override
    public int read(int register) { return 0xFF; }

    @Override
    public void write(int register, int value) {}

    @Override
    public void latch() {}

    @Override
    public void tick(int cycles) {}

    @Override
    public void reset() {}
}
