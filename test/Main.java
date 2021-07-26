import org.apache.batik.parser.AWTPathProducer;
import org.jml.Complex.Double.Compd;
import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.ComplexFunction;
import org.jml.Mathx.FourierSeries;
import org.jml.Mathx.Mathf;
import org.matxt.Action.Draw;
import org.matxt.Element.LaTeX;
import org.matxt.Video;
import org.w3c.dom.svg.SVGDocument;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Main {
    final public static File dir = new File("/Users/Adebas/Desktop/Kerner Durand");

    public static void main (String... args) throws Exception {
        LaTeX latex = new LaTeX(0, 0, "\\sum_{n=0}^{\\infty}", 24, false, Color.WHITE);
        Video video = new Video(1920, 1080, 5f, 60);

        video.add(latex);
        video.add(Draw.shape(latex, video, 0, 1));

        video.render(new File("graph.mov"));
    }
}
