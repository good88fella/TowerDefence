package entities;

import utils.GameObject;

import java.util.List;

public class Tower extends GameObject {

    public Tower(double x, double y) {
        super(x, y, 1);
        fireRange = 5;
        power = 5;
        maxHealth = 10;
        currentHealth = maxHealth;
    }

    public void fire(List<Enemy> enemies) {
        super.fireAll(enemies);
    }

    public void upgrade() {

    }
}
