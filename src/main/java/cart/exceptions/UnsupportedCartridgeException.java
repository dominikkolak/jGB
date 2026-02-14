package cart.exceptions;

public class UnsupportedCartridgeException extends RuntimeException {
    public UnsupportedCartridgeException(String message) {
        super(message);
    }
}