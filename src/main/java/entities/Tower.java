package entities;

import utils.GameObject;

import java.util.List;
import java.util.Objects;

public class Tower extends GameObject {

    public Tower(double x, double y) {
        super(x, y, 1);
        fireRange = 5;
        power = 1;
        maxHealth = 10;
        currentHealth = maxHealth;
    }

    public boolean fire(List<Enemy> enemies) {
        return super.fireAll(enemies);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tower that = (Tower) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
