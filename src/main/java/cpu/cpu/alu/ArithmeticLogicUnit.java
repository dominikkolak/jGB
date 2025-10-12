package cpu.cpu.alu;

import cpu.alu.result.ALUR16;
import cpu.alu.result.ALUR8;
import cpu.alu.result.FLAGR;

public interface ArithmeticLogicUnit {

    ALUR8 add8(int a, int b);
    ALUR8 adc(int a, int b, boolean carry);
    ALUR8 sub(int a, int b);
    ALUR8 sbc(int a, int b, boolean carry);
    ALUR8 cp(int a, int b);
    ALUR8 inc8(int a);
    ALUR8 dec8(int a);

    ALUR8 and(int a, int b);
    ALUR8 or(int a, int b);
    ALUR8 xor(int a, int b);
    ALUR8 cpl(int a);

    ALUR8 addSigned(int base, int offset);

    ALUR16 add16(int a, int b);
    int inc16(int a);
    int dec16(int a);

    ALUR8 rlc(int a);
    ALUR8 rrc(int a);
    ALUR8 rl(int a, boolean carry);
    ALUR8 rr(int a, boolean carry);

    ALUR8 sla(int a);
    ALUR8 sra(int a);
    ALUR8 srl(int a);
    ALUR8 swap(int a);

    FLAGR bit(int a, int bit);
    int set(int a, int bit);
    int rst(int a, int bit);

    ALUR8 daa(int a, boolean subtract, boolean halfCarry, boolean carry);
    ALUR8 scf(int a);
    ALUR8 ccf(int a, boolean carry);

}
