package cart.rtc;

public class RTC implements RealTimeClock {

    private long baseEpochSeconds;
    private long haltedElapsedSeconds;

    private boolean halt;
    private boolean carry;

    private int latchedSeconds;
    private int latchedMinutes;
    private int latchedHours;
    private int latchedDays;
    private boolean latchedHalt;
    private boolean latchedCarry;

    private boolean latched;

    public RTC() {
        reset();
    }

    private long now() {
        return System.currentTimeMillis() / 1000L;
    }

    private long getElapsedSeconds() {
        if (halt) return haltedElapsedSeconds;
        return now() - baseEpochSeconds;
    }

    private void setElapsedSeconds(long elapsed) {
        baseEpochSeconds = now() - elapsed;
        haltedElapsedSeconds = elapsed;
    }

    @Override
    public int read(int register) {
        long elapsed = latched ? computeLatchedElapsed() : getElapsedSeconds();

        int seconds = (int) (elapsed % 60);
        int minutes = (int) ((elapsed / 60) % 60);
        int hours = (int) ((elapsed / 3600) % 24);
        int days = (int) ((elapsed / 86400) % 512);

        boolean carryFlag = (elapsed / 86400) >= 512;

        switch (register) {
            case 0x08: return seconds;
            case 0x09: return minutes;
            case 0x0A: return hours;
            case 0x0B: return days & 0xFF;
            case 0x0C:
                int value = (days >> 8) & 1;
                if (halt) value |= 0x40;
                if (carryFlag || carry) value |= 0x80;
                return value;
        }

        return 0xFF;
    }

    private long computeLatchedElapsed() {
        return ((long) latchedDays * 86400L)
                + latchedHours * 3600L
                + latchedMinutes * 60L
                + latchedSeconds;
    }

    @Override
    public void write(int register, int value) {
        value &= 0xFF;

        long elapsed = getElapsedSeconds();

        int seconds = (int) (elapsed % 60);
        int minutes = (int) ((elapsed / 60) % 60);
        int hours = (int) ((elapsed / 3600) % 24);
        int days = (int) ((elapsed / 86400) % 512);

        switch (register) {
            case 0x08: seconds = value % 60; break;
            case 0x09: minutes = value % 60; break;
            case 0x0A: hours = value % 24; break;

            case 0x0B:
                days = (days & 0x100) | value;
                break;

            case 0x0C:
                halt = (value & 0x40) != 0;
                carry = (value & 0x80) != 0;
                days = (days & 0xFF) | ((value & 1) << 8);
                break;
        }

        long newElapsed =
                days * 86400L +
                        hours * 3600L +
                        minutes * 60L +
                        seconds;

        setElapsedSeconds(newElapsed);
    }

    @Override
    public void latch() {
        long elapsed = getElapsedSeconds();

        latchedSeconds = (int) (elapsed % 60);
        latchedMinutes = (int) ((elapsed / 60) % 60);
        latchedHours = (int) ((elapsed / 3600) % 24);
        latchedDays = (int) ((elapsed / 86400) % 512);

        latchedHalt = halt;
        latchedCarry = carry;

        latched = true;
    }

    @Override
    public void tick(int cycles) {
        // no need to tick. Saves us the sync and persists on power off.
        // conceptually not close to hardware but behaviorally more similar
    }

    @Override
    public void reset() {
        baseEpochSeconds = now();
        haltedElapsedSeconds = 0;
        halt = false;
        carry = false;
        latched = false;
    }
}
