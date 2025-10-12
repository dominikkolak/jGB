package cpu.cpu.register;

import cpu.register.enums.R16;
import cpu.register.enums.R8;

public interface Registers {

    int read(R8 register);
    void write(R8 register, int value);

    int read(R16 register);
    void write(R16 register, int value);

    int getPC();
    void setPC(int value);
    int getAndIncPC();
    void addToPC(int offset);

    int getSP();
    void setSP(int value);
    int decAndGetSP();
    int getAndIncSP();
    void addToSP(int offset);

    default void inc(R8 register) { write(register, (read(register) + 1) & 0xFFFF); }
    default void inc(R16 register) { write(register, (read(register) + 1) & 0xFFFF); }

}
