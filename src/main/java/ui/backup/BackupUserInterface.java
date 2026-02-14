package ui.backup;

import atlantafx.base.theme.PrimerDark;
import cart.util.CartridgeLoader;
import core.Overlord;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuBar;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ui.components.InputHandler;

import java.io.File;
import java.io.IOException;

public class BackupUserInterface extends Application {

    private static final int SCALE = 3;
    private static final int WIDTH = 160;
    private static final int HEIGHT = 144;

    private Canvas canvas;
    private WritableImage image;
    private int[] argbBuffer;
    private AnimationTimer gameLoop;
    private InputHandler inputHandler;

    private Overlord emulator;

    private Label statusLabel;
    private Label fpsLabel;

    private static final int[][] PALETTES = {
            { 0xFFE0F8D0, 0xFF88C070, 0xFF346856, 0xFF081820 },
            { 0xFFFFFFFF, 0xFFAAAAAA, 0xFF555555, 0xFF000000 },
            { 0xFF9BBC0F, 0xFF8BAC0F, 0xFF306230, 0xFF0F380F },
            { 0xFFC4CFA1, 0xFF8B956D, 0xFF4D533C, 0xFF1F1F1F }
    };
    private int currentPalette = 0;

    @Override
    public void start(Stage primaryStage) {

        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        emulator = new Overlord();
        inputHandler = new InputHandler();
        emulator.setInputProvider(inputHandler);
        emulator.setFrameLimitEnabled(true);

        Thread emuThread = new Thread(emulator, "Emulator Thread");
        emuThread.setDaemon(true);
        emuThread.start();

        canvas = new Canvas(WIDTH * SCALE, HEIGHT * SCALE);
        image = new WritableImage(WIDTH, HEIGHT);
        argbBuffer = new int[WIDTH * HEIGHT];

        javafx.scene.control.MenuBar menuBar = createMenuBar(primaryStage);

        statusLabel = new Label("No ROM loaded");
        fpsLabel = new Label("FPS: 0");
        BorderPane statusBar = new BorderPane();
        statusBar.setLeft(statusLabel);
        statusBar.setRight(fpsLabel);
        statusBar.setStyle("-fx-padding: 5; -fx-background-color: #f0f0f0;");

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(canvas);
        root.setBottom(statusBar);

        Scene scene = new Scene(root);

        scene.setOnKeyPressed(e -> {
            inputHandler.keyPressed(e.getCode());
            switch (e.getCode()) {
                case P -> emulator.togglePause();
                case R -> { if (emulator.isCartridgeLoaded()) emulator.reset(); }
                case ESCAPE -> Platform.exit();
            }
        });
        scene.setOnKeyReleased(e -> inputHandler.keyReleased(e.getCode()));

        primaryStage.setTitle("Java Game Boy Emulator");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.rgb(0x08, 0x18, 0x20));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gameLoop = new AnimationTimer() {
            private long lastFpsUpdate = 0;
            private int frameCount = 0;


            @Override
            public void handle(long now) {
                if (emulator.isCartridgeLoaded() && !emulator.isPaused()) {
                    int[] frame = emulator.getFrame();
                    renderFrame(frame);
                }

                frameCount++;
                if (now - lastFpsUpdate >= 500_000_000L) {
                    double fps = frameCount * 1_000_000_000.0 / (now - lastFpsUpdate);
                    fpsLabel.setText(String.format("FPS: %.1f", fps));
                    frameCount = 0;
                    lastFpsUpdate = now;
                }

                if (emulator.isPaused()) {
                    statusLabel.setText("PAUSED");
                } else if (emulator.isCartridgeLoaded()) {
                    statusLabel.setText("Running - Frame: " + emulator.getFrameCount());
                }
            }
        };
        gameLoop.start();

        primaryStage.setOnCloseRequest(e -> {
            gameLoop.stop();
            Platform.exit();
        });

    }

    private javafx.scene.control.MenuBar createMenuBar(Stage stage) {
        javafx.scene.control.MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open ROM...");
        openItem.setOnAction(e -> openRom(stage));
        MenuItem resetItem = new MenuItem("Reset");
        resetItem.setOnAction(e -> { if (emulator.isCartridgeLoaded()) emulator.reset(); });
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());
        fileMenu.getItems().addAll(openItem, resetItem, new SeparatorMenuItem(), exitItem);

        Menu emuMenu = new Menu("Emulation");
        MenuItem pauseItem = new MenuItem("Pause/Resume (P)");
        pauseItem.setOnAction(e -> emulator.togglePause());
        CheckMenuItem frameLimitItem = new CheckMenuItem("Frame Limit");
        frameLimitItem.setSelected(true);
        frameLimitItem.setOnAction(e -> emulator.setFrameLimitEnabled(frameLimitItem.isSelected()));
        emuMenu.getItems().addAll(pauseItem, frameLimitItem);

        Menu viewMenu = new Menu("View");
        ToggleGroup paletteGroup = new ToggleGroup();
        String[] paletteNames = { "Classic Green", "Grayscale", "Original DMG", "Pocket" };
        for (int i = 0; i < paletteNames.length; i++) {
            RadioMenuItem item = new RadioMenuItem(paletteNames[i]);
            item.setToggleGroup(paletteGroup);
            if (i == 0) item.setSelected(true);
            final int paletteIndex = i;
            item.setOnAction(e -> currentPalette = paletteIndex);
            viewMenu.getItems().add(item);
        }

        menuBar.getMenus().addAll(fileMenu, emuMenu, viewMenu);
        return menuBar;
    }

    private void openRom(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open ROM File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Game Boy ROMs", "*.gb", "*.gbc"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                byte[] rom = CartridgeLoader.loadRom(file);
                emulator.loadCartridge(rom);
                statusLabel.setText("Loaded: " + file.getName());
            } catch (IOException ex) {
                showError("Failed to load ROM", ex.getMessage());
            } catch (Exception ex) {
                showError("Invalid ROM", ex.getMessage());
            }
        }
    }

    private void renderFrame(int[] gbFrame) {
        int[] palette = PALETTES[currentPalette];

        for (int i = 0; i < gbFrame.length; i++) {
            argbBuffer[i] = palette[gbFrame[i] & 0x03];
        }

        PixelWriter writer = image.getPixelWriter();
        writer.setPixels(0, 0, WIDTH, HEIGHT,
                PixelFormat.getIntArgbInstance(), argbBuffer, 0, WIDTH);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
