package ppu;

public class FrameBuffer {

    private final int[] buffer = new int[FrameConstants.WIDTH * FrameConstants.HEIGHT];

    private final int[] backBuffer = new int[FrameConstants.WIDTH * FrameConstants.HEIGHT];

    private boolean useBackBuffer = false;

    public void setPixel(int x, int y, int color) {
        if (x >= 0 && x < FrameConstants.WIDTH && y >= 0 && y < FrameConstants.HEIGHT) {
            int index = y * FrameConstants.WIDTH + x;
            if (useBackBuffer) {
                backBuffer[index] = color;
            } else {
                buffer[index] = color;
            }
        }
    }

    public int getPixel(int x, int y) {
        if (x >= 0 && x < FrameConstants.WIDTH && y >= 0 && y < FrameConstants.HEIGHT) {
            return useBackBuffer ? buffer[y * FrameConstants.WIDTH + x] : backBuffer[y * FrameConstants.WIDTH + x];
        }
        return 0;
    }

    public int[] getFrame() {
        return useBackBuffer ? buffer : backBuffer;
    }

    public void swapBuffers() {
        useBackBuffer = !useBackBuffer;
    }

    public void clear() {
        if (useBackBuffer) {
            java.util.Arrays.fill(backBuffer, 0);
        } else {
            java.util.Arrays.fill(buffer, 0);
        }
    }

    public static int[] toARGB(int[] gbColors) {
        int[] palette = {
                0xFFE0F8D0,
                0xFF88C070,
                0xFF346856,
                0xFF081820
        };

        int[] argb = new int[gbColors.length];
        for (int i = 0; i < gbColors.length; i++) {
            argb[i] = palette[gbColors[i] & 0x03];
        }
        return argb;
    }

    public static int[] toGrayscale(int[] gbColors) {
        int[] palette = {
                0xFFFFFFFF,
                0xFFAAAAAA,
                0xFF555555,
                0xFF000000
        };

        int[] argb = new int[gbColors.length];
        for (int i = 0; i < gbColors.length; i++) {
            argb[i] = palette[gbColors[i] & 0x03];
        }
        return argb;
    }

}
