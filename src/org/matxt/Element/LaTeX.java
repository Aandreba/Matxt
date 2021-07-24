package org.matxt.Element;

import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.svggen.SVGGraphics2D;
import org.matxt.Extra.Defaults;
import org.matxt.Extra.Regex;
import org.mozilla.javascript.ast.ForInLoop;
import org.w3c.dom.svg.SVGDocument;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class LaTeX extends Element {
    final private static Runtime runtime = Runtime.getRuntime();
    final private static File TMP = new File("tmp");
    final private static File WIN64 = new File("pdf2svg/x64");

    public String latex;
    public int size;

    public LaTeX (float x, float y, String latex, int size, Color color) {
        super(x, y, color);
        this.latex = latex;
        this.size = size;
    }

    @Override
    public void draw(BufferedImage image, Graphics2D graphics, int sceneWidth, int sceneHeight, float halfWidth, float halfHeight) {
        // TODO
    }

    @Override
    public LaTeX clone() {
        return new LaTeX(x, y, latex, size, color);
    }

    public SVGDocument generateSVG () throws IOException {
        if (!TMP.exists()) {
            TMP.mkdir();
        }

        String input = "\\documentclass["+size+"pt]{article} % required\n" +
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

        Files.write(tex.toPath(), input.getBytes());
        Process proc = runtime.exec("pdflatex latex.tex -o latex.pdf", new String[0], TMP);

        while (proc.isAlive()) {}
        proc.destroy();

        if (Defaults.isWindows() && Defaults.is64Bit) {
            Process proc2 = runtime.exec("pdf2svg/x64/pdf2svg.exe tmp/latex.pdf tmp/latex.svg");
            while (proc2.isAlive()) {}
            proc2.destroy();
        } else if (Defaults.isWindows()) {
            Process proc2 = runtime.exec("pdf2svg/x32/pdf2svg.exe tmp/latex.pdf tmp/latex.svg");
            while (proc2.isAlive()) {}
            proc2.destroy();
        }

        return Defaults.loadSVG(svg);
    }
}
