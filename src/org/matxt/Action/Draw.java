package org.matxt.Action;

import org.matxt.Element.ElementShape;
import org.matxt.Element.Graph;
import org.matxt.Extra.Steps;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

public class Draw {
    public static <T extends ElementShape> Action<T> shape (T shape, float from, float to) {
        ArrayList<Path> paths = getPaths(shape.shape);
        return new Action<>(shape, (x,c,t) -> {
            x.isVisible = true;
            Shape newShape = getPartial(paths, t);
            x.shape = newShape == null ? c.shape : newShape;
        }, from, to);
    }

    public static Action<Graph> plot (Graph.Plot plot, float from, float to) {
        return new Action<>(plot.parent, (x,c,t) -> {
            plot.from = plot.parent.fromX;
            plot.to = Steps.smooth(plot.parent.fromX, plot.parent.toX, t);
        }, from, to);
    }

    private static Shape getPartial (ArrayList<Path> paths, float pct) {
        int limit = Math.round(paths.size() * pct);
        Path2D result = new Path2D.Double();

        if (limit >= paths.size()) {
            return null;
        }

        for (int i=0;i<limit;i++) {
            Path path = paths.get(i);
            switch (path.type) {
                case PathIterator.SEG_MOVETO -> result.moveTo(path.point[0], path.point[1]);
                case PathIterator.SEG_LINETO -> result.lineTo(path.point[0], path.point[1]);
                case PathIterator.SEG_QUADTO -> result.quadTo(path.point[0], path.point[1], path.point[2], path.point[3]);
                case PathIterator.SEG_CUBICTO -> result.curveTo(path.point[0], path.point[1], path.point[2], path.point[3], path.point[4], path.point[5]);
            }
        }

        return result;
    }

    private static ArrayList<Path> getPaths (Shape shape) {
        PathIterator iter = shape.getPathIterator(null);
        ArrayList<Path> paths = new ArrayList<>();

        double[] point;
        while (!iter.isDone()) {
            point = new double[6];
            int type = iter.currentSegment(point);

            paths.add(new Path(point, type));
            iter.next();
        }

        return paths;
    }

    static class Path {
        double[] point;
        int type;

        public Path (double[] point, int type) {
            this.point = point;
            this.type = type;
        }
    }
}
