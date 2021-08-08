package org.matxt.Extra;

import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.batik.svggen.font.Point;
import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.ComplexFunction;
import org.jml.MT.TaskIterator;
import org.jml.Mathx.FourierSeries;
import org.jml.Mathx.Mathf;
import org.jml.Vector.Single.Vec;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ShapeUtils {
    public static Point2D toPoint (Comp a) {
        return new Point2D.Float(a.re, a.im);
    }

    public static Comp toComp (Point2D a) {
        if (a instanceof Point2D.Float) {
            Point2D.Float b = (Point2D.Float) a;
            return new Comp(b.x, b.y);
        }

        return new Comp((float) a.getX(), (float) a.getY());
    }

    public static Comp lineTo (Comp from, Comp to, float t, float _t) {
        return from.mul(_t).add(to.mul(t));
    }

    public static Comp lineTo (Comp from, Comp to, float t) {
        return lineTo(from, to, t, 1 - t);
    }

    public static Comp quadTo (Comp from, Comp alpha, Comp to, float t, float _t) {
        Comp a = lineTo(from, alpha, t, _t);
        Comp b = lineTo(alpha, to, t, _t);

        return a.mul(_t).add(b.mul(t));
    }

    public static Comp quadTo (Comp from, Comp alpha, Comp to, float t) {
        return quadTo(from, alpha, to, t, 1 - t);
    }

    public static Comp cubicTo (Comp from, Comp alpha, Comp beta, Comp to, float t, float _t) {
        Comp a = quadTo(from, alpha, beta, t, _t);
        Comp b = quadTo(alpha, beta, to, t, _t);

        return a.mul(_t).add(b.mul(t));
    }

    public static Path add (Path one, Path two, Vec current) {
        if (one.type == two.type) {
            int k = one.data.length;
            float[] data = new float[k];

            for (int j=0;j<k;j++) {
                data[j] = one.data[j] + two.data[j];
            }

            return new Path(one.type, data);
        } else if (one.type == 0 || two.type == 0) {
            throw new MatxtShapeException("Can't combine \"move to\" with other paths");
        }

        Vec[] oneParams = new Vec[1 + one.data.length / 2];
        Vec[] twoParams = new Vec[1 + two.data.length / 2];

        oneParams[0] = current.clone();
        twoParams[0] = current.clone();

        for (int i=1;i<oneParams.length;i++) {
            float[] array = new float[2];
            System.arraycopy(one.data, 2 * (i - 1), array, 0, 2);
            oneParams[i] = new Vec(array);
        }

        for (int i=1;i<twoParams.length;i++) {
            float[] array = new float[2];
            System.arraycopy(two.data, 2 * (i - 1), array, 0, 2);
            twoParams[i] = new Vec(array);
        }

        if (one.type == PathIterator.SEG_LINETO && two.type == PathIterator.SEG_QUADTO) {
            Vec param1 = twoParams[1].add(oneParams[0].add(oneParams[1]).div(2));
            Vec param2 = twoParams[2].add(oneParams[1]);

            Vec data = new Vec(4);
            data.set(0, 0, param1);
            data.set(0, 2, param2);

            return new Path(PathIterator.SEG_QUADTO, data.toArray());
        } else if (one.type == PathIterator.SEG_LINETO) {
            Vec param1 = twoParams[1].add(oneParams[0].mul(0.25f).add(oneParams[1].mul(0.75f)));
            Vec param2 = twoParams[2].add(oneParams[0].add(oneParams[1]).div(2));
            Vec param3 = twoParams[3].add(oneParams[1]);

            Vec data = new Vec(6);
            data.set(0, 0, param1);
            data.set(0, 2, param2);
            data.set(0, 4, param3);

            return new Path(PathIterator.SEG_CUBICTO, data.toArray());
        }

        if (one.type == PathIterator.SEG_QUADTO && two.type == PathIterator.SEG_LINETO) {
            Vec param1 = oneParams[1].add(twoParams[0].add(twoParams[1]).div(2));
            Vec param2 = oneParams[2].add(twoParams[1]);

            Vec data = new Vec(4);
            data.set(0, 0, param1);
            data.set(0, 2, param2);

            return new Path(PathIterator.SEG_QUADTO, data.toArray());
        } else if (one.type == PathIterator.SEG_QUADTO) {
            Vec param1 = twoParams[1].add(oneParams[0].add(oneParams[1]).div(2));
            Vec param2 = twoParams[2].add(oneParams[1].add(oneParams[2]).div(2));
            Vec param3 = twoParams[3].add(oneParams[2]);

            Vec data = new Vec(6);
            data.set(0, 0, param1);
            data.set(0, 2, param2);
            data.set(0, 4, param3);

            return new Path(PathIterator.SEG_CUBICTO, data.toArray());
        }

        if (one.type == PathIterator.SEG_CUBICTO && two.type == PathIterator.SEG_LINETO) {
            Vec param1 = oneParams[1].add(twoParams[0].mul(0.25f).add(twoParams[1].mul(0.75f)));
            Vec param2 = oneParams[2].add(twoParams[0].add(twoParams[1]).div(2));
            Vec param3 = oneParams[3].add(twoParams[1]);

            Vec data = new Vec(6);
            data.set(0, 0, param1);
            data.set(0, 2, param2);
            data.set(0, 4, param3);

            return new Path(PathIterator.SEG_CUBICTO, data.toArray());
        } else if (one.type == PathIterator.SEG_CUBICTO) {
            Vec param1 = oneParams[1].add(twoParams[0].add(twoParams[1]).div(2));
            Vec param2 = oneParams[2].add(twoParams[1].add(twoParams[2]).div(2));
            Vec param3 = oneParams[3].add(twoParams[3]);

            Vec data = new Vec(6);
            data.set(0, 0, param1);
            data.set(0, 2, param2);
            data.set(0, 4, param3);

            return new Path(PathIterator.SEG_CUBICTO, data.toArray());
        }

        return null;
    }

    public static Path[] divide (Path a, Point2D current) {
        return switch (a.type) {
            case PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO -> {

            }
        };
    }

    /**
     * Calculates transition from an initial bezier curve to the final one
     * @param one Initial bezier curve
     * @param two Final bezier curve
     * @param pct Current point of transition
     * @return Transitioned curve
     */
    public static Path trans (Path one, Path two, Vec current, float pct, float _pct) {
        if (one.type == two.type) {
            int k = one.data.length;
            float[] data = new float[k];

            for (int j=0;j<k;j++) {
                data[j] = _pct * one.data[j] + pct * two.data[j];
            }

            return new Path(one.type, data);
        } else if (one.type == 0 || two.type == 0) {
            throw new MatxtShapeException("Can't combine \"move to\" with other paths");
        }

        Vec[] oneParams = new Vec[1 + one.data.length / 2];
        Vec[] twoParams = new Vec[1 + two.data.length / 2];

        oneParams[0] = current.mul(_pct);
        twoParams[0] = current.mul(pct);

        for (int i=1;i<oneParams.length;i++) {
            float[] array = new float[2];
            System.arraycopy(one.data, 2 * (i - 1), array, 0, 2);
            oneParams[i] = new Vec(array).mul(_pct);
        }

        for (int i=1;i<twoParams.length;i++) {
            float[] array = new float[2];
            System.arraycopy(two.data, 2 * (i - 1), array, 0, 2);
            twoParams[i] = new Vec(array).mul(pct);
        }

        if (one.type == PathIterator.SEG_LINETO && two.type == PathIterator.SEG_QUADTO) {
            Vec param1 = twoParams[1].add(oneParams[0].add(oneParams[1]).div(2));
            Vec param2 = twoParams[2].add(oneParams[1]);

            Vec data = new Vec(4);
            data.set(0, 0, param1);
            data.set(0, 2, param2);

            return new Path(PathIterator.SEG_QUADTO, data.toArray());
        } else if (one.type == PathIterator.SEG_LINETO) {
            Vec param1 = twoParams[1].add(oneParams[0].mul(0.25f).add(oneParams[1].mul(0.75f)));
            Vec param2 = twoParams[2].add(oneParams[0].add(oneParams[1]).div(2));
            Vec param3 = twoParams[3].add(oneParams[1]);

            Vec data = new Vec(6);
            data.set(0, 0, param1);
            data.set(0, 2, param2);
            data.set(0, 4, param3);

            return new Path(PathIterator.SEG_CUBICTO, data.toArray());
        }

        if (one.type == PathIterator.SEG_QUADTO && two.type == PathIterator.SEG_LINETO) {
            Vec param1 = oneParams[1].add(twoParams[0].add(twoParams[1]).div(2));
            Vec param2 = oneParams[2].add(twoParams[1]);

            Vec data = new Vec(4);
            data.set(0, 0, param1);
            data.set(0, 2, param2);

            return new Path(PathIterator.SEG_QUADTO, data.toArray());
        } else if (one.type == PathIterator.SEG_QUADTO) {
            Vec param1 = twoParams[1].add(oneParams[0].add(oneParams[1]).div(2));
            Vec param2 = twoParams[2].add(oneParams[1].add(oneParams[2]).div(2));
            Vec param3 = twoParams[3].add(oneParams[3]);

            Vec data = new Vec(6);
            data.set(0, 0, param1);
            data.set(0, 2, param2);
            data.set(0, 4, param3);

            return new Path(PathIterator.SEG_CUBICTO, data.toArray());
        }

        if (one.type == PathIterator.SEG_CUBICTO && two.type == PathIterator.SEG_LINETO) {
            Vec param1 = oneParams[1].add(twoParams[0].mul(0.25f).add(twoParams[1].mul(0.75f)));
            Vec param2 = oneParams[2].add(twoParams[0].add(twoParams[1]).div(2));
            Vec param3 = oneParams[3].add(twoParams[1]);

            Vec data = new Vec(6);
            data.set(0, 0, param1);
            data.set(0, 2, param2);
            data.set(0, 4, param3);

            return new Path(PathIterator.SEG_CUBICTO, data.toArray());
        } else if (one.type == PathIterator.SEG_CUBICTO) {
            Vec param1 = oneParams[1].add(twoParams[0].add(twoParams[1]).div(2));
            Vec param2 = oneParams[2].add(twoParams[1].add(twoParams[2]).div(2));
            Vec param3 = oneParams[3].add(twoParams[3]);

            Vec data = new Vec(6);
            data.set(0, 0, param1);
            data.set(0, 2, param2);
            data.set(0, 4, param3);

            return new Path(PathIterator.SEG_CUBICTO, data.toArray());
        }

        return null;
    }

    public static Path trans (Path one, Path two, Vec current, float pct) {
        return trans(one, two, current, pct, 1 - pct);
    }

    public static Segment trans (Segment first, Segment second, float pct, float _pct) {
        Segment segment = new Segment();
        Vec current = new Vec(2);

        int delta = first.size() - second.size();
        if (delta > 0) {
            second = second.clone();
            Point2D.Float point = second.get(second.size() - 2).getPoint();
            for (int i=0;i<delta;i++) {
                second.add(second.size() - 1, new Path(1, point.x, point.y));
            }

        } else if (delta < 0) {
            first = first.clone();
            Point2D.Float point = first.get(first.size() - 2).getPoint();

            for (int i=0;i<-delta;i++) {
                first.add(first.size() - 1, new Path(1, point.x, point.y));
            }
        }

        int n = first.size();
        for (int i=0;i<n;i++) {
            Path path = trans(first.get(i), second.get(i), current, pct, _pct);
            segment.add(path);
            current = path.getPointXY();
        }

        return segment;
    }

    public static Segment trans (Segment one, Segment two, float pct) {
        return trans(one, two, pct, 1 - pct);
    }

    public static ArrayList<Segment> trans (ArrayList<Segment> first, ArrayList<Segment> second, float pct, float _pct) {
        ArrayList<Segment> segments = new ArrayList<>();

        int delta = first.size() - second.size();
        if (delta > 0) {
            second = (ArrayList<Segment>) second.clone();
            Segment segment = second.get(second.size() - 2);
            Point2D.Float point = segment.get(segment.size() - 2).getPoint();

            for (int i=0;i<delta;i++) {
                second.add(second.size() - 1, new Segment(){{ add(0, point.x, point.y); add(1, point.x, point.y); }});
            }
        } else if (delta < 0) {
            first = (ArrayList<Segment>) first.clone();
            Segment segment = first.get(first.size() - 2);
            Point2D.Float point = segment.get(segment.size() - 2).getPoint();

            for (int i=0;i<-delta;i++) {
                first.add(first.size() - 1, new Segment(){{ add(0, point.x, point.y); add(1, point.x, point.y); }});
            }
        }

        int n = first.size();
        for (int i=0;i<n;i++) {
            segments.add(trans(first.get(i), second.get(i), pct, _pct));
        }

        return segments;
    }

    public static ArrayList<Segment> trans (ArrayList<Segment> first, ArrayList<Segment> second, float pct) {
        return trans(first, second, pct, 1 - pct);
    }

    public static Shape trans(Shape first, Shape second, float pct) {
        return toShape(trans(getSegments(first, null), getSegments(second, null), pct));
    }

    public static Path2D.Float toShape (ArrayList<Segment> segments) {
        Path2D.Float path = new Path2D.Float();
        for (Segment segment: segments) {
            path.append(segment.getIterator(), false);
        }

        return path;
    }

    public static ArrayList<Segment> getSegments (Shape shape, AffineTransform transform) {
        if (shape == null) {
            return new ArrayList<>();
        }

        PathIterator iter = shape.getPathIterator(transform);
        ArrayList<Segment> segments = new ArrayList<>();

        Segment current = new Segment();
        Point2D.Float lastMoveTo = null;

        while (!iter.isDone()) {
            float[] data = new float[6];
            int type = iter.currentSegment(data);

            if (type == PathIterator.SEG_CLOSE) {
                current.add(new Path(1, lastMoveTo.x, lastMoveTo.y));
                segments.add(current);
                current = new Segment();
            } else {
                Path path = current.add(type, data);
                if (type == PathIterator.SEG_MOVETO) {
                    lastMoveTo = path.getPoint();
                }
            }

            iter.next();
        }

        return segments;
    }

    public static ArrayList<ComplexFunction> getFuncs(Shape shape, AffineTransform transform) {
        PathIterator iter = shape.getPathIterator(transform);
        ArrayList<ComplexFunction> segments = new ArrayList<>();

        Segment current = new Segment();
        Point2D.Float lastMoveTo = null;

        while (!iter.isDone()) {
            float[] data = new float[6];
            int type = iter.currentSegment(data);

            if (type == PathIterator.SEG_CLOSE) {
                current.add(new Path(1, lastMoveTo.x, lastMoveTo.y));
                segments.add(current.getFunction());
                current = new Segment();
            } else {
                Path path = current.add(type, data);
                if (type == PathIterator.SEG_MOVETO) {
                    lastMoveTo = path.getPoint();
                }
            }

            iter.next();
        }

        return segments;
    }

    public static Polygon2D getPolygon (ComplexFunction func, int points) {
        float[] xpoints = new float[points];
        float[] ypoints = new float[points];

        TaskIterator iter = new TaskIterator((int i) -> {
            float t = (float) i / points;
            Comp pt = func.apply(t);

            xpoints[i] = pt.re;
            ypoints[i] = pt.im;
        }, (int i) -> i < points);

        iter.run();
        return new Polygon2D(xpoints, ypoints, points);
    }

    public static java.util.List<Polygon2D> getPolygons (ArrayList<ComplexFunction> segments, int points) {
        Stream<Polygon2D> stream = segments.stream().map(x -> getPolygon(x, points));
        return stream.collect(Collectors.toList());
    }

    public static java.util.List<Polygon2D> getPolygons (Shape shape, AffineTransform transform, int points) {
        ArrayList<ComplexFunction> segments = getFuncs(shape, transform);
        return getPolygons(segments, points);
    }

    public static boolean isPointInside (Point2D point, Collection<Polygon2D> segments) {
        for (Polygon2D poly: segments) {
            if (poly.contains(point)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isPointInside (Point2D point, Shape shape, int points, AffineTransform transform) {
        List<Polygon2D> polys = getPolygons(shape, transform, points);
        return isPointInside(point, polys);
    }

    public static class Segment extends ArrayList<Path> {
        AffineTransform transform;

        private Segment() {
            this.transform = new AffineTransform();
        }

        private Segment (AffineTransform transform) {
            this.transform = transform;
        }

        protected Path add (int type, float... data) {
            Path path = new Path(type, data);
            return this.add(path) ? path : null;
        }

        public Segment transform (AffineTransform transform) {
            Segment segment = new Segment(transform);
            segment.addAll(this);
            return segment;
        }

        public PathIterator getIterator () {
            return new PathIterator() {
                int i = 0;

                @Override
                public int getWindingRule() {
                    return 1;
                }

                @Override
                public boolean isDone() {
                    return i >= Segment.this.size();
                }

                @Override
                public void next() {
                    i++;
                }

                @Override
                public int currentSegment(float[] coords) {
                    Path path = get(i);
                    transform.transform(path.data, 0, coords, 0, path.data.length / 2);

                    return path.type;
                }

                @Override
                public int currentSegment(double[] coords) {
                    Path path = get(i);
                    transform.transform(path.data, 0, coords, 0, path.data.length / 2);

                    return path.type;
                }
            };
        }

        public ComplexFunction getFunction () {
            return FourierSeries.pathToFunction(getIterator());
        }

        @Override
        public Segment clone() {
            Segment segment = new Segment(transform);
            segment.addAll(this);

            return segment;
        }
    }

    private static class Path implements Cloneable {
        final public int type;
        final public float[] data;

        public Path (int type, float... data) {
            this.type = type;
            this.data = switch (this.type) {
                case PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO -> data.length == 2 ? data : Arrays.copyOf(data, 2);
                case PathIterator.SEG_QUADTO -> data.length == 4 ? data : Arrays.copyOf(data, 4);
                default -> data;
            };
        }

        public Path mul (float a) {
            float[] data = new float[this.data.length];
            for (int i=0;i<data.length;i++) {
                data[i] = this.data[i] * a;
            }

            return new Path(type, data);
        }

        public Path div (float a) {
            float[] data = new float[this.data.length];
            for (int i=0;i<data.length;i++) {
                data[i] = this.data[i] / a;
            }

            return new Path(type, data);
        }

        public float getX () {
            return this.data[this.data.length - 2];
        }

        public float getY () {
            return this.data[this.data.length - 1];
        }

        public Vec getXY (int pos) {
            float[] array = new float[2];
            System.arraycopy(this.data, pos, array, 0, 2);

            return new Vec(array);
        }

        public Point2D.Float getPoint () {
            return new Point2D.Float(getX(), getY());
        }

        public Vec getPointXY () {
            float[] array = new float[2];
            System.arraycopy(this.data, this.data.length - 2, array, 0, 2);
            return new Vec(array);
        }

        @Override
        public String toString() {
            String type = switch (this.type) {
                case PathIterator.SEG_MOVETO -> "MOVE TO";
                case PathIterator.SEG_LINETO -> "LINE TO";
                case PathIterator.SEG_QUADTO -> "QUADRATIC TO";
                case PathIterator.SEG_CUBICTO -> "CUBIC TO";
                default -> "END";
            };

            return type + " " + Arrays.toString(this.data);
        }
    }

    private static class MatxtShapeException extends RuntimeException {
        public MatxtShapeException(String message) {
            super(message);
        }
    }
}
