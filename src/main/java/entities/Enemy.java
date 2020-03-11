package entities;

import utils.GameObject;

public class Enemy extends GameObject {

    private int speed;

    public Enemy(double x, double y) {
        super(x, y, 1);
    }

    @Override
    public void fire() {

    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    public void move() {
        if (!isAlive) return;

    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

}
