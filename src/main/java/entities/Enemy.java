package entities;

import utils.GameObject;
import utils.Rect;

import java.util.List;
import java.util.Set;

public class Enemy extends GameObject {

    private Rect.Point prevPoint;
    private double speed;

    public Enemy(double x, double y) {
        super(x, y, 1);
        fireRange = 3;
        power = 1;
        maxHealth = 10;
        currentHealth = maxHealth;
        speed = 1;
    }

    public boolean fire(Set<Tower> towers) {
        return super.fireAll(towers);
    }

    public boolean move(GameMap gameMap) {
        if (prevPoint != null && new Rect.Point(x, y).equals(gameMap.getFinish()) && isAlive) {
            isAlive = false;
            return true;
        }

        Rect.Point nextPoint;
        if (prevPoint == null) {
            nextPoint = gameMap.getStart();
            prevPoint = new Rect.Point(x, y);
        } else {
            nextPoint = getNextPoint(gameMap, x, y);
        }
        prevPoint.setX(x);
        prevPoint.setY(y);
        x = nextPoint.getX();
        y = nextPoint.getY();
        return false;
    }

    private Rect.Point getNextPoint(GameMap gameMap, double x, double y) {
        Rect.Point nextPoint = null;
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
        return nextPoint;
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
