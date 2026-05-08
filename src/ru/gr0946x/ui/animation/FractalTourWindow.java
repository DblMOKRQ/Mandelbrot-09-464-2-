package ru.gr0946x.ui.animation;// FractalTour.java
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

// Модель ключевого кадра
class Keyframe {
    double centerX, centerY;
    double zoom;
    int durationMs; // длительность в мс до следующего кадра
    String label;

    public Keyframe(double centerX, double centerY, double zoom, int durationMs, String label) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.zoom = zoom;
        this.durationMs = durationMs;
        this.label = label;
    }
}

// Таблица для редактирования ключевых кадров
class KeyframeTableModel extends AbstractTableModel {
    private List<Keyframe> frames = new ArrayList<>();
    private String[] columns = {"Label", "Center X", "Center Y", "Zoom", "Duration (ms)"};

    public void addFrame(Keyframe kf) {
        frames.add(kf);
        fireTableRowsInserted(frames.size() - 1, frames.size() - 1);
    }

    public void removeFrame(int row) {
        frames.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public List<Keyframe> getFrames() {
        return frames;
    }

    @Override
    public int getRowCount() { return frames.size(); }
    @Override
    public int getColumnCount() { return columns.length; }
    @Override
    public String getColumnName(int col) { return columns[col]; }
    @Override
    public Object getValueAt(int row, int col) {
        Keyframe kf = frames.get(row);
        switch (col) {
            case 0: return kf.label;
            case 1: return kf.centerX;
            case 2: return kf.centerY;
            case 3: return kf.zoom;
            case 4: return kf.durationMs;
            default: return null;
        }
    }
}

// Окно для настройки тура
public class FractalTourWindow extends JFrame {
    private KeyframeTableModel tableModel;
    private JTable table;
    private JTextField labelField, xField, yField, zoomField, durationField;
    private JButton addButton, removeButton, startTourButton;
    private JFileChooser fileChooser;
    private Runnable onStartTour; // callback для запуска анимации с кадрами

    public FractalTourWindow(Runnable startTourCallback) {
        this.onStartTour = startTourCallback;
        setTitle("Fractal Tour Designer");
        setSize(600, 400);
        setLayout(new BorderLayout());

        tableModel = new KeyframeTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Панель ввода
        JPanel inputPanel = new JPanel(new GridLayout(2, 5));
        labelField = new JTextField("Frame");
        xField = new JTextField("-0.5");
        yField = new JTextField("0");
        zoomField = new JTextField("1.0");
        durationField = new JTextField("2000");
        inputPanel.add(new JLabel("Label:")); inputPanel.add(labelField);
        inputPanel.add(new JLabel("Center X:")); inputPanel.add(xField);
        inputPanel.add(new JLabel("Center Y:")); inputPanel.add(yField);
        inputPanel.add(new JLabel("Zoom:")); inputPanel.add(zoomField);
        inputPanel.add(new JLabel("Duration(ms):")); inputPanel.add(durationField);

        add(inputPanel, BorderLayout.NORTH);

        // Кнопки
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Keyframe");
        removeButton = new JButton("Remove Selected");
        startTourButton = new JButton("Start Tour & Save Frames");
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(startTourButton);
        add(buttonPanel, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Tour Frames");

        // Обработчики
        addButton.addActionListener(e -> {
            try {
                double x = Double.parseDouble(xField.getText());
                double y = Double.parseDouble(yField.getText());
                double zoom = Double.parseDouble(zoomField.getText());
                int dur = Integer.parseInt(durationField.getText());
                Keyframe kf = new Keyframe(x, y, zoom, dur, labelField.getText());
                tableModel.addFrame(kf);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number");
            }
        });

        removeButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) tableModel.removeFrame(row);
        });

        startTourButton.addActionListener(e -> {
            if (tableModel.getFrames().size() < 2) {
                JOptionPane.showMessageDialog(this, "Need at least 2 keyframes");
                return;
            }
            // Сохраняем туристические кадры в PNG
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File dir = fileChooser.getSelectedFile();
                if (!dir.exists()) dir.mkdirs();
                generateTourFrames(tableModel.getFrames(), dir);
            }
        });
    }

    private void generateTourFrames(List<Keyframe> frames, File outputDir) {
        // Линейная интерполяция между кадрами
        List<BufferedImage> allFrames = new ArrayList<>();
        for (int i = 0; i < frames.size() - 1; i++) {
            Keyframe from = frames.get(i);
            Keyframe to = frames.get(i + 1);
            int steps = Math.max(1, from.durationMs / 50); // каждый кадр ~50 мс
            for (int step = 0; step <= steps; step++) {
                double t = (double) step / steps;
                double cx = from.centerX + (to.centerX - from.centerX) * t;
                double cy = from.centerY + (to.centerY - from.centerY) * t;
                double zoom = from.zoom + (to.zoom - from.zoom) * t;
                // Здесь вы должны вызвать метод отрисовки фрактала из вашего основного приложения
                // Для примера создадим заглушку (черный квадрат)
                BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = img.createGraphics();
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, 800, 600);
                g.setColor(Color.WHITE);
                g.drawString(String.format("Frame: %d  Center: %.5f,%.5f  Zoom: %.2f", 
                    allFrames.size(), cx, cy, zoom), 10, 20);
                g.dispose();
                allFrames.add(img);
            }
        }
        // Сохраняем кадры
        try {
            for (int idx = 0; idx < allFrames.size(); idx++) {
                ImageIO.write(allFrames.get(idx), "png", new File(outputDir, String.format("frame_%05d.png", idx)));
            }
            JOptionPane.showMessageDialog(this, "Saved " + allFrames.size() + " frames to " + outputDir);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving frames: " + ex.getMessage());
        }
    }

    // Пример запуска окна из главного меню
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FractalTourWindow window = new FractalTourWindow(() -> {});
            window.setVisible(true);
        });
    }
}
