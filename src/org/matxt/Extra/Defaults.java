package org.matxt.Extra;

import org.apache.batik.anim.dom.*;
import org.apache.batik.bridge.*;
import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.batik.util.XMLResourceDescriptor;
import org.jml.Complex.Single.Comp;
import org.jml.Function.Complex.ComplexFunction;
import org.jml.MT.TaskIterator;
import org.jml.Mathx.FourierSeries;
import org.w3c.dom.svg.SVGDocument;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Defaults {
    public enum OS {
        Windows,
        macOS,
        Linux,
        Unknown
    }

    final public static boolean is64Bit = System.getProperty("sun.arch.data.model").equals("64");
    final public static OS OS;

    static {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            OS = Defaults.OS.Windows;
        } else if (os.contains("mac")) {
            OS = Defaults.OS.macOS;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            OS = Defaults.OS.Linux;
        } else {
            OS = Defaults.OS.Unknown;
        }
    }

    public static boolean isWindows () {
        return OS == Defaults.OS.Windows;
    }

    public static boolean isMac () {
        return OS == Defaults.OS.macOS;
    }

    public static boolean isLinux () {
        return OS == Defaults.OS.Linux;
    }

    public static SVGDocument loadSVG (File file) throws IOException {
        UserAgent userAgent;
        DocumentLoader loader;
        BridgeContext ctx;

        userAgent = new UserAgentAdapter();
        loader = new DocumentLoader(userAgent);
        ctx = new BridgeContext(userAgent, loader);
        ctx.setDynamicState(BridgeContext.DYNAMIC);

        URI fileURI = file.toURI();
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory svgf = new SAXSVGDocumentFactory(parser);
        return (SVGDocument) svgf.createDocument(fileURI.toString());
    }

    public static SVGDocument loadSVG (String svg) throws IOException {
        UserAgent userAgent;
        DocumentLoader loader;
        BridgeContext ctx;

        userAgent = new UserAgentAdapter();
        loader = new DocumentLoader(userAgent);
        ctx = new BridgeContext(userAgent, loader);
        ctx.setDynamicState(BridgeContext.DYNAMIC);

        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory svgf = new SAXSVGDocumentFactory(parser);
        return (SVGDocument) svgf.createDocument(svg);
    }

    protected static boolean isWindowsExe (File file) {
        if (!file.exists() | !file.isFile()) {
            return false;
        }

        byte[] bytes = new byte[2];
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytes);
            fis.close();
        } catch (Exception ignore) { return false; }

        return bytes[0] == 0x4d && bytes[1] == 0x5a;
    }
}
