package mem;

import shared.Addressable;
import shared.Component;

import java.util.Arrays;

public class OAM implements Addressable, Component {

    private final byte[] memory = new byte[MemoryConstants.OAM_SIZE];

    @Override
    public boolean accepts(int address) {
        return address >= MemoryConstants.OAM_START && address <= MemoryConstants.OAM_END;
    }

    @Override
    public void reset() {
        Arrays.fill(memory, (byte) 0);
    }

    @Override
    public byte read(int address) {
        return memory[(address - MemoryConstants.OAM_START) & 0xFF];
    }

    @Override
    public void write(int address, int value) {
        int index = (address - MemoryConstants.OAM_START) & 0xFF;
        if (index < MemoryConstants.OAM_SIZE) {
            memory[index] = (byte) value;
        }
    }

    public byte[] directMemoryAccess() {
        return memory;
    }

    public void directMemoryAccessWrite(int index, byte value) {
        if (index < MemoryConstants.OAM_SIZE) {
            memory[index] = value;
        }
    }

}
