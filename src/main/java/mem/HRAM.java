package mem;

import shared.Addressable;
import shared.Component;

public class HRAM implements Addressable, Component {

    private final byte[] memory = new byte[MemoryConstants.HRAM_SIZE];

    @Override
    public boolean accepts(int address) {
        return address >= MemoryConstants.HRAM_START && address < MemoryConstants.HRAM_END;
    }

    @Override
    public void reset() {
        java.util.Arrays.fill(memory, (byte) 0);
    }

    @Override
    public byte read(int address) {
        return memory[(address - MemoryConstants.HRAM_START) & 0x7F];
    }

    @Override
    public void write(int address, int value) {
        memory[(address - MemoryConstants.HRAM_START) & 0x7F] = (byte) value;
    }
}
