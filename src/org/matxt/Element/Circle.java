package org.matxt.Element;

import org.matxt.Extra.Config;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Circle extends Element {
    public float radius;

    public Circle(float x, float y, float radius, Color color) {
        super(x, y, color);
        this.radius = radius;
    }

    @Override
    public void draw (BufferedImage image, Graphics2D graphics, int X, int Y) {
        int R = Math.round(radius * Math.min(Config.getWidth(), Config.getHeight()));
        int hr = R / 2;

        graphics.fillOval(X - hr, Y - hr, R, R);
    }

    @Override
    public Circle clone() {
        return new Circle(x, y, radius, color);
    }
}
