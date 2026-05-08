package ru.gr0946x.ui.painting;

import ru.gr0946x.ui.fractals.Mandelbrot;
import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.ColorFunction;
import ru.gr0946x.ui.fractals.Fractal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class FractalPainter implements Painter, AspectAwareConverter {

    private Fractal fractal;
    private final Converter conv;
    private ColorFunction colorFunction;

    private final ExecutorService executor;
    private final int cores;
    private int manualOffset = 0;
    private Mandelbrot mandelbrotRef = null;

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
    public double getXMin() { return conv.getXMin(); }

    @Override
    public double getXMax() { return conv.getXMax(); }

    @Override
    public double getYMin() { return conv.getYMin(); }

    @Override
    public double getYMax() { return conv.getYMax(); }

    @Override
    public void setXShape(double xMin, double xMax) {
        conv.setXShape(xMin, xMax);
    }

    @Override
    public void setYShape(double yMin, double yMax) {
        conv.setYShape(yMin, yMax);
    }

    private void updateIterations() {
        if (mandelbrotRef == null) return;
        double currentRange = conv.getXMax() - conv.getXMin();
        final double INITIAL_RANGE = 3.0;
        double zoomFactor = INITIAL_RANGE / currentRange;
        int dynamicIter = (int)(100.0 * (1.0 + Math.log10(Math.max(1.0, zoomFactor))));
        dynamicIter = Math.min(Math.max(dynamicIter + manualOffset, 50), 2000);
        mandelbrotRef.setMaxIterations(dynamicIter);
    }

    public void setFractal(Fractal fractal) {
        this.fractal = fractal;
    }

    public void setColorFunction(ColorFunction colorFunction) {
        this.colorFunction = colorFunction;
    }
    public void setMandelbrotRef(Mandelbrot m) {
        this.mandelbrotRef = m;
    }

    public void adjustIterationsOffset(int delta) {
        manualOffset += delta;
    }

    @Override
    public void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        if (w <= 0 || h <= 0) return;
        updateIterations();


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