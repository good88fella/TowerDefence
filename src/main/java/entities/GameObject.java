package entities;

public class GameObject {

    private double x;
    private double y;

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void fire() {

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
