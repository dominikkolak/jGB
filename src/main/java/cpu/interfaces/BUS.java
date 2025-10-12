package cpu.interfaces;

public interface BUS {

    int readByte(int address);

    void writeByte(int address, int value);

    default int readWord(int address) {
        int low = readByte(address) & 0xFF;
        int high = readByte((address + 1) & 0xFFFF) & 0xFF;
        return (high << 8) | low;
    }

    default void writeWord(int address, int value) {
        writeByte(address, value & 0xFF);
        writeByte((address + 1) & 0xFFFF, (value >> 8) & 0xFF);
    }

}
