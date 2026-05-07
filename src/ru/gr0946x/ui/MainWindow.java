package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.Mandelbrot;
import ru.gr0946x.ui.painting.FractalPainter;
import ru.gr0946x.ui.painting.Painter;
import ru.gr0946x.ui.fractals.ColorFunction;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

import static java.lang.Math.*;

public class MainWindow extends JFrame implements MainMenu.MenuActionHandler {

    private final SelectablePanel mainPanel;
    private final Painter painter;
    private final Mandelbrot mandelbrot;
    private final Converter conv;

    private final FractalHistory history = new FractalHistory();
    private final LinkedList<FractaleState> redoStates = new LinkedList<>();

    public MainWindow(){
        setTitle("Множество Мандельброта");
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
        mainPanel.addSelectListener((r) -> {
            Rectangle corrected = adjustRectToAspect(r,
                    mainPanel.getWidth(), mainPanel.getHeight());

            saveCurrentState();
            var xMin = conv.xScr2Crt(corrected.x);
            var xMax = conv.xScr2Crt(corrected.x + corrected.width);
            var yMin = conv.yScr2Crt(corrected.y + corrected.height);
            var yMax = conv.yScr2Crt(corrected.y);
            conv.setXShape(xMin, xMax);
            conv.setYShape(yMin, yMax);
            mainPanel.repaint();
        });

        mainPanel.addPanListener((dx, dy) -> {
            saveCurrentState();
            PanHelper.translatePixels(conv, dx, dy, painter.getWidth(), painter.getHeight());
            mainPanel.repaint();
        });

        setJMenuBar(new MainMenu(this).createMenuBar());
        setContent();
    }

    private void saveCurrentState() {
        history.add(captureCurrentState());
        redoStates.clear();
    }

    private FractaleState captureCurrentState() {
        return new FractaleState(
                conv.getXMin(),
                conv.getXMax(),
                conv.getYMin(),
                conv.getYMax()
        );
    }

    private void applyState(FractaleState state) {
        if (state == null) {
            return;
        }
        conv.setXShape(state.xMin, state.xMax);
        conv.setYShape(state.yMin, state.yMax);
    }

    private void setContent(){
        var gl = new GroupLayout(getContentPane());
        getContentPane().setLayout(gl);
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
    public void onOpen() {
        FractaleState previousState = new FractaleState(
                conv.getXMin(),
                conv.getXMax(),
                conv.getYMin(),
                conv.getYMax()
        );

        boolean loaded = FractalLoader.showOpenDialog(this, conv, mandelbrot);
        if (loaded) {
            history.add(previousState);
            mainPanel.repaint();
        }
    }

    @Override
    public void onUndo() {
        if (!history.canUndo()) {
            return;
        }
        redoStates.add(captureCurrentState());
        applyState(history.undo());
        mainPanel.repaint();
    }

    @Override
    public void onRedo() {
        if (redoStates.isEmpty()) {
            return;
        }
        history.add(captureCurrentState());
        applyState(redoStates.removeLast());
        mainPanel.repaint();
    }

    @Override
    public void onReset() {
        saveCurrentState();
        conv.setXShape(-2.0, 1.0);
        conv.setYShape(-1.0, 1.0);
        mainPanel.repaint();
    }

    @Override
    public void onShowJulia() {
        double cx = (conv.getXMin() + conv.getXMax()) / 2.0;
        double cy = (conv.getYMin() + conv.getYMax()) / 2.0;
        var juliaWindow = new JuliaWindow(cx, cy);
        juliaWindow.setVisible(true);
    }

    @Override
    public void onIncreaseIterations() {
        ((FractalPainter) painter).adjustIterationsOffset(50);
        mainPanel.repaint();
    }

    @Override
    public void onDecreaseIterations() {
        ((FractalPainter) painter).adjustIterationsOffset(-50);
        mainPanel.repaint();
    }

    @Override
    public void onOpenTour() { /* TODO п. 11* */ }

    @Override
    public void onAbout() {
        JOptionPane.showMessageDialog(this, "Фрактал «Множество Мандельброта»\nГруппа 09-464", "О программе", JOptionPane.INFORMATION_MESSAGE);
    }

    private static Rectangle adjustRectToAspect(Rectangle sel,
                                                int panelW, int panelH) {
        if (sel.width <= 0 || sel.height <= 0 || panelW <= 0 || panelH <= 0)
            return sel;

        double targetRatio = (double) panelW / panelH;
        double selRatio    = (double) sel.width / sel.height;

        int newW, newH;
        if (selRatio < targetRatio) {
            newH = sel.height;
            newW = (int) Math.round(newH * targetRatio);
        } else {
            newW = sel.width;
            newH = (int) Math.round(newW / targetRatio);
        }

        int cx = sel.x + sel.width  / 2;
        int cy = sel.y + sel.height / 2;

        int x = cx - newW / 2;
        int y = cy - newH / 2;

        x = Math.clamp(x, 0, panelW - newW);
        y = Math.clamp(y, 0, panelH - newH);

        return new Rectangle(x, y, newW, newH);
    }
}
