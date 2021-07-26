package org.matxt.Action;

import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.ComplexFunction;
import org.jml.Mathx.FourierSeries;
import org.jml.Vector.Single.Veci;
import org.matxt.Element.ElementShape;
import org.matxt.Element.Graph;
import org.matxt.Extra.Steps;
import org.matxt.Video;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Draw {
    public static <T extends ElementShape> Action<T> shape (T shape, Video video, float from, float to) {
        AffineTransform transform = shape.getTransform();
        ComplexFunction func = FourierSeries.pathToFunction(shape.getShape().getPathIterator(transform));

        final float dt = 0.001f;
        float lastT = 0;

        Path2D path = new Path2D.Float();
        ElementShape element = new ElementShape(0, 0, path, false, false, shape.color);

        shape.isVisible = false;
        video.add(element);
        return new Action<>(shape, (x,c,t) -> {
            if (t >= 1) {
                shape.isVisible = true;
                element.isVisible = false;
                return;
            }

            for (float i=lastT+dt;i<=t;i+=dt) {
                Comp val = func.apply(i);
                path.lineTo(val.re, val.im);
            }
        }, from, to);
    }

    public static Action<Graph> plot (Graph.Plot plot, float from, float to) {
        return new Action<>(plot.parent, (x,c,t) -> {
            plot.from = plot.parent.fromX;
            plot.to = Steps.smooth(plot.parent.fromX, plot.parent.toX, t);
        }, from, to);
    }
}
