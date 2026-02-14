package ui.output;

import io.SerialOutputListener;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SerialOutputHandler implements SerialOutputListener {

    public enum OutputMode {
        NONE,
        CONSOLE,
        FILE,
        BOTH
    }

    private volatile OutputMode mode = OutputMode.NONE;
    private volatile Path outputFile = null;
    private volatile BufferedWriter fileWriter = null;
    private final StringBuilder buffer = new StringBuilder();

    private volatile SerialOutputListener uiCallback = null;

    public SerialOutputHandler() {
    }

    public void setMode(OutputMode mode) {
        if (this.mode != mode) {
            closeFile();
            this.mode = mode;

            if (mode == OutputMode.FILE || mode == OutputMode.BOTH) {
                initializeFile();
            }
        }
    }

    public OutputMode getMode() {
        return mode;
    }

    public void setOutputFile(Path path) {
        closeFile();
        this.outputFile = path;

        if (mode == OutputMode.FILE || mode == OutputMode.BOTH) {
            initializeFile();
        }
    }

    public Path getOutputFile() {
        return outputFile;
    }

    public void setUICallback(SerialOutputListener callback) {
        this.uiCallback = callback;
    }

    private void initializeFile() {
        try {
            if (outputFile == null) {
                String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                outputFile = Path.of("serial_output_" + timestamp + ".txt");
            }

            fileWriter = Files.newBufferedWriter(
                    outputFile,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

            fileWriter.write("=== Serial Output Log ===\n");
            fileWriter.write("Started: " + LocalDateTime.now() + "\n");
            fileWriter.write("=========================\n");
            fileWriter.flush();

        } catch (IOException e) {
            System.err.println("Failed to open serial output file: " + e.getMessage());
            mode = (mode == OutputMode.BOTH) ? OutputMode.CONSOLE : OutputMode.NONE;
            fileWriter = null;
        }
    }

    private void closeFile() {
        if (fileWriter != null) {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.err.println("Error closing serial output file: " + e.getMessage());
            }
            fileWriter = null;
        }
    }

    @Override
    public void onSerialOutput(char c) {
        buffer.append(c);

        switch (mode) {
            case CONSOLE -> writeToConsole(c);
            case FILE -> writeToFile(c);
            case BOTH -> {
                writeToConsole(c);
                writeToFile(c);
            }
        }

        if (uiCallback != null) {
            uiCallback.onSerialOutput(c);
        }
    }

    private void writeToConsole(char c) {
        System.out.print(c);
        System.out.flush();
    }

    private void writeToFile(char c) {
        if (fileWriter != null) {
            try {
                fileWriter.write(c);

                if (c == '\n') {
                    fileWriter.flush();
                }
            } catch (IOException e) {
                System.err.println("Error writing to serial output file: " + e.getMessage());
            }
        }
    }

    public String getBufferedOutput() {
        synchronized (buffer) {
            return buffer.toString();
        }
    }

    public void clearBuffer() {
        synchronized (buffer) {
            buffer.setLength(0);
        }
    }

    public void flush() {
        if (fileWriter != null) {
            try {
                fileWriter.flush();
            } catch (IOException e) {
                System.err.println("Error flushing serial output: " + e.getMessage());
            }
        }
    }

    public void close() {
        flush();
        closeFile();
    }
}