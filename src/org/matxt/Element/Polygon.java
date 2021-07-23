package org.matxt.Element;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Polygon extends Element {
    protected float[] xPoints, yPoints;

    public Polygon(float x, float y, float[] xPoints, float[] yPoints, Color color) {
        super(x, y, color);
        this.xPoints = xPoints;
        this.yPoints = yPoints;
    }

    public Polygon(float x, float y, int points, Color color) {
        super(x, y, color);
        this.xPoints = new float[points];
        this.yPoints = new float[points];
    }

    public int getPoints () {
        return xPoints.length;
    }

    public float[] getPoint (int i) {
        return new float[]{ xPoints[i], yPoints[i] };
    }

    public void setPoint (int i, float x, float y) {
        this.xPoints[i] = x;
        this.yPoints[i] = y;
    }

    @Override
    public void draw(BufferedImage image, Graphics2D graphics, int sceneWidth, int sceneHeight, float hw, float hh) {
        int[] pointsX = new int[xPoints.length];
        int[] pointsY = new int[yPoints.length];

        for (int i=0;i<xPoints.length;i++) {
            float pointX = xPoints[i];
            float pointY = yPoints[i];

            pointsX[i] = (int) ((x + pointX) * hw + hw);
            pointsY[i] = (int) ((pointY - y) * hh + hh);
        }

        graphics.fillPolygon(pointsX, pointsY, xPoints.length);
    }

    @Override
    public Polygon clone() {
        return new Polygon(x, y, xPoints.clone(), yPoints.clone(), color);
    }
}
