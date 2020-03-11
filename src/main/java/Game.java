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
    private List<Enemy> enemies = new ArrayList<>();
    private List<Tower> towers = new ArrayList<>();
    private boolean isGameOver = false;

    public static void main(String[] args) throws InterruptedException {
        Game game = new Game();
        game.gameMap = new GameMap(50, 25);
        game.gameMap.fillMap();
        game.gameMap.setStart(new Rect.Point(0, 1));
        game.gameMap.setFinish(new Rect.Point(49, 1));
        game.createEnemy();
        while (game.enemies.size() > 0) {
            Iterator<Enemy> iter = game.enemies.iterator();
            while (iter.hasNext()) {
                Enemy enemy = iter.next();
                if (enemy.isAlive())
                    enemy.move(game.gameMap);
                else
                    iter.remove();
                Thread.sleep(200);
            }
        }
    }

    public void createTower(double x, double y) {
        towers.add(new Tower(x, y));
    }

    public void createEnemy() {
        enemies.add(new Enemy(gameMap.getStart().getX(), gameMap.getStart().getY()));
    }

}
