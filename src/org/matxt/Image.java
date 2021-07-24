package org.matxt;

import org.jcodec.common.Format;
import org.matxt.Action.Action;
import org.matxt.Element.Element;
import org.matxt.Extra.Defaults;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Image {
    public int width, height;
    public Color background = Defaults.BACKGROUND;
    private ArrayList<Element> elements;

    public Image (int width, int height) {
        this.width = width;
        this.height = height;
        this.elements = new ArrayList<>();
    }

    public void add (Element element) {
        this.elements.add(element);
    }

    public void render (File file, String format) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        float hw = (float) width / 2;
        float hh = (float) height / 2;

        for (Element element: elements) {
            element.draw(image, graphics, width, height, hw, hh);
        }

        graphics.dispose();
        ImageIO.write(image, format, file);
    }
}
