package org.matxt.Element;

import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.PathParser;
import org.matxt.Extra.Config;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;

public class ElementShape extends Element {
    protected Shape shape;
    protected float scale = 1f;
    protected float angle = 0f;
    protected Stroke stroke = new BasicStroke(1);
    protected boolean doFill;

    public ElementShape (float x, float y, Shape shape, boolean doFill, Color color) {
        super(x, y, color);
        this.shape = shape;
        this.doFill = doFill;
    }

    public ElementShape (float x, float y, Shape shape, float scale, Stroke stroke, boolean doFill, Color color) {
        super(x, y, color);
        this.shape = shape;
        this.scale = scale;
        this.stroke = stroke;
        this.doFill = doFill;
    }

    public Shape getShape() {
        return shape;
    }

    public float getScale() {
        return scale;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public boolean isDoFill() {
        return doFill;
    }

    public AffineTransform getTransform (int X, int Y) {
        Rectangle2D bounds = shape.getBounds2D();
        double width = bounds.getWidth() * scale;
        double height = bounds.getHeight() * scale;

        AffineTransform transform = AffineTransform.getTranslateInstance(X - width / 2, Y - height / 2);
        transform.rotate(angle);
        transform.scale(scale, scale);

        return transform;
    }

    public AffineTransform getTransform () {
        return getTransform(Config.normX(x), Config.normY(y));
    }

    @Override
    public void draw (BufferedImage image, Graphics2D graphics, int X, int Y) {
        AffineTransform transform = getTransform(X, Y);
        AffineTransform prev = graphics.getTransform();

        graphics.setStroke(stroke);
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
        return new ElementShape(x, y, shape, doFill, color);
    }
}
