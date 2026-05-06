package ru.gr0946x.ui;
import java.util.LinkedList;
//история состояния фрактала
public class FractalHistory {

    private final LinkedList<FractaleState> states = new LinkedList<>();
    private final int maxSteps = 100;

    //больше ли одного состояния
    public boolean canUndo() {
        return states.size() > 1;
    }

    //добавляем состояние
    public void add(FractaleState state) {
        if (states.size() >= maxSteps) {
            states.removeFirst(); 
        }
        states.addLast(state);
    }

    //возвращаем текушее состояние если оно есть
    public FractaleState undo() {
        if (states.size() > 1) {
            states.removeLast(); 
            return states.getLast(); 
        }
        return null;
    }

}