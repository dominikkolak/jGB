package cpu.exceptions;

public class InvalidOpcodeException extends RuntimeException {
    public InvalidOpcodeException(String message) {
        super(message);
    }
}
