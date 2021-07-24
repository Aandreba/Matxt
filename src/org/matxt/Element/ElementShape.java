package org.matxt.Element;

import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.PathParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;

public class ElementShape extends Element {
    public Shape shape;
    public float scale = 1f;
    public boolean doFill;
    public boolean doCenter;

    public ElementShape (float x, float y, Shape shape, boolean doFill, boolean doCenter, Color color) {
        super(x, y, color);
        this.shape = shape;
        this.doFill = doFill;
        this.doCenter = doCenter;
    }

    public ElementShape (float x, float y, File svg, boolean doFill, boolean doCenter, Color color) throws Exception {
        super(x, y, color);

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(svg);
        NodeList paths = doc.getElementsByTagName("path");
        String path = paths.item(0).getAttributes().getNamedItem("d").getNodeValue();

        this.shape = AWTPathProducer.createShape(new StringReader(path), 0);
        this.doFill = doFill;
        this.doCenter = doCenter;
    }

    @Override
    public void draw(BufferedImage image, Graphics2D graphics, int sceneWidth, int sceneHeight, float hw, float hh) {
        AffineTransform transform = AffineTransform.getTranslateInstance(x * hw + hw, -y * hh + hh);
        transform.scale(scale, scale);

        if (doCenter) {
            Rectangle2D bounds = shape.getBounds2D();
            transform.translate(-bounds.getWidth() / 2, bounds.getHeight() / 2);
        }

        AffineTransform prev = graphics.getTransform();
        graphics.setTransform(transform);

        if (doFill) {
            graphics.fill(shape);
        } else {
            graphics.draw(shape);
        }

        graphics.setTransform(prev);
    }

    @Override
    public ElementShape clone() {
        return new ElementShape(x, y, shape, doFill, doCenter, color);
    }
}
