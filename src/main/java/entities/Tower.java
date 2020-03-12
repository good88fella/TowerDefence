package entities;

import utils.GameObject;

import java.util.List;

public class Tower extends GameObject {

    public Tower(double x, double y) {
        super(x, y, 1);
        fireRange = 5;
        power = 1;
        maxHealth = 20;
        currentHealth = maxHealth;
    }

    public boolean fire(List<Enemy> enemies) {
        return super.fireAll(enemies);
    }

    public void upgrade() {

    }
}
