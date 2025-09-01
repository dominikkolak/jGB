package cpu.instructions.enums;

public enum AluOperation {
    ADD,  // Addition
    ADC,  // Add with carry
    SUB,  // Subtraction
    SBC,  // Subtract with carry
    AND,  // Bitwise AND
    OR,   // Bitwise OR
    XOR,  // Bitwise XOR
    CP,   // Compare (this one doesn't store!)
    INC,  // Increment
    DEC,  // Decrement
    SWAP, // Swap nibbles
    DAA,  // Decimal adjust accumulator
    CPL,  // Complement accumulator
    CCF,  // Complement carry flag
    SCF,  // Set carry flag

    RLCA, // Rotate left circular accumulator
    RLA,  // Rotate left accumulator
    RRCA, // Rotate right circular accumulator
    RRA,  // Rotate right accumulator
    RLC,  // Rotate left circular
    RL,   // Rotate left
    RRC,  // Rotate right circular
    RR,   // Rotate right
    SLA,  // Shift left arithmetic
    SRA,  // Shift right arithmetic
    SRL,  // Shift right logical

    BIT,  // Test bit
    SET,  // Set bit
    RES   // Reset bit
}
