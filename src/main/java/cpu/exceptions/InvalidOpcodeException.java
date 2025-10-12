package cpu.exceptions;

public class InvalidOpcodeException extends RuntimeException {
    private final int opcode;
    private final int address;

    public InvalidOpcodeException(int opcode, int address) {
        super(String.format("Invalid opcode: 0x%02X at 0x%04X", opcode, address));
        this.opcode = opcode;
        this.address = address;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getAddress() {
        return address;
    }
}
