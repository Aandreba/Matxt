package org.matxt.Action;

import org.jml.Complex.Single.Comp;
import org.matxt.Element.Polygon;
import org.matxt.Extra.Steps;

public class Rotate {
    public static Action<Polygon> polygon (Polygon element, float angle, float from, float to) {
        return new Action<>(element, (x,c,t) -> {
            float currentAngle = Steps.linear(0, angle, t);
            Comp alpha = Comp.expi(currentAngle);

            for (int i=0;i<c.getPoints();i++) {
                float[] point = c.getPoint(i);
                Comp XY = new Comp(point[0], point[1]).mul(alpha);
                x.setPoint(i, XY.real, XY.imaginary);
            }
        }, from, to);
    }
}
