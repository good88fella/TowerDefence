import entities.Enemy;
import entities.GameMap;
import entities.Tower;
import utils.Rect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Game {

    public static Game game;
    private GameMap gameMap;
    private boolean needRedraw;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Tower> towers = new ArrayList<>();
    private boolean isGameOver = false;
    public static int enemyFinished = 0;

    public Game(){
        this.gameMap = new GameMap(50, 25);
        this.gameMap.fillMap();
        this.gameMap.setStart(new Rect.Point(0, 1));
        this.gameMap.setFinish(new Rect.Point(49, 1));
        this.createEnemy();
    }

    public void runGame() throws InterruptedException {
        while (this.enemies.size() > 0) {
            Iterator<Tower> iterTowers = this.towers.iterator();
            while (iterTowers.hasNext()) {
                Tower tower = iterTowers.next();
                if (tower.isAlive()) {
                    tower.fire(enemies);
                } else {
                    iterTowers.remove();
                }
                needRedraw = true;
            }

            Iterator<Enemy> iterEnemies = this.enemies.iterator();
            while (iterEnemies.hasNext()) {
                Enemy enemy = iterEnemies.next();
                if (enemy.isAlive()) {
                    enemy.fire(towers);
                    if (enemy.move(this.gameMap))
                        enemyFinished++;
                } else {
                    iterEnemies.remove();
                }
                needRedraw = true;
            }
            Thread.sleep(200);
        }
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void createTower(double x, double y) {
        towers.add(new Tower(x, y));
        setNeedRedraw(true);
    }

    public void createEnemy() {
        enemies.add(new Enemy(gameMap.getStart().getX(), gameMap.getStart().getY()));
    }
    public boolean isNeedRedraw() {
        return needRedraw;
    }

    public void setNeedRedraw(boolean needRedraw) {
        this.needRedraw = needRedraw;
    }

    public List<Tower> getTowers() {
        return towers;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

}
