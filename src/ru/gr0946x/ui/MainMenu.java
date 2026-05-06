package ru.gr0946x.ui;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MainMenu {

    public interface MenuActionHandler {
        void onSaveFrac();
        void onSaveJpg();
        void onSavePng();
        void onOpen();
        void onUndo();
        void onRedo();
        void onReset();
        void onShowJulia();
        void onIncreaseIterations();
        void onDecreaseIterations();
        void onOpenTour();
        void onAbout();
    }

    private final MenuActionHandler handler;

    public MainMenu(MenuActionHandler handler) {
        this.handler = handler;
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createAnimationMenu());
        menuBar.add(createHelpMenu());
        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu menu = new JMenu("Файл");
        menu.setMnemonic(KeyEvent.VK_F);

        JMenu saveMenu = new JMenu("Сохранить");

        JMenuItem saveFrac = new JMenuItem("В формате .frac");
        saveFrac.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveFrac.addActionListener(e -> handler.onSaveFrac());

        JMenuItem saveJpg = new JMenuItem("В формате JPEG (.jpg)");
        saveJpg.addActionListener(e -> handler.onSaveJpg());

        JMenuItem savePng = new JMenuItem("В формате PNG (.png)");
        savePng.addActionListener(e -> handler.onSavePng());

        saveMenu.add(saveFrac);
        saveMenu.add(saveJpg);
        saveMenu.add(savePng);

        JMenuItem open = new JMenuItem("Открыть .frac…");
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        open.addActionListener(e -> handler.onOpen());

        menu.add(saveMenu);
        menu.add(open);
        menu.addSeparator();

        JMenuItem exit = new JMenuItem("Выход");
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        exit.addActionListener(e -> System.exit(0));
        menu.add(exit);

        return menu;
    }

    private JMenu createEditMenu() {
        JMenu menu = new JMenu("Правка");
        menu.setMnemonic(KeyEvent.VK_E);

        JMenuItem undo = new JMenuItem("Отменить");
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undo.addActionListener(e -> handler.onUndo());

        JMenuItem redo = new JMenuItem("Повторить");
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        redo.addActionListener(e -> handler.onRedo());

        JMenuItem reset = new JMenuItem("Сбросить вид");
        reset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        reset.addActionListener(e -> handler.onReset());

        menu.add(undo);
        menu.add(redo);
        menu.addSeparator();
        menu.add(reset);

        return menu;
    }

    private JMenu createViewMenu() {
        JMenu menu = new JMenu("Вид");
        menu.setMnemonic(KeyEvent.VK_V);

        JMenuItem julia = new JMenuItem("Показать множество Жюлиа…");
        julia.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, InputEvent.CTRL_DOWN_MASK));
        julia.addActionListener(e -> handler.onShowJulia());

        JMenu iterMenu = new JMenu("Итерации");

        JMenuItem incIter = new JMenuItem("Увеличить");
        incIter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_DOWN_MASK));
        incIter.addActionListener(e -> handler.onIncreaseIterations());

        JMenuItem decIter = new JMenuItem("Уменьшить");
        decIter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));
        decIter.addActionListener(e -> handler.onDecreaseIterations());

        iterMenu.add(incIter);
        iterMenu.add(decIter);

        menu.add(julia);
        menu.addSeparator();
        menu.add(iterMenu);

        return menu;
    }

    private JMenu createAnimationMenu() {
        JMenu menu = new JMenu("Анимация");
        menu.setMnemonic(KeyEvent.VK_A);

        JMenuItem tour = new JMenuItem("Экскурсия по фракталу…");
        tour.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
        tour.addActionListener(e -> handler.onOpenTour());
        menu.add(tour);

        return menu;
    }

    private JMenu createHelpMenu() {
        JMenu menu = new JMenu("Справка");
        menu.setMnemonic(KeyEvent.VK_H);

        JMenuItem about = new JMenuItem("О программе…");
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        about.addActionListener(e -> handler.onAbout());
        menu.add(about);

        return menu;
    }
}
