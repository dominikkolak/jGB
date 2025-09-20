package cart.rtc;

public class NRTC implements RealTimeClock {

    public static final NRTC INSTANCE = new NRTC();
    private NRTC() {}

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
