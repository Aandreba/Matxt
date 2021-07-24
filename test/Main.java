import org.apache.batik.svggen.SVGGraphics2D;
import org.jml.Calculus.Integral;
import org.jml.Complex.Single.Comp;
import org.jml.Mathx.Mathf;
import org.matxt.Element.LaTeX;
import org.matxt.Extra.Defaults;
import org.matxt.Extra.StringOutputStream;
import org.matxt.Image;
import org.w3c.dom.svg.SVGDocument;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Main {
    final public static File dir = new File("/Users/Adebas/Desktop/Kerner Durand");

    public static void main (String... args) throws Exception {
        LaTeX latex = new LaTeX(0, 0, "\\sum_{n=0}^{\\infty}", 10, Color.WHITE);

        SVGDocument doc = latex.generateSVG();
        Integral.ComplexFunction function = Defaults.svgToFunction(doc);

        for (float t=0;t<=1;t+=0.001f) {
            System.out.println(t+": "+function.apply(t));
        }

        System.out.println();

        /*Image img = new Image(500, 500);
        img.add(latex);
        img.render(new File("draw.png"), "png");

        Video video = new Video(1920, 1080, 5f, 60);
        video.add(drawing);
        video.add(draw);

        video.render(new File("graph.mov"), Format.MOV, Codec.PRORES);*/
    }
}
