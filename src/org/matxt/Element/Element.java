package org.matxt.Element;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Element {
    public float x, y;
    public Color color;
    public boolean isVisible;

    public Element (float x, float y, Color color, boolean isVisible) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.isVisible = isVisible;
    }

    public Element (float x, float y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.isVisible = true;
    }

    public abstract void draw (BufferedImage image, Graphics2D graphics, int sceneWidth, int sceneHeight, float halfWidth, float halfHeight);
    public abstract Element clone ();
}
