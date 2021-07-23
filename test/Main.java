import org.jcodec.common.Codec;
import org.jcodec.common.Format;
import org.jml.Mathx.Mathf;
import org.matxt.Action.Draw;
import org.matxt.Element.Graph;
import org.matxt.Element.LaTeX;
import org.matxt.Element.Text;
import org.matxt.Video;

import java.awt.*;
import java.beans.Encoder;
import java.io.*;

public class Main {
    final public static File dir = new File("/Users/Adebas/Desktop/Kerner Durand");

    public static void main (String... args) throws IOException {
        Graph graph = new Graph(0, 0, 0.5f, 0.5f, -10, 10.3f, 1, -1.5f, 1.5f, 1, Color.GREEN);
        graph.add(Mathf::sin, Color.RED);
        graph.add(Mathf::cos, Color.CYAN);

        Video video = new Video(1920, 1080, 5f, 30);
        video.add(graph);

        video.render(new File(dir, "graph.mov"), Format.MOV, Codec.PRORES);
    }
}
