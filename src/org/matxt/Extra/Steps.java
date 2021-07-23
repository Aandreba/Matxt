package org.matxt.Extra;

import org.jml.Calculus.Integral;
import org.jml.Mathx.Mathf;

public class Steps {
    public static float linear (float initValue, float finalValue, float time) {
        return initValue * (1 - time) + finalValue * time;
    }

    final private static float err = 1f / (1 + Mathf.exp(5));
    public static float smooth (float initValue, float finalValue, float time) {
        float alpha = 1f / (1 + Mathf.exp(10 * (time - 0.5f)));

        float x = (alpha - err) / (1 - 2 * err);
        return (finalValue - initValue) * Mathf.clamp(1 - x, 0, 1) + initValue;
    }

    public static float easeInOut (float initValue, float finalValue, float time) {
        if (time <= 0) {
            return 0;
        } else if (time >= 1) {
            return 1;
        } else if (time < 0.5f) {
            return Mathf.pow(2, 20 * time - 10) / 2;
        }

        return (finalValue - initValue) * (2 - Mathf.pow(2, -20 * time + 10)) / 2 + finalValue;
    }

    public static float thereAndBack (float initValue, float finalValue, float time) {
        float newT = time <= 0.5f ? 2 * time : 2 * (1 - time);
        return smooth(initValue, finalValue, newT);
    }
}
