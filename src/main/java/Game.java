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
    private boolean headerRedraw;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Tower> towers = new ArrayList<>();
    private boolean isGameOver = false;
    private int balance;
    private int waveCounter;
    private int lives;
    private int killed;


    public Game() {
        this.gameMap = new GameMap(50, 25);
        this.gameMap.fillMap();
        this.gameMap.setStart(new Rect.Point(0, 1));
        this.gameMap.setFinish(new Rect.Point(49, 1));
        balance = 30;
        waveCounter = 0;
        lives = 5;
        killed = 0;
    }

    public void runGame() throws InterruptedException {
        int currentCount = 0;
        while (!isGameOver && !Thread.interrupted()) {
            if (enemies.size() == 0) {
                Thread.sleep(2000);
                waveCounter++;
                currentCount = waveCounter;
                headerRedraw = true;
            }
            if (currentCount > 0) {
                createEnemy();
                currentCount--;
            }
            Iterator<Tower> iterTowers = this.towers.iterator();
            while (iterTowers.hasNext() && !isGameOver) {
                Tower tower = iterTowers.next();
                if (tower.isAlive()) {
                    if (tower.fire(enemies)) {
                        balance += 10;
                        killed++;
                        headerRedraw = true;
                    }
                } else {
                    iterTowers.remove();
                }
                needRedraw = true;
            }

            Iterator<Enemy> iterEnemies = this.enemies.iterator();
            while (iterEnemies.hasNext() && !isGameOver) {
                Enemy enemy = iterEnemies.next();
                if (enemy.isAlive()) {
                    enemy.fire(towers);
                    boolean ifFinished = enemy.move(this.gameMap);
                    if (ifFinished) {
                        lives--;
                        headerRedraw = true;
                    }
                } else {
                    iterEnemies.remove();
                }
                if (lives <= 0)
                    isGameOver = true;
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
        if (balance - 10 >= 0 && isAvailableField(x, y)) {
            towers.add(new Tower(x, y));
            balance -= 10;
            headerRedraw = true;
        }
        setNeedRedraw(true);
    }

    private boolean isAvailableField(double x, double y) {
        boolean isAvailable = false;
        for (Rect rect : gameMap.getFieldsCoordinates()) {
            if (x >= rect.getStart().getX() && x <= rect.getEnd().getX() &&
                    y >= rect.getStart().getY() && y <= rect.getEnd().getY()) {
                isAvailable = true;
                break;
            }
        }
        for (Tower tower : towers) {
            if (tower.getX() == x && tower.getY() == y) {
                isAvailable = false;
                break;
            }
        }
        return isAvailable;
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

    public static Game getGame() {
        return game;
    }

    public int getBalance() {
        return balance;
    }

    public int getWaveCounter() {
        return waveCounter;
    }

    public int getLives() {
        return lives;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setWaveCounter(int waveCounter) {
        this.waveCounter = waveCounter;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public boolean isHeaderRedraw() {
        return headerRedraw;
    }

    public void setHeaderRedraw(boolean headerRedraw) {
        this.headerRedraw = headerRedraw;
    }

    public int getKilled() {
        return killed;
    }
}
