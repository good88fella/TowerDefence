package utils;

import java.util.Iterator;
import java.util.List;

public abstract class GameObject {

    protected double x;
    protected double y;
    protected double radius;
    protected double fireRange;
    protected double power;
    protected double maxHealth;
    protected double currentHealth;
    protected double angle;
    protected boolean isAlive;
    protected GameObject target;
    protected boolean isShooting;

    public GameObject(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        isAlive = true;
        isShooting = false;
    }

    public abstract void upgrade();

    protected boolean fireAll(List<? extends GameObject> list) {
        if (list.isEmpty())
            return false;
        if (target != null && target.isAlive() &&
                 target.getClass().equals(list.get(0).getClass()) && getDistance(target) <= fireRange) {
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

    public double getFireRange() {
        return fireRange;
    }

    public void setFireRange(double fireRange) {
        this.fireRange = fireRange;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = maxHealth;
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(double currentHealth) {
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
}
