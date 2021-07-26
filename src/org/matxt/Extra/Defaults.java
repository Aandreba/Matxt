package org.matxt.Extra;

import org.apache.batik.anim.dom.*;
import org.apache.batik.bridge.*;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.dom.svg.SVGPathContext;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.jml.Calculus.Integral;
import org.jml.Complex.Single.Comp;
import org.jml.GPGPU.OpenCL.Platform;
import org.jml.Mathx.NativeUtils;
import org.w3c.dom.*;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGPoint;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class Defaults {
    public enum OS {
        Windows,
        macOS,
        Linux,
        Unknown
    }

    final public static Color BACKGROUND = new Color(25, 27, 41);
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
}
