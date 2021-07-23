package org.matxt.Element;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ElementShape extends Element {
    public Shape shape;
    public boolean doFill;
    public boolean doCenter;

    public ElementShape (float x, float y, Shape shape, boolean doFill, boolean doCenter, Color color) {
        super(x, y, color);
        this.shape = shape;
        this.doFill = doFill;
        this.doCenter = doCenter;
    }

    @Override
    public void draw(BufferedImage image, Graphics2D graphics, int sceneWidth, int sceneHeight, float hw, float hh) {
        AffineTransform transform = AffineTransform.getTranslateInstance(x * hw + hw, -y * hh + hh);

        if (doCenter) {
            Rectangle2D bounds = shape.getBounds2D();
            transform.translate(-bounds.getWidth() / 2, bounds.getHeight() / 2);
        }

        AffineTransform prev = graphics.getTransform();
        graphics.setTransform(transform);

        if (doFill) {
            graphics.fill(shape);
        } else {
            graphics.draw(shape);
        }

        graphics.setTransform(prev);
    }

    @Override
    public ElementShape clone() {
        return new ElementShape(x, y, shape, doFill, doCenter, color);
    }
}
