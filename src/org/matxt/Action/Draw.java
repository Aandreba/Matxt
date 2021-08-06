package org.matxt.Action;

import org.apache.batik.ext.awt.geom.Polygon2D;
import org.jml.MT.TaskIterator;
import org.matxt.Element.ElementShape;
import org.matxt.Element.PixelMatrix;
import org.matxt.Extra.Defaults;
import org.matxt.Extra.StepFunction;
import org.matxt.Video;

import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Draw {
    public static <T extends ElementShape> Action<T> shape (T element, Video video, StepFunction step, float from, float to) {
        AffineTransform transf = AffineTransform.getScaleInstance(element.getScale(), element.getScale());
        transf.rotate(element.getAngle());

        Path2D shape = (Path2D) transf.createTransformedShape(element.getShape());
        Rectangle2D bounds = shape.getBounds2D();

        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();

        int pixels = width * height;
        int beta = pixels * 5;
        int pixm1 = pixels - 1;

        List<Polygon2D> segments = Defaults.getPolygons(shape, null, beta);
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

                if (Defaults.isPointInside(new Point2D.Float(X, Y), segments)) {
                    matrix.setPixel(X, Y, element.color);
                }
            }, (int j) -> j < delta);

            iter.run();
            lastIndex.set(index);
        }, from, to);
    }
}
