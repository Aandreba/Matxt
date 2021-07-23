package org.matxt.Element;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Circle extends Element {
    public float radius;

    public Circle(float x, float y, float radius, Color color) {
        super(x, y, color);
        this.radius = radius;
    }

    @Override
    public void draw(BufferedImage image, Graphics2D graphics, int sceneWidth, int sceneHeight, float hw, float hh) {
        int R = Math.round(radius * Math.min(sceneWidth, sceneHeight));
        int hr = R / 2;

        int X = Math.round(x * hw + hw) - hr;
        int Y = Math.round(-y * hh + hh) - hr;

        graphics.fillOval(X, Y, R, R);
    }

    @Override
    public Circle clone() {
        return new Circle(x, y, radius, color);
    }
}
