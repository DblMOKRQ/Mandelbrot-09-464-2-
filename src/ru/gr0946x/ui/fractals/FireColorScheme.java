package ru.gr0946x.ui.fractals;

import java.awt.Color;

public class FireColorScheme implements ColorFunction {
    @Override
    public Color getColor(float value) {
        if (value >= 1.0f) return Color.BLACK;

        // Настоящий огненный градиент
        int r = (int) Math.min(255, value * 3 * 255);
        int g = (int) Math.min(255, Math.max(0, (value - 0.33f) * 3 * 255));
        int b = (int) Math.min(255, Math.max(0, (value - 0.66f) * 3 * 255));

        return new Color(r, g, b);
    }
}