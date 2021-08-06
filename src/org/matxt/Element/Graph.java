package org.matxt.Element;

import org.jml.Calculus.Integral;
import org.jml.Function.Real.RealFunction;
import org.matxt.Extra.Config;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Graph extends Element {
    public class Plot {
        final public Graph parent;
        public RealFunction func;
        public Color color;
        BasicStroke stroke;
        public float from, to;

        public Plot (RealFunction func, Color color, float stroke, float from, float to) {
            this.parent = Graph.this;
            this.func = func;
            this.color = color;
            this.stroke = new BasicStroke(stroke);
            this.from = from;
            this.to = to;
        }

        public float getStroke() {
            return stroke.getLineWidth();
        }

        public void setStroke (float stroke) {
            this.stroke = new BasicStroke(stroke);
        }
    }

    public float width, height;
    public float fromX, toX, stepX, fromY, toY, stepY;

    private ArrayList<Plot> plots;

    public Graph (float x, float y, float width, float height, float fromX, float toX, float stepX, float fromY, float toY, float stepY, boolean isVisible, Color color) {
        super(x, y, color, isVisible);
        this.width = width;
        this.height = height;
        this.fromX = fromX;
        this.toX = toX;
        this.stepX = stepX;
        this.fromY = fromY;
        this.toY = toY;
        this.stepY = stepY;
        this.plots = new ArrayList<>();
    }

    public Graph(float x, float y, float width, float height, float fromX, float toX, float stepX, float fromY, float toY, float stepY, Color color) {
        super(x, y, color);
        this.width = width;
        this.height = height;
        this.fromX = fromX;
        this.toX = toX;
        this.stepX = stepX;
        this.fromY = fromY;
        this.toY = toY;
        this.stepY = stepY;
        this.plots = new ArrayList<>();
    }

    public Plot add (RealFunction function, Color color, float stroke, float from, float to) {
        Plot plot = new Plot(function, color, stroke, from, to);

        if (this.plots.add(plot)) {
            return plot;
        }

        return null;
    }

    public Plot add (RealFunction function, Color color, float stroke) {
        return add(function, color, stroke, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public Plot add (RealFunction function, Color color) {
        return add(function, color, 1f);
    }

    @Override
    public void draw(BufferedImage image, Graphics2D graphics, int X, int Y) {
        float hheight = height / 2;
        float hwidth = width / 2;

        int top = (int) (-(y + hheight) * Config.getHalfHeight() + Config.getHalfHeight());
        int bottom = (int) (-(y - hheight) * Config.getHalfHeight() + Config.getHalfHeight());
        int left = (int) ((x - hwidth) * Config.getHalfWidth() + Config.getHalfWidth());
        int right = (int) ((x + hwidth) * Config.getHalfWidth() + Config.getHalfWidth());

        float dx = toX - fromX;
        float dy = toY - fromY;

        int linesX = (int) (dx / stepX);
        int linesY = (int) (dy / stepY);

        float alpha = (float) (right - left) / linesX;
        float beta = (float) (top - bottom) / linesY;

        for (int i=0;i<linesX;i++) {
            int from = left + (int) (i * alpha);
            Line2D line = new Line2D.Float(from, bottom, from, top);
            graphics.draw(line);
        }

        for (int i=0;i<linesY;i++) {
            int from = bottom + (int) (i * beta);
            Line2D line = new Line2D.Float(left, from, right, from);
            graphics.draw(line);
        }

        for (Plot plot: plots) {
            int lastY = (int) ((bottom - top) * (plot.func.apply(fromX) - fromY) / (toY - fromY) + bottom);

            graphics.setColor(plot.color);
            graphics.setStroke(plot.stroke);

            for (int i=left+1;i<=right;i++) {
                float x = dx * (i - left) / (right - left) + fromX;
                if (x < plot.from || x > plot.to) {
                    continue;
                }

                float y = plot.func.apply(x);
                int h = (int) ((top - bottom) * (y - fromY) / dy + bottom);

                Line2D line = new Line2D.Float(i-1, lastY, i, h);
                graphics.draw(line);
                lastY = h;
            }
        }
    }

    @Override
    public Graph clone() {
        return new Graph(x, y, width, height, fromX, toX, stepX, fromY, toY, stepY, isVisible, color);
    }
}
