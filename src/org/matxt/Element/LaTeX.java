package org.matxt.Element;

import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.svggen.SVGGraphics2D;
import org.image2svg.src.main.java.com.fisher.imageToSvg.processor.ImageToSvgGenerator;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;

public class LaTeX extends ElementShape {
    private String latex;
    private TeXFormula formula;
    private float size;

    public LaTeX (float x, float y, String latex, float size, boolean doCenter, Color color) {
        super(x, y, null, false, doCenter, color);
        this.latex = latex;
        this.formula = new TeXFormula(latex);
        this.size = size;

        try {
            this.shape = generateShape(this.formula, this.size);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private LaTeX (float x, float y, Shape shape, boolean doFill, boolean doCenter, Color color, String latex, TeXFormula formula, float size) {
        super(x, y, shape, doFill, doCenter, color);
        this.latex = latex;
        this.formula = formula;
        this.size = size;
    }

    public String getLatex () {
        return latex;
    }

    public boolean setLatex (String latex) {
        TeXFormula formula = new TeXFormula(this.latex);

        try {
            this.shape = generateShape(formula, size);
            this.latex = latex;
            this.formula = formula;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public TeXFormula getFormula () {
        return formula;
    }

    public float getSize() {
        return size;
    }

    public boolean setSize (float size) {
        try {
            this.shape = generateShape(formula, size);
            this.size = size;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public LaTeX clone() {
        return new LaTeX(x, y, shape, false, doCenter, color, latex, formula, size);
    }

    private static Shape generateShape (TeXFormula formula, float size) throws Exception {
        final double scale = 1d / 30;

        SVGGraphics2D svg = ImageToSvgGenerator.genSvg((BufferedImage) formula.createBufferedImage(TeXConstants.STYLE_DISPLAY, 30f * size, Color.BLACK, Color.WHITE), new String[]{ "#000000" });
        String pathStr = svg.getRoot().getElementsByTagName("path").item(0).getAttributes().getNamedItem("d").getNodeValue();

        return AffineTransform.getScaleInstance(scale, scale).createTransformedShape(AWTPathProducer.createShape(new StringReader(pathStr), 0));
    }
}
