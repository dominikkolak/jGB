package cart.rom;

import cart.constants.CartridgeConstants;

public class ROM implements ReadOnlyMemory {

    private final byte[] data;

    public ROM(byte[] data) {
        if (data == null || data.length == 0) { throw new IllegalArgumentException("ROM data null or empty"); }
        if (data.length < CartridgeConstants.MIN_ROM_SIZE) { throw new IllegalArgumentException("Minimum Rom Size: " + CartridgeConstants.MIN_ROM_SIZE); }

        this.data = data;
    }

    @Override
    public byte read(int address) {
        if (address < 0 || address >= data.length) {
            return (byte) 0xFF;
        }
        return data[address];
    }

    @Override
    public int getSize() {
        return data.length;
    }

}
