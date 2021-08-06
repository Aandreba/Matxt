package org.matxt.Extra;

import org.jml.Calculus.Integral;
import org.jml.Function.Real.DoubleReal;
import org.jml.Function.Real.FloatReal;
import org.jml.Function.Real.RealFunction;
import org.jml.Mathx.Mathf;

public interface StepFunction {
    StepFunction LINEAR = t -> t;
    StepFunction EASE_IN_OUT = t -> {
        if (t <= 0) {
            return 0;
        } else if (t >= 1) {
            return 1;
        } else if (t < 0.5f) {
            return Mathf.pow(2, 20 * t - 10) / 2;
        }

        return (2 - Mathf.pow(2, -20 * t + 10)) / 2 + 1;
    };
    StepFunction GAUSSIAN = t -> Integral.integ(0, t, (DoubleReal) x -> 2 * Math.exp(-Math.pow(x - 0.5, 2) / 0.08));
    StepFunction SMOOTH = t -> {
        FloatReal sigmoid = x -> 1 / (1 + Mathf.exp(-x));
        float error = sigmoid.apply(-5);
        return Mathf.min(Mathf.max((sigmoid.apply(10 * (t - 0.5f)) - error) / (1 - 2 * error), 0), 1);
    };

    /**
     *
     * @param t Abstract time that ranges from 0 (start) to 1 (end)
     * @return Adjusted abstract time
     */
    float apply (float t);
}
