package org.matxt.Extra.Utils;

import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.ComplexFunction;
import org.jml.Mathx.FourierSeries;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;

public class Segment {
    public static class Builder extends ArrayList<Path> {
        Comp initPos;

        public Builder (Comp initPos) {
            this.initPos = initPos;
            this.add(new Path(PathIterator.SEG_MOVETO, initPos.re, initPos.im));
        }

        public Comp getCurrentPos () {
            return get(size() - 2).getCompPoint();
        }

        public Path add (int type, float... data) {
            if (type != 0 && type != 4) {
                this.add(new Path(type, data));
            }

            return null;
        }

        public Segment build () {
            Segment out = new Segment();
            out.paths.addAll(this);
            out.add(1, initPos.re, initPos.im);

            return out;
        }
    }

    ArrayList<Path> paths;
    AffineTransform transform;

    protected Segment (AffineTransform transform) {
        this.paths = new ArrayList<>();
        this.transform = transform;
    }

    protected Segment() {
        this(new AffineTransform());
    }

    public int size () {
        return paths.size();
    }

    public Comp getInitPos () {
        return get(0).getCompPoint();
    }

    public Path get (int pos) {
        return paths.get(pos);
    }

    public void split (int pos) {
        Path path = get(pos);
        Path[] split = ShapeUtils.split(path, get(pos - 1).getCompPoint());

        this.paths.set(pos, split[0]);
        this.paths.add(pos + 1, split[0]);
    }

    protected Path add (int type, float... data) {
        Path path = new Path(type, data);
        paths.add(path);
        return path;
    }

    protected Path add (int pos, Path path) {
        paths.add(pos, path);
        return path;
    }

    protected Path add (Path path) {
        paths.add(path);
        return path;
    }

    public Segment transform (AffineTransform transform) {
        Segment segment = new Segment(transform);
        segment.paths.addAll(paths);
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
        segment.paths.addAll(paths);

        return segment;
    }
}
