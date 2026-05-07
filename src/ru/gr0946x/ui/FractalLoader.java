package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.Mandelbrot;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FractalLoader {
    public static boolean showOpenDialog(Component parent, Converter conv, Mandelbrot mandelbrot) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Открыть фрактал");

        FileNameExtensionFilter fracFilter = new FileNameExtensionFilter("Fractal Data (*.frac)", "frac");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(fracFilter);
        chooser.setFileFilter(fracFilter);

        int result = chooser.showOpenDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File file = chooser.getSelectedFile();
        try {
            loadFromFractal(file, conv, mandelbrot);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Не удалось открыть файл: " + file.getName() + "\n" + e.getMessage(),
                    "Ошибка открытия",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    private static void loadFromFractal(File file, Converter conv, Mandelbrot mandelbrot) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        }

        double xMin = parseDouble(props, "xMin");
        double xMax = parseDouble(props, "xMax");
        double yMin = parseDouble(props, "yMin");
        double yMax = parseDouble(props, "yMax");

        conv.setXShape(xMin, xMax);
        conv.setYShape(yMin, yMax);

        String maxIterations = props.getProperty("maxIterations");
        if (maxIterations != null) {
            int iterations = parsePositiveInt(maxIterations, "maxIterations");
            mandelbrot.setMaxIterations(iterations);
        }
    }

    private static double parseDouble(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("В файле отсутствует поле: " + key);
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное значение поля " + key + ": " + value);
        }
    }

    private static int parsePositiveInt(String value, String key) {
        int parsedValue;
        try {
            parsedValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное значение поля " + key + ": " + value);
        }
        if (parsedValue <= 0) {
            throw new IllegalArgumentException("Поле " + key + " должно быть > 0");
        }
        return parsedValue;
    }
}
