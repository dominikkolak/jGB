package cpu.instructions;

public class InstructionSet {

    public static int getInstructionSize(int opcode) {
        if (opcode == 0xCB) {
            return 2; // CB prefix = 2 Byte
        }

        // Map opcodes to sizes
        // this is very basic and need to be expaned later
        switch (opcode) {
            // 1BI
            case 0x00: // NOP
            case 0x76: // HALT
            case 0x10: // STOP
                return 1;

            // 2BI (opcode + immediate)
            case 0x06: case 0x0E: case 0x16: case 0x1E: // LD r, n
            case 0x26: case 0x2E: case 0x3E:
                return 2;

            // 3BI (opcode + 16-bit address or imm)
            case 0xC3: // JP nn
            case 0xCD: // CALL nn
                return 3;

            default:
                return 1; // Default 1 byte
        }
    }

    // this is just a prototype, needs actual implementation later
    public static int getInstructionCycles(int opcode) {
        switch (opcode) {
            case 0x00: return 1; // NOP
            case 0x3E: return 2; // LD A, n
            case 0xC3: return 4; // JP nn
            default: return 1;
        }
    }

    public static boolean isValidOpcode(int opcode) {
        return opcode >= 0x00 && opcode <= 0xFF;
    }

}
