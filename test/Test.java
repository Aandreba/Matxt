import org.jml.Complex.Single.Comp;
import org.jml.Mathx.Mathf;
import org.matxt.Action.Transform;
import org.matxt.Element.LaTeX;
import org.matxt.Extra.Config;
import org.matxt.Extra.StepFunction;
import org.matxt.Video;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class Test {
    final public static File dir = new File("D:\\Videos\\Durand-Kerner");

    static {
        try {
            Config.setPdf2Svg_x64(new File("pdf2svg/x64/pdf2svg.exe"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main (String... args) throws IOException {
        Config.setDuration(2);

        LaTeX element = new LaTeX(-0.5f, 0.875f, "f(x) = 3x^4 + 2x^3 - 5x - 8", 50f, Color.white);
        LaTeX element1 = new LaTeX(-0.5f, 0.75f,"f(x) = (x - \\alpha)(x - \\beta)(x - \\gamma)(x - \\omega)", 50f, Color.white);
        LaTeX alpha = new LaTeX(0, 0, "\\alpha = \\alpha_0 - \\frac{f(\\alpha_0)}{(\\alpha_0 - \\beta)(\\alpha_0 - \\gamma)(\\alpha_0 - \\omega)}", 50f, Color.white);
        LaTeX guess = new LaTeX(0.5f, 0.5f, "\\alpha_0 = 1 + i \\\\ \\beta_0 = 2 + 0.5i \\\\ \\gamma_0 = 0.5 + 2i \\\\ \\omega_0 = -5 - 5i", LaTeX.Align.Align, 35f, Color.white);

        Video video = new Video();
        video.addAll(element, element1, alpha, guess);

        video.add(Transform.replace(element, LaTeX.generateShape("f(x) = x^4 + \\frac{2}{3}x^3 - \\frac{5}{3}x - \\frac{8}{3}"), StepFunction.SMOOTH, 0, Config.relTime(1.5f)));
        video.add(Transform.translate(element1, 0, -0.125f, StepFunction.SMOOTH, 0, Config.relTime(1.5f)));
        video.add(Transform.translate(guess, 0.25f, 0.25f, StepFunction.SMOOTH, Config.relTime(1.5f), Config.relTime(2)));

        video.render(new File(dir, "text8.mov"));
    }
}
