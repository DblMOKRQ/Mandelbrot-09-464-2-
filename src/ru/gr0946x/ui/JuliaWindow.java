package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.painting.Painter;

import javax.swing.*;
import java.awt.*;

import static java.lang.Math.*;

public class JuliaWindow extends JFrame {

    public JuliaWindow(double cx, double cy) {
        setTitle("Julia set: c = " + cx + " + " + cy + "i");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        var conv = new Converter(-2.0, 2.0, -2.0, 2.0);

        Painter painter = new Painter() {

            @Override
            public int getWidth() {
                return getContentPane().getWidth();
            }

            @Override
            public int getHeight() {
                return getContentPane().getHeight();
            }

            @Override
            public void setWidth(int width) {}

            @Override
            public void setHeight(int height) {}

            @Override
            public void paint(Graphics g) {

                conv.setXShape(-2.0, 2.0);
                conv.setYShape(-2.0, 2.0);

                int width = getContentPane().getWidth();
                int height = getContentPane().getHeight();

                for (int x = 0; x < getWidth(); x++) {
                    for (int y = 0; y < getHeight(); y++) {

                        double zx = conv.xScr2Crt(x);
                        double zy = conv.yScr2Crt(y);

                        int maxIter = 200;
                        int iter = 0;

                        while (zx * zx + zy * zy < 4 && iter < maxIter) {
                            double newX = zx * zx - zy * zy + cx;
                            double newY = 2 * zx * zy + cy;

                            zx = newX;
                            zy = newY;
                            iter++;
                        }

                        double value = iter / (double) maxIter;

                        Color color;
                        if (iter == maxIter) {
                            color = Color.BLACK;
                        } else {
                            float r = (float) abs(sin(5 * value));
                            float gr = (float) abs(cos(8 * value) * sin(3 * value));
                            float b = (float) abs((sin(7 * value) + cos(15 * value)) / 2f);
                            color = new Color(r, gr, b);
                        }

                        g.setColor(color);
                        g.drawLine(x, y, x, y);
                    }
                }
            }
        };

        var panel = new PaintPanel(painter);
        panel.setBackground(Color.WHITE);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
    }
}