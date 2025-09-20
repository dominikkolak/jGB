package cart.util;

import cart.constants.CartridgeConstants;
import cart.exceptions.InvalidCartridgeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CartridgeLoader {

    public static byte[] loadRom(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) { throw new IllegalArgumentException("FilePath is Empty"); }

        Path path = Paths.get(filePath);

        if (!Files.exists(path)) { throw new IllegalArgumentException("ROM not Found"); }
        if (!Files.isRegularFile(path)) { throw new IOException("Path is not a File"); }
        if (!Files.isReadable(path)) { throw new IOException("ROM is not readable"); }

        byte[] romData = Files.readAllBytes(path);

        if (romData.length < CartridgeConstants.HEADER_END + 1) { throw new InvalidCartridgeException("ROM file does not contain space for header"); }
        if (romData.length < CartridgeConstants.MIN_ROM_SIZE) { throw new InvalidCartridgeException("ROM file too small: " + romData.length + " < " + CartridgeConstants.MIN_ROM_SIZE); }

        return romData;
    }

    private CartridgeLoader() {
        throw new AssertionError("No instantiation for Utility classes");
    }

}
