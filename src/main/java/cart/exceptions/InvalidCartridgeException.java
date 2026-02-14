package cart.exceptions;

public class InvalidCartridgeException extends RuntimeException {
    public InvalidCartridgeException(String message) {
        super(message);
    }
}