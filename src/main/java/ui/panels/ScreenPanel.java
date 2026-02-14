package ui.panels;

import core.Overlord;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ScreenPanel extends StackPane {

    private static final int GB_WIDTH = 160;
    private static final int GB_HEIGHT = 144;
    private static final int INITIAL_SCALE = 3;

    private final Canvas canvas;
    private final WritableImage image;
    private final int[] argbBuffer;

    private final Overlord emulator;
    private AnimationTimer renderLoop;

    public static final int[][] PALETTES = {
            { 0xFF9BBC0F, 0xFF8BAC0F, 0xFF306230, 0xFF0F380F },             // ORIGINAL
            { 0xFFFFFFFF, 0xFFAAAAAA, 0xFF555555, 0xFF000000 },             // GRAYSCALE
            { 0xFFE0F8D0, 0xFF88C070, 0xFF346856, 0xFF081820 },             // CLASSIC
            { 0xFFC4CFA1, 0xFF8B956D, 0xFF4D533C, 0xFF1F1F1F },             // MUTED
            { 0xFFE0F7FA, 0xFF80DEEA, 0xFF0277BD, 0xFF003C5F },             // OCEAN
            { 0xFFFFF3E0, 0xFFFFB74D, 0xFFE65100, 0xFF5D2A00 },             // SUNSET
            { 0xFFFFEBEE, 0xFFEF5350, 0xFFB71C1C, 0xFF3E0000 },             // CRIMSON
            { 0xFFFFF0F6, 0xFFFF77A8, 0xFFD81B60, 0xFF4A0033 },             // VAPOR
            { 0xFFE8F5E9, 0xFF81C784, 0xFF2E7D32, 0xFF0B3D0B },             // FOREST
            { 0xFFF3E5F5, 0xFFBA68C8, 0xFF6A1B9A, 0xFF1A0033 }              // PURPLE
    };

    private int currentPalette = 0;

    public ScreenPanel(Overlord emulator) {
        this.emulator = emulator;
        this.canvas = new Canvas(GB_WIDTH * INITIAL_SCALE, GB_HEIGHT * INITIAL_SCALE);
        this.image = new WritableImage(GB_WIDTH, GB_HEIGHT);
        this.argbBuffer = new int[GB_WIDTH * GB_HEIGHT];

        canvas.setFocusTraversable(true);

        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: derive(-color-bg-default, -15%); " +
                "-fx-border-color: -color-border-default; " +
                "-fx-border-width: 2;");

        setMinWidth(0);
        setMinHeight(0);

        getChildren().add(canvas);

        drawBackground();

        setFocusTraversable(true);

        startRenderLoop();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        resizeCanvas();
    }

    private void resizeCanvas() {
        double width = getWidth();
        double height = getHeight();

        if (width <= 0 || height <= 0) {
            return;
        }

        double insetWidth = getInsets().getLeft() + getInsets().getRight();
        double insetHeight = getInsets().getTop() + getInsets().getBottom();

        double availableWidth = width - insetWidth;
        double availableHeight = height - insetHeight;

        if (availableWidth <= 0 || availableHeight <= 0) {
            return;
        }

        int scaleWidth = Math.max(1, (int) (availableWidth / GB_WIDTH));
        int scaleHeight = Math.max(1, (int) (availableHeight / GB_HEIGHT));

        int scale = Math.min(scaleWidth, scaleHeight);

        double newWidth = GB_WIDTH * scale;
        double newHeight = GB_HEIGHT * scale;

        canvas.setWidth(newWidth);
        canvas.setHeight(newHeight);

        if (emulator == null || !emulator.isCartridgeLoaded()) {
            drawBackground();
        }
    }

    private void drawBackground() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.rgb(158, 170, 138));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void startRenderLoop() {
        renderLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (emulator != null && emulator.isCartridgeLoaded()) {
                    int[] frame = emulator.getFrame();
                    if (frame != null) {
                        renderFrame(frame);
                    }
                } else {
                    drawBackground();
                }
            }
        };
        renderLoop.start();
    }

    private void renderFrame(int[] gbFrame) {
        if (gbFrame.length != GB_WIDTH * GB_HEIGHT) {
            return;
        }

        int[] palette = PALETTES[currentPalette];

        for (int i = 0; i < gbFrame.length; i++) {
            argbBuffer[i] = palette[gbFrame[i] & 0x03];
        }

        PixelWriter writer = image.getPixelWriter();
        writer.setPixels(
                0, 0,
                GB_WIDTH, GB_HEIGHT,
                PixelFormat.getIntArgbInstance(),
                argbBuffer,
                0,
                GB_WIDTH
        );

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void setPalette(int index) {
        if (index >= 0 && index < PALETTES.length) {
            currentPalette = index;
        }
    }

    public int getPalette() {
        return currentPalette;
    }

    public void stop() {
        if (renderLoop != null) {
            renderLoop.stop();
        }
    }
}