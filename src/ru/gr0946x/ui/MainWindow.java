package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.Fractal;
import ru.gr0946x.ui.fractals.Mandelbrot;
import ru.gr0946x.ui.painting.FractalPainter;
import ru.gr0946x.ui.painting.Painter;

import javax.swing.*;
import java.awt.*;

import static java.lang.Math.*;

public class MainWindow extends JFrame implements MainMenu.MenuActionHandler {

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
        painter = new FractalPainter(mandelbrot, conv, (value) -> {
            if (value == 1.0) return Color.BLACK;
            var r = (float) abs(sin(5 * value));
            var g = (float) abs(cos(8 * value) * sin(3 * value));
            var b = (float) abs((sin(7 * value) + cos(15 * value)) / 2f);
            return new Color(r, g, b);
        });

        mainPanel = new SelectablePanel(painter);
        mainPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                double cx = conv.xScr2Crt(e.getX());
                double cy = conv.yScr2Crt(e.getY());

                new JuliaWindow(cx, cy).setVisible(true);
            }
        });
        mainPanel.setBackground(Color.WHITE);
        mainPanel.addSelectListener((r) -> {
            var xMin = conv.xScr2Crt(r.x);
            var xMax = conv.xScr2Crt(r.x + r.width);
            var yMin = conv.yScr2Crt(r.y + r.height);
            var yMax = conv.yScr2Crt(r.y);
            conv.setXShape(xMin, xMax);
            conv.setYShape(yMin, yMax);
            mainPanel.repaint();
        });

        setJMenuBar(new MainMenu(this).createMenuBar());
        setContent();
    }

    private void setContent() {
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

    @Override
    public void onSaveFrac() { /* TODO п. 5а */ }

    @Override
    public void onSaveJpg() { /* TODO п. 5б */ }

    @Override
    public void onSavePng() { /* TODO п. 5в */ }

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
    public void onIncreaseIterations() { /* TODO п. 10 */ }

    @Override
    public void onDecreaseIterations() { /* TODO п. 10 */ }

    @Override
    public void onOpenTour() { /* TODO п. 11* */ }

    @Override
    public void onAbout() {
        JOptionPane.showMessageDialog(this, "Фрактал «Множество Мандельброта»\nГруппа 09-464", "О программе", JOptionPane.INFORMATION_MESSAGE);
    }
}
