import entities.Enemy;
import entities.GameMap;
import entities.Tower;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public static Game game;
    private GameMap gameMap;
    private boolean needRedraw;
    private List<Enemy> enemies = new ArrayList<>();

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

    private List<Tower> towers = new ArrayList<>();

    public Game() {
        this.gameMap = new GameMap(50, 25);
        gameMap.fillMap();

        Tower tower1 = new Tower(0, 1);
        tower1.setCurrentHealth(100);
        tower1.setMaxHealth(200);
        tower1.setAngle(0);
        this.towers.add(tower1);
        Tower tower2 = new Tower(10, 10);
        tower2.setMaxHealth(100);
        tower2.setCurrentHealth(60);
        tower2.setAngle(45);
        this.towers.add(tower2);
        Tower tower3 = new Tower(25, 18);
        tower3.setMaxHealth(1000);
        tower3.setCurrentHealth(350);
        tower3.setAngle(210);
        this.towers.add(tower3);

        Enemy enemy = new Enemy(1, 1);
        enemies.add(enemy);
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    private boolean isGameOver = false;

    public void createTower(double x, double y) {

        towers.add(new Tower(x, y));
        setNeedRedraw(true);
    }

    public void createEnemy() {
        enemies.add(new Enemy(gameMap.getStart().getX(), gameMap.getStart().getY()));
    }
}
