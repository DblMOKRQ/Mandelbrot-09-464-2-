package ru.gr0946x.ui.fractals;

import java.awt.Color;

public class ZebraColorScheme implements ColorFunction {
    @Override
    public Color getColor(float value) {
        if (value >= 1.0f) return Color.BLACK;

        // Умножаем value на количество "полос" (например, 50)
        int band = (int) (value * 50);

        // Четная полоса - белая, нечетная - черная
        return (band % 2 == 0) ? Color.WHITE : Color.BLACK;
    }
}