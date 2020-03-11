package entities;

import utils.GameObject;
import utils.Rect;

public class Enemy extends GameObject {

    private int speed;
    private Rect.Point prevPoint;

    public Enemy(double x, double y) {
        super(x, y, 1);
    }

    @Override
    public void fire() {

    }

    public void move(GameMap gameMap) {
        if (prevPoint != null && new Rect.Point(x, y).equals(gameMap.getFinish()) && isAlive) {
            isAlive = false;
            return;
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Rect.Point getPrevPoint() {
        return prevPoint;
    }

    public void setPrevPoint(Rect.Point prevPoint) {
        this.prevPoint = prevPoint;
    }
}
