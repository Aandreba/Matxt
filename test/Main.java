import org.jml.Function.Real.DoubleReal;
import org.matxt.Element.LaTeX;
import org.matxt.Extra.Config;
import org.matxt.Extra.StepFunction;
import org.matxt.Video;

import java.awt.*;
import java.io.*;

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
        LaTeX element = new LaTeX(0, 0, "f(x) = 3x^4 + 2x^3 - 5x - 8", 50f, Color.white);
        Video video = new Video(2f, 30);

        video.add(element);
        video.draw(element, StepFunction.SMOOTH, 0f, 0.9f);
        video.render(new File(dir, "text.mov"));
    }
}
