package cpu.instruction;

import java.util.function.Function;

public record Instruction(int length, String mnemonic, Function<ExecutionContext, CycleState> execute) {

    public static Instruction create(int length, String mnemonic, Function<ExecutionContext, CycleState> execute) {
        return new Instruction(length, mnemonic, execute);
    }

    public CycleState execute(ExecutionContext ctx) {
        return execute.apply(ctx);
    }

    public int getLength() { return this.length; }

}
