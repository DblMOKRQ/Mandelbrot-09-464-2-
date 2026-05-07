package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.ColorFunction;
import ru.gr0946x.ui.fractals.DefaultColorScheme;
import ru.gr0946x.ui.painting.FractalPainter;

import javax.swing.*;
import java.awt.*;

public class JuliaWindow extends JFrame {

    public JuliaWindow(double cx, double cy) {
        setTitle("Множество Жюлиа: c = " + cx + " + " + cy + "i");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        var conv = new Converter(-2.0, 2.0, -2.0, 2.0);

        // Для Жюлиа пока ставим стандартный цвет
        ColorFunction defaultColor = new DefaultColorScheme();

        // ПУНКТ 9: Передаем функцию вычисления Жюлиа через лямбда-выражение!
        FractalPainter painter = new FractalPainter((x, y) -> {
            int maxIter = 200;
            int iter = 0;
            double zx = x;
            double zy = y;

            while (zx * zx + zy * zy < 4 && iter < maxIter) {
                double newX = zx * zx - zy * zy + cx;
                double newY = 2 * zx * zy + cy;
                zx = newX;
                zy = newY;
                iter++;
            }
            return (float) iter / maxIter;
        }, conv, defaultColor);

        var panel = new PaintPanel(painter);
        panel.setBackground(Color.WHITE);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
    }
}