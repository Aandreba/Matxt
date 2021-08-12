package org.matxt.Element;

import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMSymbolElement;
import org.apache.batik.anim.dom.SVGOMUseElement;
import org.apache.batik.parser.AWTPathProducer;
import org.matxt.Extra.Config;
import org.matxt.Extra.Defaults;
import org.matxt.Extra.Regex;
import org.matxt.Extra.Utils.Segment;
import org.matxt.Extra.Utils.ShapeUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LaTeX extends Body {
    private String latex;
    private Align align;

    public LaTeX (float x, float y, String latex, float size, Color color) {
        super(x, y, generateShape(latex, Align.Display), true, color);
        this.align = Align.Display;
        this.latex = latex;
        this.scale = size / 10f;
    }

    public LaTeX (float x, float y, String latex, Align align, float size, Color color) {
        super(x, y, generateShape(latex, align), true, color);
        this.latex = latex;
        this.align = align;
        this.scale = size / 10f;
    }

    private LaTeX(float x, float y, Color color, boolean isVisible, Shape shape, ArrayList<Segment> segments, float scale, float angle, Stroke stroke, boolean doFill, String latex, Align align) {
        super(x, y, color, isVisible, shape, segments, scale, angle, stroke, doFill);
        this.latex = latex;
        this.align = align;
    }

    public String getLatex() {
        return latex;
    }

    public void setLatex (String latex) {
        this.shape = generateShape(latex, align);
        this.latex = latex;
    }

    public Align getAlign() {
        return align;
    }

    public void setAlign(Align align) {
        this.shape = generateShape(latex, align);
        this.align = align;
    }

    public float getSize () {
        return this.scale * 10f;
    }

    public void setSize (float size) {
        this.scale = size / 10f;
    }

    @Override
    public LaTeX clone() {
        return new LaTeX(x, y, color, isVisible, new Path2D.Float(shape), getSegments(), getScale(), getAngle(), getStroke(), doFill, latex, align);
    }

    public static Path2D generateShape (String latex) {
        return generateShape(latex, Align.Display);
    }

    public static Path2D generateShape (String latex, Align align) {
        System.out.println("Loading LaTeX script");

        String input = "\\documentclass[10pt]{article} % required\n" +
                "\\pagestyle{empty} % required\n" +
                "\\usepackage{amsmath}\n" +
                "\\usepackage{amssymb}\n" +
                "\\usepackage{color}\n" +
                "\\usepackage[T1]{fontenc}\n" +
                "\\begin{document}\n" +
                "\\definecolor{fgC}{rgb}{0,0,0}\n" +
                "\\color{fgC}\n" +
                align.start + latex + align.end +
                "\\end{document}";

        File tex = Config.getTemporaryFile("latex.tex");
        File pdf = Config.getTemporaryFile("latex.pdf");
        File svg = Config.getTemporaryFile("latex.svg");

        try {
            Files.write(tex.toPath(), input.getBytes()); // TODO FIX FOR MAC
            Process proc = Config.RUNTIME.exec("pdflatex latex.tex -o latex.pdf", new String[0], Config.getTempDir());
            //while (proc.isAlive()){}

            System.out.println(new String(proc.getErrorStream().readAllBytes()));
            System.out.println(new String(proc.getInputStream().readAllBytes()));

            proc.destroy();

            if (Defaults.isWindows() && Defaults.is64Bit) {
                Process proc2 = Config.RUNTIME.exec('"'+Config.getPdf2Svg_x64().getAbsolutePath()+"\" \""+pdf.getAbsolutePath()+"\" \""+svg.getAbsolutePath()+'"');
                while (proc2.isAlive()) {}
                proc2.destroy();
            } else if (Defaults.isWindows()) {
                Process proc2 = Config.RUNTIME.exec('"'+Config.getPdf2Svg_x86().getAbsolutePath()+"\" \""+pdf.getAbsolutePath()+"\" \""+svg.getAbsolutePath()+'"');
                while (proc2.isAlive()) {}
                proc2.destroy();
            } else {
                Process proc2 = Config.RUNTIME.exec("pdf2svg \""+pdf.getAbsolutePath()+"\" \""+svg.getAbsolutePath()+'"');
                while (proc2.isAlive()) {}
                proc2.destroy();
            }

            SVGDocument doc = Defaults.loadSVG(svg);
            Path2D path = new Path2D.Float();

            NodeList nodes = doc.getElementsByTagName("path");
            HashMap<String, Shape> symbols = new HashMap<>();

            for (int i = 0; i < nodes.getLength(); i++) {
                SVGOMPathElement node = (SVGOMPathElement) nodes.item(i);
                String spath = node.getAttribute("d");
                Shape shape = AWTPathProducer.createShape(new StringReader(spath), 1);

                Node parent = node.getParentNode();
                if (!(parent instanceof SVGOMSymbolElement)) {
                    Float[] nums = Regex.matches(node.getAttribute("transform"), "\\d+(\\.\\d+){0,1}").stream().map(Float::valueOf).toArray(Float[]::new);
                    AffineTransform transform = new AffineTransform(nums[0], nums[1], nums[2], nums[3], nums[4], nums[5]);

                    String[] styleList = node.getAttribute("style").split(";");
                    HashMap<String, String> styles = new HashMap<>(){{
                        for (String style: styleList) {
                            String[] split = style.split(":");
                            put(split[0], split[1]);
                        }
                    }};

                    if (Arrays.stream(styleList).anyMatch(x -> x.startsWith("stroke"))) {
                        String widthStr = styles.get("stroke-width");
                        String capStr = styles.get("stroke-linecap");
                        String joinStr = styles.get("stroke-linejoin");
                        String miterlimitStr = styles.get("stroke-miterlimit");

                        float width = widthStr == null ? 1f : Float.parseFloat(widthStr);
                        float miterlimit = miterlimitStr == null ? 10f : Float.parseFloat(miterlimitStr);

                        int cap = capStr == null ? BasicStroke.CAP_SQUARE : switch (capStr.toLowerCase()) {
                            case "round" -> BasicStroke.CAP_ROUND;
                            case "butt" -> BasicStroke.CAP_BUTT;
                            default -> BasicStroke.CAP_SQUARE;
                        };

                        int join = joinStr == null ? BasicStroke.JOIN_MITER : switch (joinStr.toLowerCase()) {
                            case "round" -> BasicStroke.JOIN_ROUND;
                            case "bevel" -> BasicStroke.JOIN_BEVEL;
                            default -> BasicStroke.JOIN_MITER;
                        };

                        BasicStroke stroke = new BasicStroke(width, cap, join, miterlimit);
                        shape = stroke.createStrokedShape(shape);
                    }

                    path.append(transform.createTransformedShape(shape), false);
                    continue;
                }

                symbols.put(parent.getAttributes().getNamedItem("id").getNodeValue(), shape);
            }

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
            AffineTransform transform = AffineTransform.getTranslateInstance(-bounds.getX(), -bounds.getY());

            path.transform(transform);
            System.out.println("Script Loaded! \n");
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
    }

    public enum Align {
        Display ("\\[", "\\]\n"),
        Align("\\begin{align*}", "\\end{align*}");

        final public String start, end;
        Align (String start, String end) {
            this.start = start;
            this.end = end;
        }
    }
}
