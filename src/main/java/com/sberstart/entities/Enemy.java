package com.sberstart.entities;

import com.sberstart.aux.GameObject;
import com.sberstart.aux.Rect;

import java.util.Set;

public class Enemy extends GameObject {

    private Rect.Point prevPoint;
    private double speed;

    public Enemy(double x, double y, int fireRange, int power, int maxHealth) {
        super(x, y, 1);
        this.fireRange = fireRange;
        this.power = power;
        this.maxHealth = maxHealth;
        currentHealth = maxHealth;
        speed = 0.1;
        attackSpeed = 0;
    }

    @Override
    public void resetAttackSpeed() {
        attackSpeed = 15;
    }

    public boolean fire(Set<Tower> towers) {
        return super.fireAll(towers);
    }

    public boolean move(GameMap gameMap) {
/*
        if (prevPoint != null && new Rect.Point(x, y).equals(gameMap.getFinish()) && isAlive) {
            isAlive = false;
            return true;
        }
*/
        Rect.Point nextPoint;
        if (prevPoint == null) {
            nextPoint = gameMap.getStart();
            prevPoint = new Rect.Point(x, y);
        } else {
            nextPoint = getNextPoint(gameMap, x, y);
        }
        if (nextPoint == null) {
            isAlive = false;
            return true;
        }
        prevPoint.setX(x);
        prevPoint.setY(y);
        x = nextPoint.getX();
        y = nextPoint.getY();
        return false;
    }

    private int floorDouble(double val) {
        return (int)(Math.floor(val));
    }

    private int ceilDouble(double val) {
        return (int)(Math.ceil(val));
    }


    private Rect.Point goUp(char[][]matrix, double x, double y) {
        if (prevPoint != null && prevPoint.compareWith(x, y - speed)) {
            return null;
        }
        double roundX = Math.round(x);
        if (y - speed >= 0 - 0.00001 && Math.abs(x - roundX) < 0.00001) {
            if (matrix[floorDouble(y - speed + 0.00001)][(int)roundX] == '#') {
                return new Rect.Point(roundX, y - speed);
            }
        }
        return null;
    }

    private Rect.Point goDown(char[][]matrix, double x, double y) {
        if (prevPoint != null && prevPoint.compareWith(x, y + speed)) {
            return null;
        }
        double roundX = Math.round(x);
        if (y + speed < matrix.length + 0.00001 && Math.abs(x - roundX) < 0.00001) {
            if (matrix[ceilDouble(y + speed - 0.00001)][(int)roundX] == '#') {
                return new Rect.Point(roundX, y + speed);
            }
        }
        return null;
    }

    private Rect.Point goLeft(char[][]matrix, double x, double y) {
        if (prevPoint != null && prevPoint.compareWith(x - speed, y)) {
            return null;
        }
        double roundY = Math.round(y);
        if (x - speed >= 0 - 0.00001 && Math.abs(y - roundY) < 0.00001) {
            if (matrix[(int)roundY][floorDouble(x - speed + 0.00001)] == '#') {
                return new Rect.Point(x - speed, roundY);
            }
        }
        return null;
    }

    private Rect.Point goRight(char[][]matrix, double x, double y) {
        if (prevPoint != null && prevPoint.compareWith(x + speed, y)) {
            return null;
        }
        double roundY = Math.round(y);
        int ceilX = ceilDouble(x + speed - 0.00001);
        if (ceilX < matrix[0].length && Math.abs(y - roundY) < 0.00001) {
            if (matrix[(int)roundY][ceilX] == '#') {
                return new Rect.Point(x + speed, roundY);
            }
        }
        return null;
    }

    private Rect.Point findNextPoint(GameMap gameMap, double x, double y) {
        char[][] matrix = gameMap.getMatrix();
        Rect.Point nextPoint;
        nextPoint = goDown(matrix, x, y);
        if (nextPoint == null) {
            nextPoint = goUp(matrix, x, y);
        }
        if (nextPoint == null) {
            nextPoint = goRight(matrix, x, y);
        }
        if (nextPoint == null) {
            nextPoint = goLeft(matrix, x, y);
        }
        return nextPoint;
    }

    private Rect.Point getNextPoint(GameMap gameMap, double x, double y) {
        /*Rect.Point nextPoint = null;
        if (y != gameMap.getHeight() - 1 && !prevPoint.equals(new Rect.Point(x, y + speed)) &&
                gameMap.getMatrix()[(int) (y + speed)][(int) x] == '#')
            nextPoint = new Rect.Point(x, y + speed);
        else if (y != 0 && !prevPoint.equals(new Rect.Point(x, y - speed))
                && gameMap.getMatrix()[(int) (y - speed)][(int) x] == '#')
            nextPoint = new Rect.Point(x, y - speed);
        else if (x != gameMap.getWidth() - 1 && !prevPoint.equals(new Rect.Point(x + speed, y)) &&
                gameMap.getMatrix()[(int) y][(int) (x + speed)] == '#')
            nextPoint = new Rect.Point(x + speed, y);
        else if (x != 0 && !prevPoint.equals(new Rect.Point(x - speed, y)) &&
                gameMap.getMatrix()[(int) y][(int) (x - speed)] == '#')
            nextPoint = new Rect.Point(x - speed, y);
        return nextPoint;*/
        return findNextPoint(gameMap, x, y);
    }

    public Rect.Point getPrevPoint() {
        return prevPoint;
    }

    public void setPrevPoint(Rect.Point prevPoint) {
        this.prevPoint = prevPoint;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
