package org.matxt.Element;

import org.matxt.Extra.Config;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class PixelMatrix extends Element {
    final public int width, height;
    final private BufferedImage pixels;

    public PixelMatrix (float x, float y, int width, int height, Color color) {
        super(x, y, color);
        this.width = width;
        this.height = height;
        this.pixels = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public PixelMatrix (float x, float y, int width, int height) {
        this(x, y, width, height, Config.getBackground());
    }

    private PixelMatrix (float x, float y, Color color, boolean isVisible, int width, int height, BufferedImage pixels) {
        super(x, y, color, isVisible);
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public Graphics2D getGraphics () {
        return pixels.createGraphics();
    }

    public Color getPixel (int x, int y) {
        return new Color(this.pixels.getRGB(x, y));
    }

    public void setPixel (int x, int y, Color color) {
        this.pixels.setRGB(x, y, color.getRGB());
    }

    @Override
    public void draw (BufferedImage image, Graphics2D graphics, int X, int Y) {
        int finalX = X - width / 2;
        int finalY = Y - height / 2;

        graphics.drawImage(pixels, finalX, finalY, color, null);
    }

    @Override
    public PixelMatrix clone() {
        BufferedImage bi = new BufferedImage(pixels.getWidth(), pixels.getHeight(), pixels.getType());
        byte[] sourceData = ((DataBufferByte)pixels.getRaster().getDataBuffer()).getData();
        byte[] biData = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourceData, 0, biData, 0, sourceData.length);

        return new PixelMatrix(x, y, color, isVisible, width, height, bi);
    }
}
