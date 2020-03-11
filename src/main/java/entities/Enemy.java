package entities;

import utils.GameObject;

public class Enemy extends GameObject {

    private int speed;
    private

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

        if (direction == MoveDirection.UP)
            move(0, -1);
        else if (direction == MoveDirection.RIGHT)
            move(1, 0);
        else if (direction == MoveDirection.DOWN)
            move(0, 1);
        else if (direction == MoveDirection.LEFT)
            move(-1, 0);
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

}
