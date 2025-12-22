package cpu.instructions.types;

import cpu.components.ALU;
import cpu.components.RegisterFile;
import cpu.instructions.Instruction;
import cpu.instructions.enums.Register16;
import cpu.instructions.enums.Register8;
import cpu.interfaces.BUS;

/// DONE!

public class LoadInstruction implements Instruction {

    private enum LoadType {
        REG8_IMM,    // LD r, n
        REG8_REG8,   // LD r, r'
        REG16_IMM,   // LD rr, nn
        MEM_REG8,    // LD [HL], r
        REG8_MEM     // LD r, [HL]
    }

    private final LoadType type;
    private final Register8 destReg8;
    private final Register8 srcReg8;
    private final Register16 destReg16;
    private final int immediate;

    // LD r, n
    public LoadInstruction(Register8 dest, byte value) {
        this.type = LoadType.REG8_IMM;
        this.destReg8 = dest;
        this.srcReg8 = null;
        this.destReg16 = null;
        this.immediate = value & 0xFF;
    }

    // LD r, r'
    public LoadInstruction(Register8 dest, Register8 src) {
        this.type = LoadType.REG8_REG8;
        this.destReg8 = dest;
        this.srcReg8 = src;
        this.destReg16 = null;
        this.immediate = 0;
    }

    // LD rr, nn
    public LoadInstruction(Register16 dest, int value) {
        this.type = LoadType.REG16_IMM;
        this.destReg8 = null;
        this.srcReg8 = null;
        this.destReg16 = dest;
        this.immediate = value & 0xFFFF;
    }

    @Override
    public int execute(RegisterFile registers, ALU alu, BUS bus) {
        switch (type) {
            case REG8_IMM:
                registers.write8(destReg8, (byte) immediate);
                return 2; // 8 cycles

            case REG8_REG8:
                byte value = registers.read8(srcReg8);
                registers.write8(destReg8, value);
                return 1; // 4 cycles

            case REG16_IMM:
                registers.write16(destReg16, immediate);
                return 3; // 12 cycles

            default:
                return 1;
        }
    }

    @Override
    public String getMnemonic() {
        switch (type) {
            case REG8_IMM:
                return String.format("LD %s, 0x%02X", destReg8, immediate);
            case REG8_REG8:
                return String.format("LD %s, %s", destReg8, srcReg8);
            case REG16_IMM:
                return String.format("LD %s, 0x%04X", destReg16, immediate);
            default:
                return "LD";
        }
    }

    @Override
    public int getSize() {
        switch (type) {
            case REG8_IMM: return 2;
            case REG8_REG8: return 1;
            case REG16_IMM: return 3;
            default: return 1;
        }
    }

}
