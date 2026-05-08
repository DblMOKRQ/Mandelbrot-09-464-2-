package ru.gr0946x.ui.fractals;

import java.awt.Color;

public class DefaultColorScheme implements ColorFunction {
    @Override
    public Color getColor(float value) {
        if (value == 1.0f) return Color.BLACK;
        float r = (float) Math.abs(Math.sin(5 * value));
        float g = (float) Math.abs(Math.cos(8 * value) * Math.sin(3 * value));
        float b = (float) Math.abs((Math.sin(7 * value) + Math.cos(15 * value)) / 2f);
        return new Color(r, g, b);
    }
}