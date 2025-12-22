import cpu.CPU;
import cpu.instructions.enums.Register8;
import cpu.interfaces.BUS;

public class Main {
    public static void main(String[] args) {

        testBasicArithmetic();
        testRegisterLoads();
        testIncrementDecrement();
        testSubtraction();
        testCarryFlag();

    }


    private static void testBasicArithmetic() {

        CPU cpu = new CPU();
        BUS bus = new SimpleBus(0x10000);

        // Program: LD A, 5 ; LD B, 3 ; ADD A, B
        byte[] program = {
                (byte) 0x3E, (byte) 0x05,
                (byte) 0x06, (byte) 0x03,
                (byte) 0x80
        };
        ((SimpleBus)bus).loadProgram(0x0100, program);

        cpu.step(bus); // LD A, 5
        cpu.step(bus); // LD B, 3
        cpu.step(bus); // ADD A, B

        int a = cpu.getRegisters().read8(Register8.A) & 0xFF;
        int b = cpu.getRegisters().read8(Register8.B) & 0xFF;

        System.out.printf("E: A=0x08, B=0x03\n");
        System.out.printf("R: A=0x%02X, B=0x%02X\n", a, b);
        System.out.println();

    }

    private static void testRegisterLoads() {

        CPU cpu = new CPU();
        BUS bus = new SimpleBus(0x10000);

        // Program: LD A, 0x42 ; LD B, A ; LD C, B
        byte[] program = {
                (byte) 0x3E, (byte) 0x42,
                (byte) 0x47,
                (byte) 0x4F
        };
        ((SimpleBus)bus).loadProgram(0x0100, program);

        cpu.step(bus); // LD A, 0x42
        cpu.step(bus); // LD B, A
        cpu.step(bus); // LD C, A

        int a = cpu.getRegisters().read8(Register8.A) & 0xFF;
        int b = cpu.getRegisters().read8(Register8.B) & 0xFF;
        int c = cpu.getRegisters().read8(Register8.C) & 0xFF;

        System.out.printf("E: A=0x42, B=0x42, C=0x42\n");
        System.out.printf("R: A=0x%02X, B=0x%02X, C=0x%02X\n", a, b, c);

        System.out.println();

    }

    private static void testIncrementDecrement() {

        CPU cpu = new CPU();
        BUS bus = new SimpleBus(0x10000);

        // Program: LD A, 10 ; INC A ; DEC A ; DEC A
        byte[] program = {
                (byte) 0x3E, (byte) 0x0A,
                (byte) 0x3C,
                (byte) 0x3D,
                (byte) 0x3D
        };
        ((SimpleBus)bus).loadProgram(0x0100, program);

        cpu.step(bus); // LD A, 10
        cpu.step(bus); // INC A
        cpu.step(bus); // DEC A
        cpu.step(bus); // DEC A

        int a = cpu.getRegisters().read8(Register8.A) & 0xFF;

        System.out.printf("E: A=0x09\n");
        System.out.printf("R: A=0x%02X\n", a);
        System.out.println();

    }

    private static void testSubtraction() {

        CPU cpu = new CPU();
        BUS bus = new SimpleBus(0x10000);

        // Program: LD A, 20 ; LD B, 7 ; SUB B
        byte[] program = {
                (byte) 0x3E, (byte) 0x14,
                (byte) 0x06, (byte) 0x07,
                (byte) 0x90
        };
        ((SimpleBus)bus).loadProgram(0x0100, program);

        cpu.step(bus); // LD A, 20
        cpu.step(bus); // LD B, 7
        cpu.step(bus); // SUB B

        int a = cpu.getRegisters().read8(Register8.A) & 0xFF;
        boolean subtractFlag = cpu.getRegisters().getFlags().getSubtract();

        System.out.printf("E: A=0x0D (13), N=1\n");
        System.out.printf("R: A=0x%02X (%d), N=%d\n", a, a, subtractFlag ? 1 : 0);
        System.out.println();

    }

    private static void testCarryFlag() {

        CPU cpu = new CPU();
        BUS bus = new SimpleBus(0x10000);

        // Program: LD A, 0xFF ; LD B, 2 ; ADD A, B
        byte[] program = {
                (byte) 0x3E, (byte) 0xFF,
                (byte) 0x06, (byte) 0x02,
                (byte) 0x80 // OVERFLOW
        };
        ((SimpleBus)bus).loadProgram(0x0100, program);

        cpu.step(bus); // LD A, 0xFF
        cpu.step(bus); // LD B, 2
        cpu.step(bus); // ADD A, B

        int a = cpu.getRegisters().read8(Register8.A) & 0xFF;
        boolean carryFlag = cpu.getRegisters().getFlags().getCarry();
        boolean zeroFlag = cpu.getRegisters().getFlags().getZero();

        System.out.printf("E: A=0x01, C=1\n");
        System.out.printf("R: A=0x%02X, C=%d, Z=%d\n", a, carryFlag ? 1 : 0, zeroFlag ? 1 : 0);
        System.out.println();

    }
}