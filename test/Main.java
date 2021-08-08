import org.jml.Complex.Single.Comp;
import org.jml.Mathx.Mathf;
import org.matxt.Action.Transform;
import org.matxt.Element.LaTeX;
import org.matxt.Extra.Config;
import org.matxt.Extra.StepFunction;
import org.matxt.Video;

import java.awt.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;

public class Main {
    final public static File dir = new File("D:\\Videos\\Durand-Kerner");

    static {
        try {
            Config.setPdf2Svg_x64(new File("pdf2svg/x64/pdf2svg.exe"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main (String... args) throws Exception {
        Config.setDuration(12);

        Comp alpha_n = new Comp(1, 1);
        Comp beta_n = new Comp(2, 0.5f);
        Comp gamma_n = new Comp(0.5f, 2);
        Comp omega_n = new Comp(-5, -5);

        LaTeX element = new LaTeX(-0.5f, 0.875f, "f(x) = 3x^4 + 2x^3 - 5x - 8", 50f, Color.white);
        LaTeX element1 = new LaTeX(-0.5f, 0.75f,"f(x) = (x - \\alpha)(x - \\beta)(x - \\gamma)(x - \\omega)", 50f, Color.white);
        LaTeX guess = new LaTeX(0.5f, 0.5f, "\\alpha_0 = 1 + i \\\\ \\beta_0 = 2 + 0.5i \\\\ \\gamma_0 = 0.5 + 2i \\\\ \\omega_0 = -5 - 5i", LaTeX.Align.Align, 35f, Color.white);

        LaTeX alpha = new LaTeX(0, 0, "\\alpha = \\alpha_0 - \\frac{f(\\alpha_0)}{(\\alpha_0 - \\beta_0)(\\alpha_0 - \\gamma_0)(\\alpha_0 - \\omega_0)}", 50f, Color.white);
        LaTeX beta = new LaTeX(0, 0, "\\alpha = \\alpha_0 - \\frac{f(\\alpha_0)}{(\\alpha_0 - \\beta_0)(\\alpha_0 - \\gamma_0)(\\alpha_0 - \\omega_0)}", 50f, Color.white);
        LaTeX gamma = new LaTeX(0, 0, "\\alpha = \\alpha_0 - \\frac{f(\\alpha_0)}{(\\alpha_0 - \\beta_0)(\\alpha_0 - \\gamma_0)(\\alpha_0 - \\omega_0)}", 50f, Color.white);
        LaTeX omega = new LaTeX(0, 0, "\\alpha = \\alpha_0 - \\frac{f(\\alpha_0)}{(\\alpha_0 - \\beta_0)(\\alpha_0 - \\gamma_0)(\\alpha_0 - \\omega_0)}", 50f, Color.white);

        Video video = new Video();
        video.add(element);
        video.add(element1);
        video.add(guess);

        video.add(alpha);
        video.add(beta);
        video.add(gamma);
        video.add(omega);

        video.add(Transform.replace(element, LaTeX.generateShape("f(x) = x^4 + \\frac{2}{3}x^3 - \\frac{5}{3}x - \\frac{8}{3}"), StepFunction.SMOOTH, 0, Config.relTime(1.5f)));
        video.add(Transform.translate(element1, 0, -0.125f, StepFunction.SMOOTH, 0, Config.relTime(1.5f)));

        video.add(Transform.translate(guess, 0.25f, 0.25f, StepFunction.SMOOTH, Config.relTime(1.5f), Config.relTime(2)));
        video.add(Transform.translate(alpha, 0, 0.15f, StepFunction.SMOOTH, Config.relTime(1.5f), Config.relTime(2)));
        video.add(Transform.translate(beta, 0, -0.15f, StepFunction.SMOOTH, Config.relTime(1.5f), Config.relTime(2)));
        video.add(Transform.translate(gamma, 0, -0.45f, StepFunction.SMOOTH, Config.relTime(1.5f), Config.relTime(2)));
        video.add(Transform.translate(omega, 0, -0.75f, StepFunction.SMOOTH, Config.relTime(1.5f), Config.relTime(2)));

        float to = Config.relTime(-0.5f);
        for (int i=0;i<1;i++) {
            int j = i + 1;

            float from1 = to + Config.relTime(1);
            float to1 = from1 + Config.relTime(2);

            float from2 = to1 + Config.relTime(1);
            float to2 = from2 + Config.relTime(2);

            float from3 = to2 + Config.relTime(1);
            to = from3 + Config.relTime(2);

            if (i > 0) {
                video.add(Transform.translate(beta, 0, -0.125f, StepFunction.SMOOTH, from1, to1));
                video.add(Transform.translate(gamma, 0, -0.25f, StepFunction.SMOOTH, from1, to1));
                video.add(Transform.translate(omega, 0, -0.375f, StepFunction.SMOOTH, from1, to1));
            }

            video.add(Transform.replace(alpha, LaTeX.generateShape("\\alpha_"+j+" = \\alpha_"+i+" - \\frac{f(\\alpha_"+i+")}{(\\alpha_"+i+" - \\beta_"+i+")(\\alpha_"+i+" - \\gamma_"+i+")(\\alpha_"+i+" - \\omega_"+i+")}"), StepFunction.SMOOTH, from1, to1));
            video.add(Transform.replace(beta, LaTeX.generateShape("\\beta_"+j+" = \\beta_"+i+" - \\frac{f(\\beta_"+i+")}{(\\beta_"+i+" - \\alpha_"+i+")(\\beta_"+i+" - \\gamma_"+i+")(\\beta_"+i+" - \\omega_"+i+")}"), StepFunction.SMOOTH, from1, to1));
            video.add(Transform.replace(gamma, LaTeX.generateShape("\\gamma_"+j+" = \\gamma_"+i+" - \\frac{f(\\gamma_"+i+")}{(\\gamma_"+i+" - \\alpha_"+i+")(\\gamma_"+i+" - \\beta_"+i+")(\\gamma_"+i+" - \\omega_"+i+")}"), StepFunction.SMOOTH, from1, to1));
            video.add(Transform.replace(omega, LaTeX.generateShape("\\omega_"+j+" = \\omega_"+i+" - \\frac{f(\\omega_"+i+")}{(\\omega_"+i+" - \\alpha_"+i+")(\\omega_"+i+" - \\beta_"+i+")(\\omega_"+i+" - \\gamma_"+i+")}"), StepFunction.SMOOTH, from1, to1));

            if (i == 0) {
                continue;
            }

            video.add(Transform.replace(alpha, LaTeX.generateShape("\\alpha_"+j+" = "+toString(alpha_n)+" - \\frac{f("+toString(alpha_n)+")}{("+ toString(alpha_n.subtr(beta_n))+")("+ toString(alpha_n.subtr(gamma_n))+")("+ toString(alpha_n.subtr(omega_n))+")}"), StepFunction.SMOOTH, from2, to2));
            video.add(Transform.replace(beta, LaTeX.generateShape("\\beta_"+j+" = "+toString(beta_n)+" - \\frac{f("+toString(beta_n)+")}{("+ toString(beta_n.subtr(alpha_n))+")("+ toString(beta_n.subtr(gamma_n))+")("+ toString(beta_n.subtr(omega_n))+")}"), StepFunction.SMOOTH, from2, to2));
            video.add(Transform.replace(gamma, LaTeX.generateShape("\\gamma_"+j+" = "+toString(gamma_n)+" - \\frac{f("+toString(gamma_n)+")}{("+ toString(gamma_n.subtr(alpha_n))+")("+ toString(gamma_n.subtr(beta_n))+")("+ toString(gamma_n.subtr(omega_n))+")}"), StepFunction.SMOOTH, from2, to2));
            video.add(Transform.replace(omega, LaTeX.generateShape("\\omega_"+j+" = "+toString(omega_n)+" - \\frac{f("+toString(omega_n)+")}{("+ toString(omega_n.subtr(alpha_n))+")("+ toString(omega_n.subtr(beta_n))+")("+ toString(omega_n.subtr(gamma_n))+")}"), StepFunction.SMOOTH, from2, to2));

            alpha_n = iter(alpha_n, beta_n, gamma_n, omega_n);
            beta_n = iter(beta_n, gamma_n, omega_n, alpha_n);
            gamma_n = iter(gamma_n, omega_n, alpha_n, beta_n);
            omega_n = iter(omega_n, alpha_n, beta_n, gamma_n);

            video.add(Transform.translate(beta, 0, 0.125f, StepFunction.SMOOTH, from3, to));
            video.add(Transform.translate(gamma, 0, 0.25f, StepFunction.SMOOTH, from3, to));
            video.add(Transform.translate(omega, 0, 0.375f, StepFunction.SMOOTH, from3, to));

            video.add(Transform.replace(alpha, LaTeX.generateShape("\\alpha_"+j+" = "+toString(alpha_n)), StepFunction.SMOOTH, from3, to));
            video.add(Transform.replace(beta, LaTeX.generateShape("\\beta_"+j+" = "+toString(beta_n)), StepFunction.SMOOTH, from3, to));
            video.add(Transform.replace(gamma, LaTeX.generateShape("\\gamma_"+j+" = "+toString(gamma_n)), StepFunction.SMOOTH, from3, to));
            video.add(Transform.replace(omega, LaTeX.generateShape("\\omega_"+j+" = "+toString(omega_n)), StepFunction.SMOOTH, from3, to));
            video.add(Transform.replace(guess, LaTeX.generateShape("\\alpha_"+j+" = "+toString(alpha_n)+" \\\\ \\beta_"+j+" = "+toString(beta_n)+" \\\\ \\gamma_"+j+" = "+toString(gamma_n)+" \\\\ \\omega_"+j+" = "+toString(omega_n), LaTeX.Align.Align), StepFunction.SMOOTH, from3, to));
        }

        video.render(new File(dir, "text9.mov"));
    }

    private static String toString(Comp comp) {
        String str = toString(comp.re);

        if (comp.im != 0) {
            return str + (comp.im < 0 ? " - " : " + ") + (Mathf.abs(comp.im) == 1 ? "" : toString(Mathf.abs(comp.im))) + "i";
        }

        return str;
    }

    final private static NumberFormat format = NumberFormat.getNumberInstance(Locale.ENGLISH);
    static {
        format.setMaximumFractionDigits(2);
    }

    private static String toString (float val) {
        return format.format(val);
    }

    private static Comp function (Comp a) {
        Comp p2 = a.pow(2);
        Comp p3 = p2.mul(a);
        Comp p4 = p2.pow(2);

        return p4.add(p3.mul(2 / 3f)).subtr(a.mul(5 / 3f)).subtr(8 / 3f);
    }

    private static Comp iter (Comp a, Comp b, Comp c, Comp d) {
        return a.subtr(function(a).div(a.subtr(b).mul(a.subtr(c)).mul(a.subtr(d))));
    }
}
