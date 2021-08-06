package org.matxt.Element;

import org.apache.batik.ext.awt.geom.Polygon2D;
import org.matxt.Extra.Config;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Rect extends Element {
    public float width, height;

    public Rect(float x, float y, float width, float height, Color color) {
        super(x, y, color);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw (BufferedImage image, Graphics2D graphics, int X, int Y) {
        int W = Config.normX(width);
        int H = (int) (y * Config.getHalfHeight() + Config.getHalfHeight());

        graphics.fillRect(X, Y, W, H);
    }

    @Override
    public Rect clone() {
        return new Rect(x, y, width, height, color);
    }
}
