package org.matxt.Extra;

import org.apache.batik.anim.dom.*;
import org.apache.batik.bridge.*;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.dom.svg.SVGPathContext;
import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.jml.Calculus.Integral;
import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.ComplexFunction;
import org.jml.GPGPU.OpenCL.Platform;
import org.jml.MT.TaskIterator;
import org.jml.Mathx.FourierSeries;
import org.jml.Mathx.NativeUtils;
import org.w3c.dom.*;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGPoint;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Defaults {
    public enum OS {
        Windows,
        macOS,
        Linux,
        Unknown
    }

    final public static Color BACKGROUND = new Color(25, 27, 41);
    final public static boolean is64Bit = System.getProperty("sun.arch.data.model").equals("64");
    final public static OS OS;

    static {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            OS = Defaults.OS.Windows;
        } else if (os.contains("mac")) {
            OS = Defaults.OS.macOS;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            OS = Defaults.OS.Linux;
        } else {
            OS = Defaults.OS.Unknown;
        }
    }

    public static boolean isWindows () {
        return OS == Defaults.OS.Windows;
    }

    public static boolean isMac () {
        return OS == Defaults.OS.macOS;
    }

    public static boolean isLinux () {
        return OS == Defaults.OS.Linux;
    }

    public static SVGDocument loadSVG (File file) throws IOException {
        UserAgent userAgent;
        DocumentLoader loader;
        BridgeContext ctx;

        userAgent = new UserAgentAdapter();
        loader = new DocumentLoader(userAgent);
        ctx = new BridgeContext(userAgent, loader);
        ctx.setDynamicState(BridgeContext.DYNAMIC);

        URI fileURI = file.toURI();
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory svgf = new SAXSVGDocumentFactory(parser);
        return (SVGDocument) svgf.createDocument(fileURI.toString());
    }

    public static SVGDocument loadSVG (String svg) throws IOException {
        UserAgent userAgent;
        DocumentLoader loader;
        BridgeContext ctx;

        userAgent = new UserAgentAdapter();
        loader = new DocumentLoader(userAgent);
        ctx = new BridgeContext(userAgent, loader);
        ctx.setDynamicState(BridgeContext.DYNAMIC);

        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory svgf = new SAXSVGDocumentFactory(parser);
        return (SVGDocument) svgf.createDocument(svg);
    }

    protected static boolean isWindowsExe (File file) {
        if (!file.exists() | !file.isFile()) {
            return false;
        }

        byte[] bytes = new byte[2];
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytes);
            fis.close();
        } catch (Exception ignore) { return false; }

        return bytes[0] == 0x4d && bytes[1] == 0x5a;
    }

    public static ArrayList<ComplexFunction> getSegments (Shape shape, AffineTransform transform) {
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

    public static List<Polygon2D> getPolygons (ArrayList<ComplexFunction> segments, int points) {
        Stream<Polygon2D> stream = segments.stream().map(x -> getPolygon(x, points));
        return stream.collect(Collectors.toList());
    }

    public static List<Polygon2D> getPolygons (Shape shape, AffineTransform transform, int points) {
        ArrayList<ComplexFunction> segments = getSegments(shape, transform);
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

    private static class Segment extends ArrayList<Path> {
        public Segment() {}

        public Path add (int type, float[] data) {
            Path path = new Path(type, data);
            return this.add(path) ? path : null;
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
                    System.arraycopy(path.data, 0, coords, 0, path.data.length);
                    return path.type;
                }

                @Override
                public int currentSegment(double[] coords) {
                    Path path = get(i);
                    for (int i=0;i<path.data.length;i++) {
                        coords[i] = path.data[i];
                    }

                    return path.type;
                }
            };
        }

        public ComplexFunction getFunction () {
            return FourierSeries.pathToFunction(getIterator());
        }
    }

    private static class Path {
        final public int type;
        final public float[] data;

        public Path (int type, float... data) {
            this.type = type;
            this.data = switch (this.type) {
                case PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO -> Arrays.copyOf(data, 2);
                case PathIterator.SEG_QUADTO -> Arrays.copyOf(data, 4);
                case PathIterator.SEG_CUBICTO -> data;
                default -> new float[0];
            };
        }

        public float getX () {
            return this.data[this.data.length - 2];
        }

        public float getY () {
            return this.data[this.data.length - 1];
        }

        public Point2D.Float getPoint () {
            return new Point2D.Float(getX(), getY());
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
}
