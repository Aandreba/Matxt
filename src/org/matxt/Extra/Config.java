package org.matxt.Extra;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

public class Config {
    final public static Runtime RUNTIME = Runtime.getRuntime();

    private static File TMP;
    private static File WIN_PDF2SVG_32 = null;
    private static File WIN_PDF2SVG_64 = null;

    private static Color BACKGROUND = new Color(25, 27, 41);

    private static int WIDTH = 1920;
    private static int HEIGHT = 1080;
    private static float ASPECT_RATIO = (float) WIDTH / HEIGHT;

    private static float HALF_WIDTH = WIDTH / 2f;
    private static float HALF_HEIGHT = HEIGHT / 2f;

    static {
        try {
            setTempDir(new File("tmp"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static File getTempDir () {
        return TMP;
    }

    public static void setTempDir (File tmp) throws IOException {
        if (!tmp.exists() && !tmp.mkdir()) {
            throw new IOException("Error creating directory \""+tmp+"\"");
        } else if (tmp.exists() && !tmp.isDirectory()) {
            throw new IOException("File provided isn't directory");
        }

        Config.TMP = tmp;

        for (File file: tmp.listFiles()) {
            file.deleteOnExit();
        }

        tmp.deleteOnExit();
    }

    public static File getTemporaryFile (String path) {
        File file = new File(TMP, path);
        file.deleteOnExit();

        return file;
    }

    public static File getPdf2Svg_x86 () {
        return WIN_PDF2SVG_32;
    }

    public static void setPdf2Svg_x86 (File file) throws IOException {
        if (!Defaults.isWindowsExe(file)) {
            throw new IOException("File provided isn't a Windows executable");
        }

        WIN_PDF2SVG_32 = file;
    }

    public static File getPdf2Svg_x64 () {
        return WIN_PDF2SVG_64;
    }

    public static void setPdf2Svg_x64 (File file) throws IOException {
        if (!Defaults.isWindowsExe(file)) {
            throw new IOException("File provided isn't a Windows executable");
        }

        WIN_PDF2SVG_64 = file;
    }

    public static Color getBackground() {
        return BACKGROUND;
    }

    public static void setBackground(Color background) {
        Config.BACKGROUND = background;
    }

    public static int getWidth () {
        return WIDTH;
    }

    public static void setWidth (int width) {
        Config.WIDTH = width;
        Config.HALF_WIDTH = width / 2f;
        Config.ASPECT_RATIO = (float) WIDTH / HEIGHT;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public static void setHeight (int height) {
        Config.HEIGHT = height;
        Config.HALF_HEIGHT = height / 2f;
        Config.ASPECT_RATIO = (float) WIDTH / HEIGHT;
    }

    public static float getAspectRatio() {
        return ASPECT_RATIO;
    }

    public static float getHalfWidth() {
        return HALF_WIDTH;
    }

    public static float getHalfHeight() {
        return HALF_HEIGHT;
    }

    public static int normX (float x) {
        return (int) (x * HALF_WIDTH + HALF_WIDTH);
    }

    public static int normY (float y) {
        return (int) (-y * HALF_HEIGHT + HALF_HEIGHT);
    }
}
