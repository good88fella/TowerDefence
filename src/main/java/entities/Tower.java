package entities;

import utils.GameObject;

import java.util.List;
import java.util.Objects;

public class Tower extends GameObject {

    private int fireRangeUpgradeCost;
    private int powerUpgradeCost;
    private int healthUpgradeCost;

    public Tower(double x, double y) {
        super(x, y, 1);
        fireRange = 5;
        power = 1;
        maxHealth = 10;
        currentHealth = maxHealth;
        fireRangeUpgradeCost = 100;
        powerUpgradeCost = 30;
        healthUpgradeCost = 10;
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

    public int getFireRangeUpgradeCost() {
        return fireRangeUpgradeCost;
    }

    public void setFireRangeUpgradeCost(int fireRangeUpgradeCost) {
        this.fireRangeUpgradeCost = fireRangeUpgradeCost;
    }

    public int getPowerUpgradeCost() {
        return powerUpgradeCost;
    }

    public void setPowerUpgradeCost(int powerUpgradeCost) {
        this.powerUpgradeCost = powerUpgradeCost;
    }

    public int getHealthUpgradeCost() {
        return healthUpgradeCost;
    }

    public void setHealthUpgradeCost(int healthUpgradeCost) {
        this.healthUpgradeCost = healthUpgradeCost;
    }
}
