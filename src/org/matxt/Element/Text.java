package org.matxt.Element;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

public class Text extends Body {
    final public static Font[] FONTS = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    final public static Font DEFAULT = FONTS[0];

    private String text;
    private Font font;

    public Text (float x, float y, String text, Font font, Color color) {
        super(x, y, generateShape(text, font = font.deriveFont(font.getSize2D() * 3f)), 1, 0, new BasicStroke(2), false, color);
        this.text = text;
        this.font = font;
    }

    public Text (float x, float y, String text, float size, Color color) {
        this(x, y, text, DEFAULT.deriveFont(size), color);
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
        //this.stroke = new BasicStroke(font.getSize2D() / 12f);
        this.shape = generateShape(text, font);
    }

    @Override
    public Text clone() {
        return new Text(x, y, text, font, color);
    }

    private static Shape generateShape (String text, Font font) {
        FontRenderContext ctx = new FontRenderContext(new AffineTransform(), true, true);
        TextLayout layout = new TextLayout(text, font, ctx);
        return layout.getOutline(null);
    }
}
