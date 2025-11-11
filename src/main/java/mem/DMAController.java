package mem;

import shared.Clocked;
import shared.Component;

public class DMAController implements Clocked, Component {

    private final OAM oam;
    private DMAMemoryAccess memory;

    private boolean active;
    private int sourceAddress;
    private int currentByte;
    private int cycleCounter;

    private int DMARegister;

    public DMAController(OAM oam) {
        this.oam = oam;
        reset();
    }

    public void setMemory(DMAMemoryAccess memory) {
        this.memory = memory;
    }

    public int read() {
        return DMARegister;
    }

    public void write(int value) {
        DMARegister = value & 0xFF;
        startTransfer(DMARegister);
    }

    private void startTransfer(int sourceHigh) {
        sourceAddress = (sourceHigh & 0xFF) << 8;
        currentByte = 0;
        cycleCounter = 0;
        active = true;
    }

    @Override
    public void tick(int cycles) {
        if (!active || memory == null) {
            return;
        }

        cycleCounter += cycles;

        while (cycleCounter >= MemoryConstants.CYCLES_PER_BYTE && currentByte < MemoryConstants.DMA_LENGTH) {
            cycleCounter -= MemoryConstants.CYCLES_PER_BYTE;

            byte data = memory.read(sourceAddress + currentByte);
            oam.directMemoryAccessWrite(currentByte, data);

            currentByte++;
        }

        if (currentByte >= MemoryConstants.DMA_LENGTH) {
            active = false;
        }
    }

    public boolean isActive() { return active; }

    public int getCurrentByte() { return currentByte; }

    public int getRemainingCycles() {
        return active ? (MemoryConstants.DMA_LENGTH - currentByte) * MemoryConstants.CYCLES_PER_BYTE : 0;
    }

    @Override
    public void reset() {
        active = false;
        sourceAddress = 0;
        currentByte = 0;
        cycleCounter = 0;
        DMARegister = 0xFF;
    }
}