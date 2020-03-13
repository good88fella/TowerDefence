package com.sberstart.aux;

import java.util.*;

public abstract class GameObject {

    protected double x;
    protected double y;
    protected double radius;
    protected int fireRange;
    protected int power;
    protected int maxHealth;
    protected int currentHealth;
    protected double angle;
    protected boolean isAlive;
    protected GameObject target;
    protected boolean isShooting;
    protected int attackSpeed;

    public GameObject(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        isAlive = true;
        isShooting = false;
    }

    protected boolean fireAll(Collection<? extends GameObject> list) {
        if (list.isEmpty())
            return false;
        if (target != null && target.isAlive() &&
                 target.getClass().equals(new ArrayList<>(list).get(0).getClass()) &&
                getDistance(target) <= fireRange) {
            isShooting = true;
        } else {
            GameObject tmpTarget = null;
            double minDistance = Double.MAX_VALUE;
            Iterator<? extends GameObject> itr = list.iterator();
            while (itr.hasNext()) {
                GameObject gameObject = itr.next();
                if (gameObject.isAlive()) {
                    double distance = getDistance(gameObject);
                    if (distance <= fireRange && distance < minDistance) {
                        tmpTarget = gameObject;
                        minDistance = distance;
                    }
                } else {
                    itr.remove();
                }
            }
            target = tmpTarget;
        }
        if (target != null) {
            target.currentHealth -= power;
            angle = Math.atan2(target.y - y, target.x - x) * 180 / Math.PI + 180;
            if (target.currentHealth <= 0) {
                target.setAlive(false);
                return true;
            }
        }
        return false;
    }

    private double getDistance(GameObject gameObject) {
        double dx = x - gameObject.x;
        double dy = y - gameObject.y;
        return Math.sqrt(dx * dx + dy * dy);
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getFireRange() {
        return fireRange;
    }

    public void setFireRange(int fireRange) {
        this.fireRange = fireRange;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public GameObject getTarget() {
        return target;
    }

    public void setTarget(GameObject target) {
        this.target = target;
    }

    public boolean isShooting() {
        return isShooting;
    }

    public void setShooting(boolean shooting) {
        isShooting = shooting;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public abstract void resetAttackSpeed();

    public void decAttackSpeed() {
        this.attackSpeed--;
    }
}
