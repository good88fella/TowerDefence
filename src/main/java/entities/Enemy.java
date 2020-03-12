package entities;

import utils.GameObject;
import utils.Rect;

import java.util.List;

public class Enemy extends GameObject {

    private Rect.Point prevPoint;

    public Enemy(double x, double y) {
        super(x, y, 1);
        fireRange = 3;
        power = 1;
        maxHealth = 10;
        currentHealth = maxHealth;
    }

    public boolean fire(List<Tower> towers) {
        return super.fireAll(towers);
    }

    @Override
    public void upgrade() {
        power++;
        maxHealth += 10;
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
        if (y != gameMap.getHeight() - 1 && !prevPoint.equals(new Rect.Point(x, y + 1)) &&
                gameMap.getMatrix()[(int) (y + 1)][(int) x] == '#')
            nextPoint = new Rect.Point(x, y + 1);
        else if (y != 0 && !prevPoint.equals(new Rect.Point(x, y -1))
                && gameMap.getMatrix()[(int) (y - 1)][(int) x] == '#')
            nextPoint = new Rect.Point(x, y - 1);
        else if (x != gameMap.getWidth() - 1 && !prevPoint.equals(new Rect.Point(x + 1, y)) &&
                gameMap.getMatrix()[(int) y][(int) (x + 1)] == '#')
            nextPoint = new Rect.Point(x + 1, y);
        else if (x != 0 && !prevPoint.equals(new Rect.Point(x - 1, y)) &&
                gameMap.getMatrix()[(int) y][(int) (x - 1)] == '#')
            nextPoint = new Rect.Point(x - 1, y);
        return nextPoint;
    }

    public Rect.Point getPrevPoint() {
        return prevPoint;
    }

    public void setPrevPoint(Rect.Point prevPoint) {
        this.prevPoint = prevPoint;
    }
}
