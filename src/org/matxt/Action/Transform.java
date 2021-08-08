package org.matxt.Action;

import org.matxt.Element.Element;
import org.matxt.Element.Body;
import org.matxt.Extra.ShapeUtils;
import org.matxt.Extra.StepFunction;
import org.matxt.Video;

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

    public static <T extends Body> Action<T> replace (T element, ArrayList<ShapeUtils.Segment> target, StepFunction step, float from, float to) {
        return new Action<>(element, (x,c,t) -> {
            float pct = step.apply(t);
            if (pct >= 1) {
                x.setSegments(target);
            }

            ArrayList<ShapeUtils.Segment> segments = ShapeUtils.trans(c.getSegments(), target, pct);
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
