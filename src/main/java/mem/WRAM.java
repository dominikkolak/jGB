package mem;

import shared.Addressable;
import shared.Component;

public class WRAM implements Addressable, Component {

    private final byte[] memory = new byte[MemoryConstants.WRAM_SIZE];

    @Override
    public boolean accepts(int address) {
        return address >= MemoryConstants.WRAM_START && address < MemoryConstants.WRAM_0_END;
    }

    @Override
    public void reset() {
        java.util.Arrays.fill(memory, (byte) 0);
    }

    @Override
    public byte read(int address) {
        return memory[(address - MemoryConstants.WRAM_START) & (MemoryConstants.WRAM_SIZE - 1)];
    }

    @Override
    public void write(int address, int value) {
        memory[(address - MemoryConstants.WRAM_START) & (MemoryConstants.WRAM_SIZE - 1)] = (byte) value;
    }

    public byte[] directMemoryAccess() {
        return memory;
    }

}
