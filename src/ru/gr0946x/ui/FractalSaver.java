package ru.gr0946x.ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.awt.Component;

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

    public static boolean SaveAsImage
}
