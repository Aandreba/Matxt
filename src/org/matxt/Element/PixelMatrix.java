package org.matxt.Element;

import org.jml.MT.TaskIterator;
import org.matxt.Extra.Config;
import org.matxt.Extra.Defaults;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class PixelMatrix extends Element {
    final public int width, height, pixelCount;
    final private Color[][] pixels;

    public PixelMatrix (float x, float y, int width, int height, Color color) {
        super(x, y, color);
        this.width = width;
        this.height = height;
        this.pixelCount = width * height;
        this.pixels = new Color[width][height];

        Color[] alpha = new Color[height];
        Arrays.fill(alpha, new Color(0, 0, 0, 0));

        for (int i=0;i<width;i++) {
            System.arraycopy(alpha, 0, this.pixels[i], 0, height);
        }
    }

    public PixelMatrix (float x, float y, int width, int height) {
        this(x, y, width, height, Defaults.BACKGROUND);
    }

    private PixelMatrix (float x, float y, Color color, boolean isVisible, int width, int height, Color[][] pixels) {
        super(x, y, color, isVisible);
        this.width = width;
        this.height = height;
        this.pixelCount = width * height;
        this.pixels = pixels;
    }

    public Color getPixel (int x, int y) {
        return this.pixels[x][y];
    }

    public void setPixel (int x, int y, Color color) {
        this.pixels[x][y] = color;
    }

    @Override
    public void draw (BufferedImage image, Graphics2D graphics, int X, int Y) {
        int finalX = X - width / 2;
        int finalY = Y - height / 2;

        TaskIterator iter = new TaskIterator((int k) -> {
            int i = k / height;
            int j = k % height;

            int x = finalX + i;
            int y = finalY + j;

            if (x < 0 | x >= Config.getWidth() | y < 0 | y >= Config.getHeight()) {
                return;
            }

            Color color = this.pixels[i][j];
            float t = color.getAlpha() / 255f;
            int r, g, b;

            if (t == 0) {
                return;
            } else if (t == 1) {
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
            } else {
                r = (int) ((1 - t) * this.color.getRed() + t * color.getRed());
                g = (int) ((1 - t) * this.color.getGreen() + t * color.getGreen());
                b = (int) ((1 - t) * this.color.getBlue() + t * color.getBlue());
            }

            image.setRGB(x, y, new Color(r, g, b).getRGB());
        }, (int k) -> k < pixelCount);

        iter.run();
    }

    @Override
    public PixelMatrix clone() {
        Color[][] pixels = new Color[height][width];
        System.arraycopy(this.pixels, 0, pixels, 0, width * height);

        return new PixelMatrix(x, y, color, isVisible, width, height, pixels);
    }
}
