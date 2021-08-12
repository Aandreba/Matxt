package org.matxt.Element;

import org.matxt.Extra.Config;
import org.matxt.Extra.Utils.Segment;
import org.matxt.Extra.Utils.ShapeUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Body extends Element {
    protected Shape shape;
    private ArrayList<Segment> segments;

    protected float scale = 1f;
    protected float angle = 0f;
    protected Stroke stroke = new BasicStroke(1);
    protected boolean doFill;

    public Body(float x, float y, Shape shape, boolean doFill, Color color) {
        super(x, y, color);
        setShape(shape);
        this.doFill = doFill;
    }

    public Body(float x, float y, Shape shape, float scale, float angle, Stroke stroke, boolean doFill, Color color) {
        super(x, y, color);
        setShape(shape);
        this.scale = scale;
        this.angle = angle;
        this.stroke = stroke;
        this.doFill = doFill;
    }

    protected Body(float x, float y, Color color, boolean isVisible, Shape shape, ArrayList<Segment> segments, float scale, float angle, Stroke stroke, boolean doFill) {
        super(x, y, color, isVisible);
        this.shape = shape;
        this.segments = segments;
        this.scale = scale;
        this.angle = angle;
        this.stroke = stroke;
        this.doFill = doFill;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape (Shape shape) {
        this.shape = shape;
        this.segments = ShapeUtils.getSegments(shape, null);
    }

    public ArrayList<Segment> getSegments() {
        return (ArrayList<Segment>) segments.clone();
    }

    public void setSegments (ArrayList<Segment> segments) {
        this.segments = segments;
        this.shape = ShapeUtils.toShape(this.segments);
    }

    public void setSegment (int index, Segment segment) {
        this.segments.set(index, segment);
        this.shape = ShapeUtils.toShape(this.segments);
    }

    public void addSegment (Segment segment) {
        this.segments.add(segment);
        this.shape = ShapeUtils.toShape(this.segments);
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

    public Rectangle2D getBounds () {
        Rectangle2D bounds = shape.getBounds2D();

        double w = bounds.getWidth() * scale / Config.getWidth();
        double h = bounds.getHeight() * scale / Config.getHeight();

        double x = this.x + w / 2;
        double y = this.y + h / 2;

        return new Rectangle2D.Double(x, y, w, h);
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
    public Body clone() {
        return new Body(x, y, color, isVisible, new Path2D.Double(shape), segments, scale, angle, stroke, doFill);
    }
}
