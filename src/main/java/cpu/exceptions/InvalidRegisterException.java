package cpu.exceptions;

public class InvalidRegisterException extends RuntimeException {
    private final String register;

    public InvalidRegisterException(String register) {
        super("Invalid register: " + register);
        this.register = register;
    }

    public String getRegister() {
        return register;
    }
}
