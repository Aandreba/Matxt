package org.matxt.Action;

import org.jml.Complex.Single.Comp;
import org.jml.Mathx.Rand;
import org.matxt.Element.Element;
import org.matxt.Element.Body;
import org.matxt.Extra.Utils.Path;
import org.matxt.Extra.Utils.Segment;
import org.matxt.Extra.Utils.ShapeUtils;
import org.matxt.Extra.StepFunction;

import java.awt.*;
import java.util.ArrayList;

public class Transform {
    public static <T extends Element> Action<T> translate (T element, float dx, float dy, StepFunction step, float from, float to) {
        return new Action<>(element, (x,c,t) -> {
            float _t = step.apply(t);
            x.x = dx * _t + c.x;
            x.y = dy * _t + c.y;
        }, from, to);
    }

    public static <T extends Body> Action<T> rotate (T element, float angle, StepFunction step, float from, float to) {
        return new Action<>(element, (x,c,t) -> {
            x.setAngle(c.getAngle() + angle * step.apply(t));
        }, from, to);
    }

    public static <T extends Body> Action<T> replace (T element, ArrayList<Segment> target, StepFunction step, float from, float to) {
        ArrayList<Segment> origin = element.getSegments();
        ArrayList<Segment> end = (ArrayList<Segment>) target.clone();

        int delta = origin.size() - end.size();
        if (delta > 0) {
            for (int i=0;i<delta;i++) {
                Segment.Builder builder = new Segment.Builder(new Comp(0, 0));
                builder.add(new Path(1, 0, 0));
                end.add(builder.build());
            }
        } else if (delta < 0) {
            for (int i=0;i<-delta;i++) {
                Segment.Builder builder = new Segment.Builder(new Comp(0, 0));
                builder.add(new Path(1, 0, 0));
                origin.add(builder.build());
            }
        }

        for (int i=0;i<origin.size();i++) {
            Segment one = origin.get(i);
            Segment two = end.get(i);

            while (one.size() > two.size()) {
                two.split(Rand.getInt(1, two.size() - 2));
            }

            while (two.size() > one.size()) {
                one.split(Rand.getInt(1, one.size() - 2));
            }
        }

        return new Action<>(element, (x,c,t) -> {
            float pct = step.apply(t);
            if (pct >= 1) {
                x.setSegments(target);
            }

            ArrayList<Segment> segments = ShapeUtils.trans(origin, end, pct);
            x.setSegments(segments);
        }, from, to);
    }

    public static <T extends Body> Action<T> replace (T element, Shape target, StepFunction step, float from, float to) {
        return replace(element, ShapeUtils.getSegments(target, null), step, from, to);
    }

    public static <T extends Element> Action<T> visible (T element, float from, float to) {
        return new Action<>(element, (x,c,t) -> x.isVisible = true, from, to);
    }

    public static <T extends Element> Action<T> invisible (T element, float from, float to) {
        return new Action<>(element, (x,c,t) -> x.isVisible = false, from, to);
    }
}
