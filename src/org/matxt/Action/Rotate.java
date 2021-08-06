package org.matxt.Action;

import org.matxt.Element.ElementShape;
import org.matxt.Extra.StepFunction;

public class Rotate {
    public static <T extends ElementShape> Action<T> polygon (T element, float angle, StepFunction step, float from, float to) {
        return new Action<>(element, (x,c,t) -> {
            element.setAngle(c.getAngle() + angle * step.apply(t));
        }, from, to);
    }
}
