package ru.gr0946x.ui;

import ru.gr0946x.Converter;

//продолжение конвектора
public class PanHelper {

    public static void translate(Converter conv, double dxWorld, double dyWorld) {
        conv.setXShape(conv.getXMin() + dxWorld, conv.getXMax() + dxWorld);
        conv.setYShape(conv.getYMin() + dyWorld, conv.getYMax() + dyWorld);
    }

    public static void translatePixels(Converter conv, int dxPixels, int dyPixels, int screenWidth, int screenHeight) {
        double widthWorld = conv.getXMax() - conv.getXMin();
        double heightWorld = conv.getYMax() - conv.getYMin();
        double dxWorld = (dxPixels * widthWorld) / screenWidth;
        double dyWorld = (dyPixels * heightWorld) / screenHeight;
        translate(conv, -dxWorld, +dyWorld);
    }
}