import entities.Enemy;
import entities.GameMap;
import entities.Tower;
import jdk.internal.org.objectweb.asm.tree.analysis.Analyzer;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public static Game game;
    private GameMap gameMap;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Tower> towers = new ArrayList<>();
    private boolean isGameOver = false;

    public static void main(String[] args) {

    }

    public void createTower(double x, double y) {
        towers.add(new Tower(x, y));
    }

    public void createEnemy() {
        enemies.add(new Enemy(gameMap.getStart().getX(), gameMap.getStart().getY()));
    }
}