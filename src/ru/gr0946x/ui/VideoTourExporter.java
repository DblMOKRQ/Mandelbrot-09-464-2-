package ru.gr0946x.ui;

import org.jcodec.api.awt.AWTSequenceEncoder;
import ru.gr0946x.Converter;
import ru.gr0946x.ui.painting.FractalPainter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class VideoTourExporter {

    private final FractalPainter painter;
    private final Converter converter;

    public VideoTourExporter(FractalPainter painter, Converter converter) {
        this.painter = painter;
        this.converter = converter;
    }

    public void createVideo(List<FractaleState> keyFrames, File outputFile, int framesPerTransition) throws IOException {
        int videoWidth = 1280;
        int videoHeight = 720;

        int oldW = converter.getWidth();
        int oldH = converter.getHeight();
        double oldXMin = converter.getXMin();
        double oldXMax = converter.getXMax();
        double oldYMin = converter.getYMin();
        double oldYMax = converter.getYMax();

        converter.setWidth(videoWidth);
        converter.setHeight(videoHeight);

        AWTSequenceEncoder encoder = AWTSequenceEncoder.createSequenceEncoder(outputFile, 25);

        for (int i = 0; i < keyFrames.size() - 1; i++) {
            FractaleState start = keyFrames.get(i);
            FractaleState end = keyFrames.get(i + 1);

            for (int t = 0; t < framesPerTransition; t++) {
                double progress = (double) t / framesPerTransition;

                double xMin = start.xMin + (end.xMin - start.xMin) * progress;
                double xMax = start.xMax + (end.xMax - start.xMax) * progress;
                double yMin = start.yMin + (end.yMin - start.yMin) * progress;
                double yMax = start.yMax + (end.yMax - start.yMax) * progress;

                converter.setXShape(xMin, xMax);
                converter.setYShape(yMin, yMax);

                BufferedImage frame = new BufferedImage(videoWidth, videoHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = frame.createGraphics();
                painter.paint(g2d);
                g2d.dispose();

                encoder.encodeImage(frame);
            }
        }

        encoder.finish();

        converter.setWidth(oldW);
        converter.setHeight(oldH);
        converter.setXShape(oldXMin, oldXMax);
        converter.setYShape(oldYMin, oldYMax);
    }
}