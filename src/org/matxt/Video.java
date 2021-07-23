package org.matxt;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.Codec;
import org.jcodec.common.Format;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Rational;
import org.jcodec.scale.AWTUtil;
import org.jml.Mathx.Mathf;
import org.matxt.Action.Action;
import org.matxt.Element.Element;
import org.matxt.Extra.Defaults;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Video {
    public int width, height, framerate;
    public float duration;
    public Color background = Defaults.BACKGROUND;

    private ArrayList<Action> actions;
    private ArrayList<Element> elements;

    public Video(int width, int height, float duration, int framerate) {
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.framerate = framerate;

        this.actions = new ArrayList<>();
        this.elements = new ArrayList<>();
    }

    public void add (Element element) {
        this.elements.add(element);
    }

    public void add (Action action) {
        this.actions.add(action);
    }

    public void render (File file, Format format, Codec codec) throws IOException {
        float[] deltas = new float[actions.size()];
        for (int i=0;i<deltas.length;i++) {
            Action action = actions.get(i);
            deltas[i] = action.to - action.from;
        }

        SequenceEncoder encoder = new SequenceEncoder(NIOUtils.writableChannel(file), Rational.R(framerate, 1), format, codec, (Codec)null);
        int frameCount = (int) (duration * framerate);

        for (int j=0;j<frameCount;j++) {
            float t = (float) j / frameCount;

            for (int i=0;i<actions.size();i++) {
                Action action = actions.get(i);
                if (action.isTimeInsideWindow(t)) {
                    float delta = deltas[i];
                    action.applyFunction((t - action.from) / delta);
                }
            }

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();

            graphics.setBackground(background);
            graphics.clearRect(0, 0, width, height);

            float hw = width / 2f;
            float hh = height / 2f;
            float ar = (float) width / height;

            AffineTransform transf = AffineTransform.getScaleInstance(ar, 1f);
            transf.translate(0, -height + height / ar);

            graphics.setTransform(transf);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (Element element: elements) {
                if (element.isVisible) {
                    graphics.setColor(element.color);
                    element.draw(image, graphics, width, height, hw, hh);
                }
            }

            Picture pic = AWTUtil.fromBufferedImageRGB(image);
            encoder.encodeNativeFrame(pic);
            System.out.println((100f * j / frameCount)+" %");
        }

        encoder.finish();
    }
}
