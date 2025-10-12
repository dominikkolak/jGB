package cpu.instructions.enums;

public enum OpCode {

    // Basic opcodes - more later
    NOP(0x00),
    LD_A_N(0x3E),
    LD_B_N(0x06),
    LD_C_N(0x0E),
    ADD_A_B(0x80),
    JP_NN(0xC3);

    private final int value;

    OpCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static OpCode fromByte(int opcode) {
        for (OpCode op : values()) {
            if (op.value == opcode) {
                return op;
            }
        }
        return null;
    }
}
