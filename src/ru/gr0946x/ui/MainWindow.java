package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.Fractal;
import ru.gr0946x.ui.fractals.Mandelbrot;
import ru.gr0946x.ui.painting.FractalPainter;
import ru.gr0946x.ui.painting.Painter;
import ru.gr0946x.ui.fractals.ColorFunction;

import javax.swing.*;
import java.awt.*;

import static java.lang.Math.*;

public class MainWindow extends JFrame {

    private final SelectablePanel mainPanel;
    private final Painter painter;
    private final Fractal mandelbrot;
    private final Converter conv;

    
    private final FractalHistory history = new FractalHistory();

    public MainWindow(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 650));
        mandelbrot = new Mandelbrot();
        conv = new Converter(-2.0, 1.0, -1.0, 1.0);

        saveCurrentState();

        painter = new FractalPainter(mandelbrot, conv, (value)->{
            if (value == 1.0) return Color.BLACK;
            var r = (float)abs(sin(5 * value));
            var g = (float)abs(cos(8 * value) * sin (3 * value));
            var b = (float)abs((sin(7 * value) + cos(15 * value)) / 2f);
            return new Color(r, g, b);
        });
        mainPanel = new SelectablePanel(painter);
        mainPanel.setBackground(Color.WHITE);


        mainPanel.addSelectListener((r)->{
            saveCurrentState();
            var xMin = conv.xScr2Crt(r.x);
            var xMax = conv.xScr2Crt(r.x + r.width);
            var yMin = conv.yScr2Crt(r.y + r.height);
            var yMax = conv.yScr2Crt(r.y);
            conv.setXShape(xMin, xMax);
            conv.setYShape(yMin, yMax);
            mainPanel.repaint();
        });

        mainPanel.addPanListener((dx, dy) -> {
            saveCurrentState();
            PanHelper.translatePixels(conv, dx, dy, painter.getWidth(), painter.getHeight());
            mainPanel.repaint();
        });

        setContent();
    }

    private void saveCurrentState() {
        history.add(new FractaleState(
                conv.getXMin(),
                conv.getXMax(),
                conv.getYMin(),
                conv.getYMax()
        ));
    }

    private void setContent(){
        var gl = new GroupLayout(getContentPane());
        setLayout(gl);
        gl.setVerticalGroup(gl.createSequentialGroup()
                .addGap(8)
                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
        );
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGap(8)
                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                .addGap(8)
        );
    }

    private ColorFunction getColorFunction() {
        return (value) -> {
            if (value == 1.0) return Color.BLACK;
            var r = (float) Math.abs(Math.sin(5 * value));
            var g = (float) Math.abs(Math.cos(8 * value) * Math.sin(3 * value));
            var b = (float) Math.abs((Math.sin(7 * value) + Math.cos(15 * value)) / 2f);
            return new Color(r, g, b);
        };
    }
    @Override
    public void onSaveFrac() {
        FractalSaver.showSaveDialog(this, conv, mandelbrot, getColorFunction());
    }

    @Override
    public void onSaveJpg() {
        FractalSaver.showSaveDialog(this, conv, mandelbrot, getColorFunction());
    }

    @Override
    public void onSavePng() {
        FractalSaver.showSaveDialog(this, conv, mandelbrot, getColorFunction());
    }

    @Override
    public void onOpen() { /* TODO п. 6 */ }

    @Override
    public void onUndo() { /* TODO п. 7 */ }

    @Override
    public void onRedo() { /* TODO п. 7 */ }

    @Override
    public void onReset() { /* TODO */ }

    @Override
    public void onShowJulia() { /* TODO п. 8 */ }

    @Override
    public void onIncreaseIterations() {
        ((Mandelbrot) mandelbrot).setMaxIterations(
                ((Mandelbrot) mandelbrot).getMaxIterations() + 50
        );
        mainPanel.repaint();
    }

    @Override
    public void onDecreaseIterations() {
        ((Mandelbrot) mandelbrot).setMaxIterations(
                ((Mandelbrot) mandelbrot).getMaxIterations() - 50
        );
        mainPanel.repaint();
    }

    @Override
    public void onOpenTour() { /* TODO п. 11* */ }

    @Override
    public void onAbout() {
        JOptionPane.showMessageDialog(this, "Фрактал «Множество Мандельброта»\nГруппа 09-464", "О программе", JOptionPane.INFORMATION_MESSAGE);
    }
}
