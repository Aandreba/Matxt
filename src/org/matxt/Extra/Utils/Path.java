package org.matxt.Extra.Utils;

import org.jml.Complex.Single.Comp;
import org.jml.Vector.Single.Vec;

import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Arrays;

public class Path implements Cloneable {
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

    public Comp getCompPoint () {
        return new Comp(getX(), getY());
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
