package org.matxt.Element;

import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMUseElement;
import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.svggen.SVGGraphics2D;
import org.matxt.Extra.Defaults;
import org.matxt.Extra.Regex;
import org.mozilla.javascript.ast.ForInLoop;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class LaTeX extends ElementShape {
    final private static Runtime runtime = Runtime.getRuntime();
    final private static File TMP = new File("tmp");
    final private static File WIN64 = new File("pdf2svg/x64");

    private String latex;
    private float size;

    public LaTeX (float x, float y, String latex, float size, boolean doCenter, Color color) {
        super(x, y, generateShape(latex, size), true, doCenter, color);
        this.latex = latex;
        this.size = size;
    }

    private LaTeX(float x, float y, Shape shape, boolean doFill, boolean doCenter, Color color, String latex, float size) {
        super(x, y, shape, doFill, doCenter, color);
        this.latex = latex;
        this.size = size;
    }

    public String getLatex() {
        return latex;
    }

    public void setLatex(String latex) {
        this.shape = generateShape(latex, size);
        this.latex = latex;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.shape = generateShape(latex, size);
        this.size = size;
    }

    @Override
    public LaTeX clone() {
        return new LaTeX(x, y, shape, doFill, doCenter, color, latex, size);
    }

    private static Path2D generateShape(String latex, float size) {
        if (!TMP.exists()) {
            TMP.mkdir();
        }

        String input = "\\documentclass[10pt]{article} % required\n" +
                "\\pagestyle{empty} % required\n" +
                "\\usepackage{amsmath}\n" +
                "\\usepackage{amssymb}\n" +
                "\\usepackage{color}\n" +
                "\\usepackage[T1]{fontenc}\n" +
                "\\begin{document}\n" +
                "\\definecolor{fgC}{rgb}{0,0,0}\n" +
                "\\color{fgC}\n" +
                "\\["+latex+"\\]\n" +
                "\\end{document}";

        File tex = new File(TMP, "latex.tex");
        File svg = new File(TMP, "latex.svg");

        try {
            Files.write(tex.toPath(), input.getBytes());
            Process proc = runtime.exec("pdflatex latex.tex -o latex.pdf", new String[0], TMP);

            while (proc.isAlive()) {
            }
            proc.destroy();

            if (Defaults.isWindows() && Defaults.is64Bit) {
                Process proc2 = runtime.exec("pdf2svg/x64/pdf2svg.exe tmp/latex.pdf tmp/latex.svg");
                while (proc2.isAlive()) {
                }
                proc2.destroy();
            } else if (Defaults.isWindows()) {
                Process proc2 = runtime.exec("pdf2svg/x32/pdf2svg.exe tmp/latex.pdf tmp/latex.svg");
                while (proc2.isAlive()) {
                }
                proc2.destroy();
            }

            SVGDocument doc = Defaults.loadSVG(svg);
            NodeList nodes = doc.getElementsByTagName("path");

            HashMap<String, Shape> symbols = new HashMap<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                SVGOMPathElement node = (SVGOMPathElement) nodes.item(i);
                String path = node.getAttribute("d");

                symbols.put(node.getParentNode().getAttributes().getNamedItem("id").getNodeValue(), AWTPathProducer.createShape(new StringReader(path), 1));
            }

            Path2D path = new Path2D.Float();
            NodeList usages = doc.getElementsByTagName("use");
            for (int i = 0; i < usages.getLength(); i++) {
                SVGOMUseElement use = (SVGOMUseElement) usages.item(i);
                NamedNodeMap attrs = use.getAttributes();

                String href = null;
                for (int j = 0; j < attrs.getLength(); j++) {
                    Node attr = attrs.item(j);
                    if (attr.getNodeName().equals("xlink:href")) {
                        href = attr.getNodeValue().substring(1);
                        break;
                    }
                }

                if (href == null) {
                    continue;
                }

                Shape subpath = symbols.get(href);
                float x = use.getX().getBaseVal().getValue();
                float y = use.getY().getBaseVal().getValue();

                PathIterator iter = subpath.getPathIterator(AffineTransform.getTranslateInstance(x, y));
                path.append(iter, false);
            }

            Rectangle2D bounds = path.getBounds2D();
            float scale = size;

            AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
            transform.translate(-bounds.getX(), -bounds.getY());

            path.transform(transform);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }
}
