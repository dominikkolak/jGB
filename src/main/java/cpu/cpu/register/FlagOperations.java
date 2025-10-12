package cpu.cpu.register;

import cpu.alu.result.ALUR8;
import cpu.register.enums.CONDITION;
import cpu.register.enums.FLAG;

public interface FlagOperations {

    boolean getFlag(FLAG flag);
    void setFlag(FLAG flag, boolean value);

    void setAllFlags(boolean z, boolean n, boolean h, boolean c);
    void setFlags(Boolean z, Boolean n, Boolean h, Boolean c);

    void setFlags(ALUR8 result);

    int getFlagRegister();
    void setFlagRegister(int value);

    boolean checkCondition(CONDITION condition);
}
