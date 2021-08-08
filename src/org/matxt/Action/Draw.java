package org.matxt.Action;

import org.apache.batik.ext.awt.geom.Polygon2D;
import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.ComplexFunction;
import org.jml.MT.TaskIterator;
import org.jml.Mathx.FourierSeries;
import org.jml.Mathx.Mathf;
import org.matxt.Element.Body;
import org.matxt.Element.PixelMatrix;
import org.matxt.Extra.Defaults;
import org.matxt.Extra.ShapeUtils;
import org.matxt.Extra.StepFunction;
import org.matxt.Video;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Draw {
    public static <T extends Body> Action<T> outline (T element, Video video, StepFunction step, float from, float to) {
        final float dt = 1e-3f;

        Path2D.Float path = new Path2D.Float();
        ComplexFunction function = FourierSeries.shapeToFunction(element.getShape(), null);

        /* DETECT POINTS IN TIME WHERE "MOVE TO" ARE PRESENT */
        PathIterator iter = element.getShape().getPathIterator(null);
        ArrayList<Float> moveTos = new ArrayList<>();
        int len = 0;

        while (!iter.isDone()) {
            int type = iter.currentSegment(new float[6]);
            if (type == 0) {
                moveTos.add((float) len);
            }

            iter.next();
            len++;
        }

        for (int i=0;i<moveTos.size();i++) {
            moveTos.set(i, moveTos.get(i) / len);
        }

        /* END */

        Stroke newStroke = element.getStroke() instanceof BasicStroke ? new BasicStroke(((BasicStroke) element.getStroke()).getLineWidth() / 2) : element.getStroke();
        Body newElement = new Body(element.x, element.y, path, element.getScale(), element.getAngle(), newStroke, false, element.color);

        Comp firsPoint = function.apply(0);
        path.moveTo(firsPoint.re, firsPoint.im);

        video.add(newElement);
        element.isVisible = false;

        AtomicReference<Float> lastTime = new AtomicReference<>(0f);
        return new Action<>(element, (x,c,_t) -> {
            if (_t >= 1) {
                newElement.isVisible = false;
                element.isVisible = true;
                return;
            }

            float lastT = lastTime.get();
            float t = _t <= 0 ? 0 : step.apply(_t);

            Point2D.Float currentPoint = (Point2D.Float) path.getCurrentPoint();
            Comp lastPoint = new Comp(currentPoint.x, currentPoint.y);
            AtomicReference<Float> lastI = new AtomicReference<>(lastT - dt);

            for (float i=lastTime.get();i<t;i+=dt) {
                Comp point = function.apply(i);
                if (point.equals(lastPoint)) {
                    continue;
                }

                float finalI = i;
                if (moveTos.stream().anyMatch(z -> lastI.get() <= z && finalI >= z)) {
                    path.moveTo(point.re, point.im);
                } else {
                    path.lineTo(point.re, point.im);
                }

                lastPoint = point;
                lastI.updateAndGet(z -> z++);
            }

            lastTime.set(t);
        }, from, to);
    }

    public static <T extends Body> Action<T> fill(T element, Video video, StepFunction step, float from, float to) {
        AffineTransform transf = AffineTransform.getScaleInstance(element.getScale(), element.getScale());
        transf.rotate(element.getAngle());

        Path2D shape = (Path2D) transf.createTransformedShape(element.getShape());
        Rectangle2D bounds = shape.getBounds2D();

        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();

        int pixels = width * height;
        int beta = pixels * 5;
        int pixm1 = pixels - 1;

        List<Polygon2D> segments = ShapeUtils.getPolygons(shape, null, beta);
        PixelMatrix matrix = new PixelMatrix(element.x, element.y, width, height);

        video.add(matrix);
        element.isVisible = false;

        AtomicInteger lastIndex = new AtomicInteger();

        return new Action<>(element, (x,c,t) -> {
            float _t = t <= 0 ? 0 : (t >= 1 ? 1 : step.apply(t));
            if (t >= 1) {
                element.isVisible = true;
                matrix.isVisible = false;
            }

            // FILL
            int last = lastIndex.get();
            int index = Math.min((int) (_t * pixels), pixm1);
            int delta = index - last;

            TaskIterator iter = new TaskIterator((int j) -> {
                int i = j + last;
                int X = i / height;
                int Y = i % height;

                if (ShapeUtils.isPointInside(new Point2D.Float(X, Y), segments)) {
                    matrix.setPixel(X, Y, element.color);
                }
            }, (int j) -> j < delta);

            iter.run();
            lastIndex.set(index);
        }, from, to);
    }
}
