package com.sberstart.entities;

import com.sberstart.aux.GameObject;
import com.sberstart.aux.Upgrade;

import java.util.List;
import java.util.Objects;

public class Tower extends GameObject {

    private int fireRangeUpgradeCost;
    private int powerUpgradeCost;
    private int healthUpgradeCost;
    private int fireRangeUpgradeLvl;
    private int powerUpgradeLvl;
    private int healthUpgradeLvl;

    public Tower(double x, double y) {
        super(x, y, 1);
        fireRange = 5;
        power = 1;
        maxHealth = 15;
        currentHealth = maxHealth;
        fireRangeUpgradeCost = 100;
        powerUpgradeCost = 30;
        healthUpgradeCost = 10;
        fireRangeUpgradeLvl = 0;
        powerUpgradeLvl = 0;
        healthUpgradeLvl = 0;
        attackSpeed = 0;
    }


    @Override
    public void resetAttackSpeed() {
        attackSpeed = 20;
    }

    public void upgrade(Upgrade up) {
        switch (up) {
            case ARMOR:
                maxHealth += 15;
                currentHealth += 15;
                healthUpgradeLvl++;
                break;
            case POWER:
                power++;
                powerUpgradeLvl++;
                break;
            case RANGE:
                fireRange++;
                fireRangeUpgradeLvl++;
                break;
        }
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

    public int getFireRangeUpgradeLvl() {
        return fireRangeUpgradeLvl;
    }

    public int getPowerUpgradeLvl() {
        return powerUpgradeLvl;
    }

    public int getHealthUpgradeLvl() {
        return healthUpgradeLvl;
    }
}
