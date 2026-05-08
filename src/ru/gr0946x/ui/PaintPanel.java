package ru.gr0946x.ui;

import ru.gr0946x.ui.painting.AspectAwareConverter;
import ru.gr0946x.ui.painting.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class PaintPanel extends JPanel {

    private Painter painter;
    public PaintPanel(Painter painter){
        this.painter = painter;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                int newW = getWidth();
                int newH = getHeight();

                int oldW = painter.getWidth();
                int oldH = painter.getHeight();

                if (oldW > 0 && oldH > 0 && newW > 0 && newH > 0) {
                    adjustAspectRatio(painter, oldW, oldH, newW, newH);
                }

                painter.setWidth(newW);
                painter.setHeight(newH);
                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        painter.paint(g);
    }


    private void adjustAspectRatio(Painter painter,
                                   int oldW, int oldH,
                                   int newW, int newH) {
        if (!(painter instanceof AspectAwareConverter aac)) return;

        double xMin = aac.getXMin();
        double xMax = aac.getXMax();
        double yMin = aac.getYMin();
        double yMax = aac.getYMax();

        double mathW = xMax - xMin;
        double mathH = yMax - yMin;

        double scaleX = mathW / oldW;
        double scaleY = mathH / oldH;

        double scale = Math.max(scaleX, scaleY);

        double newMathW = scale * newW;
        double newMathH = scale * newH;

        double cx = (xMin + xMax) / 2.0;
        double cy = (yMin + yMax) / 2.0;

        aac.setXShape(cx - newMathW / 2.0, cx + newMathW / 2.0);
        aac.setYShape(cy - newMathH / 2.0, cy + newMathH / 2.0);
    }

}
