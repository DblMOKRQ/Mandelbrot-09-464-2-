package ru.gr0946x.ui;

import ru.gr0946x.ui.painting.Painter;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class SelectablePanel extends PaintPanel{
    private SelectedRect rect = null;
    private Graphics g;
    private Point dragStart = null;

    private final ArrayList<SelectListener> selectHandlers = new ArrayList<>();
    private final ArrayList<PanListener> panHandlers = new ArrayList<>();

    public void addSelectListener(SelectListener listener){
        selectHandlers.add(listener);
    }

    public void removeSelectListener(SelectListener listener){
        selectHandlers.remove(listener);
    }

    public void addPanListener(PanListener listener){panHandlers.add(listener);}
    public void removePanListener(PanListener listener){panHandlers.remove(listener);}

    public SelectablePanel(Painter painter) {
        super(painter);
        g = getGraphics();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //левой - выделяем
                if(SwingUtilities.isLeftMouseButton(e)) {
                    rect = new SelectedRect(e.getX(), e.getY());
                    paintSelectedRect();
                }
                //правой - двигаем
                else if (SwingUtilities.isRightMouseButton(e)) {
                    dragStart = new Point(e.getX(),e.getY());
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)&& rect!=null) {
                    paintSelectedRect();
                    for (var handler : selectHandlers) {
                        handler.onSelect(new Rectangle(
                                        rect.getUpperLeft().x,
                                        rect.getUpperLeft().y,
                                        rect.getWidth(),
                                        rect.getHeight()
                                )
                        );
                    }

                    rect = null;
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    dragStart=null;
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)&& rect != null) {
                    paintSelectedRect();
                    rect.setLastPoint(e.getX(), e.getY());
                    paintSelectedRect();
                } else if (SwingUtilities.isRightMouseButton(e) && dragStart!=null) {
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;

                    if(dx!=0 || dy!=0){
                        for(var handler: panHandlers)
                        {
                            handler.onPan(dx,dy);
                        }
                        dragStart = new Point(e.getX(),e.getY());
                    }

                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                g = getGraphics();
            }
        });
    }

    private void paintSelectedRect(){
        if (g != null){
            g.setXORMode(Color.WHITE);
            g.setColor(Color.BLACK);
            g.drawRect(
                    rect.getUpperLeft().x,
                    rect.getUpperLeft().y,
                    rect.getWidth(),
                    rect.getHeight()
            );
            g.setPaintMode();
        }
    }
}
