package net.aubrecht.mandelbrot.picture.service;

/**
 *
 * @author aubi
 */
public class MandelbrotPointArgument {

    private double result;
    private double x;
    private double y;
    private int iterations;
    private int bailout;

    public MandelbrotPointArgument() {
    }

    public MandelbrotPointArgument(double result, double x, double y, int iterations, int bailout) {
        this.result = result;
        this.x = x;
        this.y = y;
        this.iterations = iterations;
        this.bailout = bailout;
    }

    @Override
    public String toString() {
        return "MandelbrotPointArgument{" + "result=" + result + ", x=" + x + ", y=" + y + ", iterations=" + iterations + ", bailout=" + bailout + '}';
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the iterations
     */
    public int getIterations() {
        return iterations;
    }

    /**
     * @param iterations the iterations to set
     */
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    /**
     * @return the bailout
     */
    public int getBailout() {
        return bailout;
    }

    /**
     * @param bailout the bailout to set
     */
    public void setBailout(int bailout) {
        this.bailout = bailout;
    }
}
