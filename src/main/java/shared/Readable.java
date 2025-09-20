package shared;

public interface Readable {
    byte read(int address);

    default int read8(int address) {
        return read(address & 0xFFFF) & 0xFF;
    }

    // dont use for instructions, microOp Approach, should be 2 read8s in separate cycles!
    default int read16(int address) {
        return (read8(address + 1) << 8) | read8(address);
    }

    default int read8Signed(int address) {
        return (byte) read(address & 0xFFFF);
    }
}
