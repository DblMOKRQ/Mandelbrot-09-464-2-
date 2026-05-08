package ru.gr0946x.ui;

import ru.gr0946x.Converter;
import ru.gr0946x.ui.painting.FractalPainter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TourWindow extends JFrame {
    private final List<FractaleState> tourPoints = new ArrayList<>();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final MainWindow mainApp;
    private final VideoTourExporter exporter;

    public TourWindow(MainWindow mainApp, FractalPainter painter, Converter conv) {
        this.mainApp = mainApp;
        this.exporter = new VideoTourExporter(painter, conv);

        setTitle("Настройка видео-экскурсии");
        setSize(450, 350);
        setLayout(new BorderLayout());
        setLocationRelativeTo(mainApp);
        JList<String> list = new JList<>(listModel);
        add(new JScrollPane(list), BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout());

        JButton addBtn = new JButton("Добавить кадр");
        JButton clearBtn = new JButton("Очистить");
        JButton saveBtn = new JButton("Сохранить видео (.mp4)");
        saveBtn.setBackground(new Color(180, 240, 180));
        addBtn.addActionListener(e -> {
            tourPoints.add(mainApp.captureCurrentState());
            listModel.addElement("Точка " + tourPoints.size() + " [Zoom зафиксирован]");
        });
        clearBtn.addActionListener(e -> {
            tourPoints.clear();
            listModel.clear();
        });
        saveBtn.addActionListener(e -> {
            if (tourPoints.size() < 2) {
                JOptionPane.showMessageDialog(this, "Нужно добавить хотя бы 2 кадра для создания перехода!");
                return;
            }
            startVideoExport();
        });

        btnPanel.add(addBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(saveBtn);

        add(btnPanel, BorderLayout.SOUTH);
    }
    private void startVideoExport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Выберите место для сохранения видео");
        chooser.setSelectedFile(new File("fractal_tour.mp4"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if (!file.getName().toLowerCase().endsWith(".mp4")) {
                file = new File(file.getAbsolutePath() + ".mp4");
            }

            File finalFile = file;
            new Thread(() -> {
                try {
                    this.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "Начинается рендеринг видео. Это может занять пару минут.");

                    exporter.createVideo(tourPoints, finalFile, 50);

                    JOptionPane.showMessageDialog(this, "Видео успешно сохранено: " + finalFile.getName());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при создании видео: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    this.setEnabled(true);
                }
            }).start();
        }
    }
}