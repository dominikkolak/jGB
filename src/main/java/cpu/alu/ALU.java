package cpu.alu;

import cpu.alu.result.ALUR16;
import cpu.alu.result.ALUR8;
import cpu.alu.result.FLAGR;

public class ALU implements ArithmeticLogicUnit{


    @Override
    public ALUR8 add8(int a, int b) {
        a &= 0xFF;
        b &= 0xFF;
        int result = a + b;

        return ALUR8.add(
                result,
                (a & 0xF) + (b & 0xF) > 0xF,
                result > 0xFF
        );
    }

    @Override
    public ALUR8 adc(int a, int b, boolean carry) {
        a &= 0xFF;
        b &= 0xFF;
        int c = carry ? 1 : 0;
        int result = a + b + c;

        return ALUR8.add(
                result,
                (a & 0xF) + (b & 0xF) + c > 0xF,
                result > 0xFF
        );
    }

    @Override
    public ALUR8 sub(int a, int b) {
        a &= 0xFF;
        b &= 0xFF;
        int result = a - b;

        return ALUR8.sub(
                result,
                (a & 0xF) < (b & 0xF),
                a < b
        );
    }

    @Override
    public ALUR8 sbc(int a, int b, boolean carry) {
        a &= 0xFF;
        b &= 0xFF;
        int c = carry ? 1 : 0;
        int result = a - b - c;

        return ALUR8.sub(
                result,
                (a & 0xF) < (b & 0xF) + c,
                a < b + c
        );
    }

    @Override
    public ALUR8 cp(int a, int b) {
        return sub(a, b);
    }

    @Override
    public ALUR8 inc8(int a) {
        a &= 0xFF;
        int result = a + 1;

        return ALUR8.result8(
                result,
                false,
                (a & 0xF) == 0xF,
                false
        );
    }

    @Override
    public ALUR8 dec8(int a) {
        a &= 0xFF;
        int result = a - 1;

        return ALUR8.result8(
                result,
                true,
                (a & 0xF) == 0,
                false
        );
    }

    @Override
    public ALUR8 and(int a, int b) {
        int result = (a & b) & 0xFF;

        return ALUR8.logic(
                result,
                true
        );
    }

    @Override
    public ALUR8 or(int a, int b) {
        int result = (a | b) & 0xFF;

        return ALUR8.logic(
                result,
                false
        );
    }

    @Override
    public ALUR8 xor(int a, int b) {
        int result = (a ^ b) & 0xFF;

        return ALUR8.logic(
                result,
                false
        );
    }

    @Override
    public ALUR8 cpl(int a) {
        int result = (~a) & 0xFF;

        return ALUR8.result8(
                result,
                true,
                true,
                false
        );
    }

    @Override
    public ALUR8 addSigned(int base, int offset) {
        base &= 0xFFFF;
        offset = (byte) offset;
        int result = (base + offset) & 0xFFFF;

        int unsignedOffset = offset & 0xFF;
        int lowByte = base & 0xFF;

        return ALUR8.result16(
                result,
                false,
                (lowByte & 0xF) + (unsignedOffset & 0xF) > 0xF,
                lowByte + unsignedOffset > 0xFF
        );
    }

    @Override
    public ALUR16 add16(int a, int b) {
        return ALUR16.add(a & 0xFFFF, b & 0xFFFF);
    }

    @Override
    public int inc16(int a) {
        return (a + 1) & 0xFFFF;
    }

    @Override
    public int dec16(int a) {
        return (a - 1) & 0xFFFF;
    }

    @Override
    public ALUR8 rlc(int a) {
        a &= 0xFF;
        int bit7 = (a >> 7) & 1;
        int result = ((a << 1) | bit7) & 0xFF;

        return ALUR8.shift(
                result,
                bit7 == 1
        );
    }

    @Override
    public ALUR8 rrc(int a) {
        a &= 0xFF;
        int bit0 = a & 1;
        int result = ((a >> 1) | (bit0 << 7)) & 0xFF;

        return ALUR8.shift(
                result,
                bit0 == 1
        );
    }

    @Override
    public ALUR8 rl(int a, boolean carry) {
        a &= 0xFF;
        int bit7 = (a >> 7) & 1;
        int result = ((a << 1) | (carry ? 1 : 0)) & 0xFF;

        return ALUR8.shift(
                result,
                bit7 == 1
        );
    }

    @Override
    public ALUR8 rr(int a, boolean carry) {
        a &= 0xFF;
        int bit0 = a & 1;
        int result = ((a >> 1) | (carry ? 0x80 : 0)) & 0xFF;

        return ALUR8.shift(result,
                bit0 == 1
        );
    }

    @Override
    public ALUR8 sla(int a) {
        a &= 0xFF;
        int bit7 = (a >> 7) & 1;
        int result = (a << 1) & 0xFF;

        return ALUR8.shift(
                result,
                bit7 == 1
        );
    }

    @Override
    public ALUR8 sra(int a) {
        a &= 0xFF;
        int bit0 = a & 1;
        int bit7 = a & 0x80;
        int result = ((a >> 1) | bit7) & 0xFF;

        return ALUR8.shift(result,
                bit0 == 1
        );
    }

    @Override
    public ALUR8 srl(int a) {
        a &= 0xFF;
        int bit0 = a & 1;
        int result = (a >> 1) & 0xFF;

        return ALUR8.shift(result,
                bit0 == 1
        );
    }

    @Override
    public ALUR8 swap(int a) {
        a &= 0xFF;
        int result = ((a & 0x0F) << 4) | ((a & 0xF0) >> 4);

        return ALUR8.shift(result,
                false
        );
    }

    @Override
    public FLAGR bit(int a, int bit) {
        return FLAGR.forBit(a & 0xFF, bit);
    }

    @Override
    public int set(int a, int bit) {
        return (a | (1 << bit)) & 0xFF;
    }

    @Override
    public int rst(int a, int bit) {
        return (a & ~(1 << bit)) & 0xFF;
    }

    @Override
    public ALUR8 daa(int a, boolean subtract, boolean halfCarry, boolean carry) {
        a &= 0xFF;
        int correction = 0;
        boolean newCarry = carry;

        if (!subtract) {
            if (carry || a > 0x99) {
                correction |= 0x60;
                newCarry = true;
            }
            if (halfCarry || (a & 0x0F) > 0x09) {
                correction |= 0x06;
            }
            a += correction;
        } else {
            if (carry) {
                correction |= 0x60;
            }
            if (halfCarry) {
                correction |= 0x06;
            }
            a -= correction;
        }

        a &= 0xFF;

        return ALUR8.result8(a, subtract, false, newCarry);
    }

    @Override
    public ALUR8 scf(int a) {
        return ALUR8.result8(a & 0xFF, false, false, true);
    }

    @Override
    public ALUR8 ccf(int a, boolean carry) {
        return ALUR8.result8(a & 0xFF, false, false, !carry);
    }
}
