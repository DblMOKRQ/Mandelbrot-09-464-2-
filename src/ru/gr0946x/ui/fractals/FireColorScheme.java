package ru.gr0946x.ui.fractals;

import java.awt.Color;

public class FireColorScheme implements ColorFunction {
    @Override
    public Color getColor(float value) {
        // Если value равно 1.0, значит мы внутри множества Мандельброта
        if (value >= 1.0f) return Color.BLACK;

        // Математика для красивого огненного градиента
        int r = (int) (9.0 * (1 - value) * value * value * value * 255);
        int g = (int) (15.0 * (1 - value) * (1 - value) * value * value * 255);
        int b = (int) (8.5 * (1 - value) * (1 - value) * (1 - value) * value * 255);

        return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, b));
    }
}