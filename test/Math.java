import org.matxt.Action.Transform;
import org.matxt.Element.Body;
import org.matxt.Element.LaTeX;
import org.matxt.Element.Text;
import org.matxt.Extra.Config;
import org.matxt.Extra.StepFunction;
import org.matxt.Extra.Utils.Path;
import org.matxt.Extra.Utils.Segment;
import org.matxt.Extra.Utils.ShapeUtils;
import org.matxt.Image;
import org.matxt.Video;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Math {
    static {
        try {
            Config.setPdf2Svg_x64(new File("pdf2svg/x64/pdf2svg.exe"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main (String... args) throws IOException {
        Config.setDuration(3f);
        LaTeX text = new LaTeX(0, 0, "f(x) = x^2 - 3x", 100f, Color.WHITE);

        Video video = new Video();
        video.add(text);
        video.add(Transform.replace(text, LaTeX.generateShape("f'(x) = 2x - 3", LaTeX.Align.Display), StepFunction.SMOOTH, 0, 0.9f));
        video.render(new File("replaceNew.mov"));
    }
}
