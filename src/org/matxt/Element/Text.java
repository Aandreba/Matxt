package org.matxt.Element;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Text extends ElementShape {
    final public static Font[] FONTS = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    final public static Font DEFAULT = FONTS[0];

    private String text;
    private Font font;

    public Text (float x, float y, String text, Font font, boolean doCenter, Color color) {
        super(x, y, generateShape(text, font), false, doCenter, color);
        this.text = text;
        this.font = font;
        this.doCenter = doCenter;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        this.shape = generateShape(text, font);
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
        this.shape = generateShape(text, font);
    }

    public Text (float x, float y, String text, float size, boolean doCenter, Color color) {
        this(x, y, text, DEFAULT.deriveFont(size), doCenter, color);
    }

    @Override
    public Text clone() {
        return new Text(x, y, text, font, doCenter, color);
    }

    private static Shape generateShape (String text, Font font) {
        FontRenderContext ctx = new FontRenderContext(new AffineTransform(), true, false);
        TextLayout layout = new TextLayout(text, font, ctx);
        return layout.getOutline(null);
    }
}
