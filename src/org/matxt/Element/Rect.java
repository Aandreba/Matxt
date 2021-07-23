package org.matxt.Element;

import java.awt.*;

public class Rect extends Polygon {
    private float width, height;

    public Rect(float x, float y, float width, float height, Color color) {
        super(x, y, 4, color);
        setWidth(width);
        setHeight(height);
    }

    public float getWidth () {
        return width;
    }

    public void setWidth (float width) {
        float half = width / 2;
        this.width = width;

        xPoints[0] = half;
        xPoints[1] = half;
        xPoints[2] = -half;
        xPoints[3] = -half;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        float half = height / 2;
        this.height = height;

        yPoints[0] = half;
        yPoints[1] = -half;
        yPoints[2] = -half;
        yPoints[3] = half;
    }
}
