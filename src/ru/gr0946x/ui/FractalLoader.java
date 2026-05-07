package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.Mandelbrot;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
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
        if (!file.getName().toLowerCase().endsWith(".frac")) {
            file = new File(file.getParentFile(), file.getName() + ".frac");
        }

        boolean loaded = loadFromFractal(file, conv, mandelbrot);
        if (!loaded) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Не удалось открыть файл .frac",
                    "Ошибка открытия",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        return loaded;
    }

    private static boolean loadFromFractal(File file, Converter conv, Mandelbrot mandelbrot) {
        try {
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
                int iterations = Integer.parseInt(maxIterations);
                if (iterations > 0) {
                    mandelbrot.setMaxIterations(iterations);
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static double parseDouble(Properties props, String key) {
        return Double.parseDouble(props.getProperty(key));
    }
}
