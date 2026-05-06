package ru.gr0946x.ui;
//текущее состояние фрактала
public class FractaleState {
    public final double xMin, xMax, yMin, yMax;

    public FractaleState(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }
}