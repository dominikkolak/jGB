package cpu.cpu.interrupt;

import cpu.interrupt.InterruptRequester;
import cpu.register.enums.INTERRUPT;
import mem.MemoryConstants;
import shared.Addressable;
import shared.Clocked;
import shared.Component;

public class Timer implements Clocked, Addressable, Component {

    private final InterruptRequester interrupts;

    private int internalCounter;

    private int tima;
    private int tma;
    private int tac;

    private boolean previousBit;

    private boolean overflowPending;
    private int overflowDelay;

    public Timer(InterruptRequester interrupts) {
        this.interrupts = interrupts;
        reset();
    }

    @Override
    public boolean accepts(int address) {
        return address >= MemoryConstants.DIV && address <= MemoryConstants.TAC;
    }

    @Override
    public void tick(int cycles) {
        for (int i = 0; i < cycles; i++) {
            tickOnce();
        }
    }

    private void tickOnce() {
        if (overflowPending) {
            overflowDelay--;
            if (overflowDelay <= 0) {
                tima = tma;
                interrupts.request(INTERRUPT.TIMER);
                overflowPending = false;
            }
        }

        internalCounter = (internalCounter + 1) & 0xFFFF;

        if (isTimerEnabled()) {
            boolean currentBit = getSelectedBit();

            if (previousBit && !currentBit) {
                incrementTIMA();
            }

            previousBit = currentBit;
        }
    }

    private boolean isTimerEnabled() {
        return (tac & MemoryConstants.TAC_ENABLED) != 0;
    }

    private boolean getSelectedBit() {
        int bitPosition = MemoryConstants.CLOCK_BITS[tac & MemoryConstants.TAC_CLOCK_MASK];
        return ((internalCounter >> bitPosition) & 1) != 0;
    }

    private void incrementTIMA() {
        tima++;
        if (tima > 0xFF) {
            tima = 0;
            overflowPending = true;
            overflowDelay = 4;
        }
    }

    @Override
    public void reset() {
        internalCounter = 0xABCC;
        tima = 0x00;
        tma = 0x00;
        tac = 0x00;
        previousBit = false;
        overflowPending = false;
        overflowDelay = 0;
    }

    @Override
    public byte read(int address) {
        return switch (address) {
            case MemoryConstants.DIV  -> (byte) ((internalCounter >> 8) & 0xFF);
            case MemoryConstants.TIMA -> (byte) tima;
            case MemoryConstants.TMA  -> (byte) tma;
            case MemoryConstants.TAC  -> (byte) (tac | 0xF8);
            default -> (byte) 0xFF;
        };
    }

    @Override
    public void write(int address, int value) {
        value &= 0xFF;

        switch (address) {
            case MemoryConstants.DIV -> {
                boolean oldBit = getSelectedBit();
                internalCounter = 0;
                boolean newBit = getSelectedBit();

                if (isTimerEnabled() && oldBit && !newBit) {
                    incrementTIMA();
                }
                previousBit = newBit;
            }
            case MemoryConstants.TIMA -> {
                if (!overflowPending) {
                    tima = value;
                }
            }
            case MemoryConstants.TMA -> tma = value;
            case MemoryConstants.TAC -> {
                int oldClockBit = MemoryConstants.CLOCK_BITS[tac & MemoryConstants.TAC_CLOCK_MASK];
                boolean oldEnabled = isTimerEnabled();
                boolean oldBit = oldEnabled && ((internalCounter >> oldClockBit) & 1) != 0;

                tac = value & 0x07;

                int newClockBit = MemoryConstants.CLOCK_BITS[tac & MemoryConstants.TAC_CLOCK_MASK];
                boolean newEnabled = isTimerEnabled();
                boolean newBit = newEnabled && ((internalCounter >> newClockBit) & 1) != 0;

                if (oldBit && !newBit) {
                    incrementTIMA();
                }
                previousBit = newBit;
            }
        }
    }

    public int getDIV() { return (internalCounter >> 8) & 0xFF; }
    public int getTIMA() { return tima; }
    public int getTMA() { return tma; }
    public int getTAC() { return tac; }
    public int getInternalCounter() { return internalCounter; }
}
