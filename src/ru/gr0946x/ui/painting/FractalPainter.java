package ru.gr0946x.ui.painting;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.ColorFunction;
import ru.gr0946x.ui.fractals.Fractal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FractalPainter implements Painter {

    private final Fractal fractal;
    private final Converter conv;
    private final ColorFunction colorFunction;

    private final ExecutorService executor;
    private final int cores;

    public FractalPainter(Fractal f, Converter conv, ColorFunction cf) {
        this.fractal = f;
        this.conv = conv;
        this.colorFunction = cf;


        this.cores = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(cores);
    }

    @Override
    public int getWidth() {
        return conv.getWidth();
    }

    @Override
    public int getHeight() {
        return conv.getHeight();
    }

    @Override
    public void setWidth(int width) {
        conv.setWidth(width);
    }

    @Override
    public void setHeight(int height) {
        conv.setHeight(height);
    }

    @Override
    public void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        if (w <= 0 || h <= 0) return;


        int chunkWidth = w / cores;
        List<Future<RenderedChunk>> futures = new ArrayList<>();


        for (int i = 0; i < cores; i++) {
            int startX = i * chunkWidth;
            int endX = (i == cores - 1) ? w : startX + chunkWidth;

            futures.add(executor.submit(new RenderTask(startX, endX, h)));
        }


        try {
            for (Future<RenderedChunk> future : futures) {
                RenderedChunk chunk = future.get();
                g.drawImage(chunk.image, chunk.startX, 0, null);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    private static class RenderedChunk {
        BufferedImage image;
        int startX;

        RenderedChunk(BufferedImage image, int startX) {
            this.image = image;
            this.startX = startX;
        }
    }


    private class RenderTask implements Callable<RenderedChunk> {
        private final int startX;
        private final int endX;
        private final int height;

        RenderTask(int startX, int endX, int height) {
            this.startX = startX;
            this.endX = endX;
            this.height = height;
        }

        @Override
        public RenderedChunk call() {
            int currentWidth = endX - startX;

            BufferedImage img = new BufferedImage(currentWidth, height, BufferedImage.TYPE_INT_RGB);

            for (int i = startX; i < endX; i++) {
                for (int j = 0; j < height; j++) {

                    double x = conv.xScr2Crt(i);
                    double y = conv.yScr2Crt(j);
                    double res = fractal.inSetProbability(x, y);
                    Color color = colorFunction.getColor((float) res);
                    img.setRGB(i - startX, j, color.getRGB());
                }
            }
            return new RenderedChunk(img, startX);
        }
    }
}