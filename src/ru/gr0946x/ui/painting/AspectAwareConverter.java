package ru.gr0946x.ui.painting;

public interface AspectAwareConverter {
    double getXMin();
    double getXMax();
    double getYMin();
    double getYMax();

    void setXShape(double xMin, double xMax);
    void setYShape(double yMin, double yMax);
}
