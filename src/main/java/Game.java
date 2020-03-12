import entities.Enemy;
import entities.GameMap;
import entities.Tower;
import utils.Rect;
import utils.Upgrade;

import java.util.*;

public class Game {

    public static Game game;
    private GameMap gameMap;
    private boolean needRedraw;
    private boolean headerRedraw;
    private List<Enemy> enemies = Collections.synchronizedList(new ArrayList<>());
    private Set<Tower> towers = Collections.synchronizedSet(new HashSet<>());
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
        lives = 1;
        killed = 0;
    }

    public void runGame() throws InterruptedException {
        int currentCount = 0;
        while (!isGameOver && !Thread.interrupted()) {
            if (enemies.size() == 0) {
                Thread.sleep(2000);
                waveCounter++;
                upgradeEnemy(enemies, waveCounter);
                currentCount = waveCounter;
                headerRedraw = true;
            }
            if (currentCount > 0) {
                createEnemy();
                currentCount--;
            }
            synchronized (towers) {
                Iterator<Tower> iterTowers = towers.iterator();
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
            }

            synchronized (enemies) {
                Iterator<Enemy> iterEnemies = enemies.iterator();
                while (iterEnemies.hasNext() && !isGameOver) {
                    Enemy enemy = iterEnemies.next();
                    if (enemy.isAlive()) {
                        enemy.fire(towers);
                        boolean ifFinished = enemy.move(gameMap);
                        if (ifFinished) {
                            lives--;
                            headerRedraw = true;
                        }
                    } else {
                        iterEnemies.remove();
                    }
                    if (lives <= 0) {
                        isGameOver = true;
                        headerRedraw = true;
                    }
                    needRedraw = true;
                }
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

    public Tower getTowerOnClick(double x, double y) {
        if (towers.contains(new Tower(x, y))) {
            for (Tower tower : towers) {
                if (tower.getX() == x && tower.getY() == y)
                    return tower;
            }
        }
        createTower(x, y);
        return null;
    }

    public void createTower(double x, double y) {
        if (balance - 10 >= 0 && isAvailableField(x, y) && !isGameOver) {
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

    public boolean upgradeTower(Tower tower, Upgrade up) {
        switch (up) {
            case RANGE:
                if (balance >= 100) {
                    balance -= 100;
                    tower.upgrade(Upgrade.RANGE);
                    return true;
                }
                break;
            case POWER:
                if (balance >= 30) {
                    balance -= 30;
                    tower.upgrade(Upgrade.POWER);
                    return true;
                }
                break;
            case ARMOR:
                if (balance >= 10) {
                    balance -= 10;
                    tower.upgrade(Upgrade.ARMOR);
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean destroyTower(double x, double y) {
        if (towers.remove(new Tower(x, y))) {
            needRedraw = true;
            return true;
        }
        return false;
    }

    public void createEnemy() {
        enemies.add(new Enemy(gameMap.getStart().getX(), gameMap.getStart().getY()));
    }

    public void upgradeEnemy(List<Enemy> enemyList, int waveCounter) {
        for (Enemy enemy : enemyList) {
            if (waveCounter % 10 == 0) {
                enemy.upgrade(Upgrade.RANGE);
            } else if (waveCounter % 2 == 0) {
                enemy.upgrade(Upgrade.POWER);
                enemy.upgrade(Upgrade.ARMOR);
            } else {
                break;
            }
        }
    }

    public boolean isNeedRedraw() {
        return needRedraw;
    }

    public void setNeedRedraw(boolean needRedraw) {
        this.needRedraw = needRedraw;
    }

    public Set<Tower> getTowers() {
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
