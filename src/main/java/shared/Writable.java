package shared;

public interface Writable {
    void write(int address, int value);

    default void write8(int address, int value) {
        write(address & 0xFFFF, value & 0xFF);
    }

    // dont use for instructions, microOp Approach, should be 2 write8s in separate cycles!
    default void write16(int address, int value) {
        write8(address, value);
        write8(address + 1, value >> 8);
    }
}