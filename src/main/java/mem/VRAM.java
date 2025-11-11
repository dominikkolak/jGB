package mem;

import shared.Addressable;
import shared.Component;

import java.util.Arrays;

public class VRAM implements Addressable, Component {

    private final byte[] memory = new byte[MemoryConstants.VRAM_SIZE];

    @Override
    public boolean accepts(int address) {
        return address >= MemoryConstants.VRAM_START && address < MemoryConstants.VRAM_END;
    }

    @Override
    public void reset() {
        Arrays.fill(memory, (byte) 0);
    }

    @Override
    public byte read(int address) {
        return memory[(address - MemoryConstants.VRAM_START) & (MemoryConstants.VRAM_SIZE - 1)];
    }

    @Override
    public void write(int address, int value) {
        memory[(address - MemoryConstants.VRAM_START) & (MemoryConstants.VRAM_SIZE - 1)] = (byte) value;
    }

    public byte[] directMemoryAccess() {
        return memory;
    }
}
