package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.fractals.ColorFunction;
import ru.gr0946x.ui.fractals.Fractal;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.awt.Component;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.Properties;


public class FractalSaver {
    public static File showSaveDialog(Component parent){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Сохранить фрактал");

        chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPG (*.jpg)", "jpg"));
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Fractal Data (*.frac)", "frac"));
        chooser.setAcceptAllFileFilterUsed(false);

        chooser.setSelectedFile(new File("fractal"));

        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION){
            return null;
        }

        return chooser.getSelectedFile();
    }

    private static File addExtension(File file, String ext) {
        String name = file.getName();
        //если сохранили файл с параметром сохранения
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0){
            String currentExt = name.substring(lastDot + 1).toLowerCase();
            if (currentExt.equals(ext)){
                return file;
            }
        }

        //если нет параметра сохранения или он неправильный - добавляем
        return new File(file.getParentFile(), name + '.' + ext);

    }

    public static boolean SaveAsImage(File file, Converter conv, Fractal fractal, ColorFunction colorFunc) {
        try {
            int h = conv.getHeight();
            int w = conv.getWidth();
            if (h <= 0 || w <= 0)
                return false;

            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            //рисование каждого пикселя
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    double cx = conv.xScr2Crt(x);
                    double cy = conv.yScr2Crt(y);
                    float value = fractal.inSetProbability(cx, cy);
                    img.setRGB(x, y, colorFunc.getColor(value).getRGB());

                }
            }

            //подписи
            Graphics2D g2d = img.createGraphics();
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
            g2d.setColor(Color.WHITE);
            String coords = String.format("X:[%.4f;%.4f] Y:[%.4f;%.4f]", conv.xScr2Crt(0), conv.xScr2Crt(w), conv.yScr2Crt(h), conv.yScr2Crt(0));
            g2d.drawString(coords, 5, h - 5);
            g2d.dispose();

            //определение формата
            String name = file.getName().toLowerCase();
            String format = name.endsWith(".png") ? "png" : "jpg";

            return ImageIO.write(img, format, file);
        }

        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //сохранение в формате для восстановления
    public static boolean saveAsFractal(File file, Converter conv) {
        try {
            Properties props = new Properties();
            props.setProperty("xMin", String.valueOf(conv.xScr2Crt(0)));
            props.setProperty("xMax", String.valueOf(conv.xScr2Crt(conv.getWidth())));
            props.setProperty("yMin", String.valueOf(conv.yScr2Crt(conv.getHeight())));
            props.setProperty("yMax", String.valueOf(conv.yScr2Crt(0)));
            props.setProperty("width", String.valueOf(conv.getWidth()));
            props.setProperty("height", String.valueOf(conv.getHeight()));

            try (FileOutputStream fos = new FileOutputStream(file)) {
                props.store(fos, "Fractal data - do not edit manualy");
            }
            return true;
        }

        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
