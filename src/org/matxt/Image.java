package org.matxt;

import org.jcodec.common.Format;
import org.matxt.Action.Action;
import org.matxt.Element.Element;
import org.matxt.Extra.Config;
import org.matxt.Extra.Defaults;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Image extends ArrayList<Element> {
    public Image () {
        super();
    }

    public void render (File file, String format) throws IOException {
        BufferedImage image = new BufferedImage(Config.getWidth(), Config.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        for (Element element: this) {
            element.draw(image, graphics, Config.normX(element.x), Config.normY(element.y));
        }

        graphics.dispose();
        ImageIO.write(image, format, file);
    }
}
