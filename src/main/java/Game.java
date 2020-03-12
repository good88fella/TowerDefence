import entities.Enemy;
import entities.GameMap;
import entities.Tower;
import utils.Rect;
import utils.Upgrade;

import java.util.*;

public class Game {

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
    private int armyRange;
    private int armyPower;
    private int armyHealth;
    private boolean isStarted;
    private int currentCount;


    public void gameInit() {
        this.gameMap = new GameMap(50, 25);
        this.gameMap.fillMap();
        this.gameMap.setStart(new Rect.Point(0, 1));
        this.gameMap.setFinish(new Rect.Point(49, 1));
        enemies.clear();
        towers.clear();
        balance = 30;
        waveCounter = 0;
        lives = 1;
        killed = 0;
        armyRange = 3;
        armyPower = 1;
        armyHealth = 10;
        currentCount = 0;
    }

    public Game() {
        gameInit();
    }

    public void runGame() throws InterruptedException {
        while (!Thread.interrupted()) {
            if (isStarted && !isGameOver) {
                if (enemies.size() == 0) {
                    Thread.sleep(5000);
                    waveCounter++;
                    upgradeArmy(waveCounter);
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
                                balance += (10 + 2 * waveCounter);
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
        if (!towers.contains(tower) || tower == null)
            return false;
        switch (up) {
            case RANGE:
                int upRangeCost = tower.getFireRangeUpgradeCost() + 40 * tower.getFireRangeUpgradeLvl();
                if (balance >= upRangeCost) {
                    balance -= upRangeCost;
                    tower.upgrade(Upgrade.RANGE);
                    tower.setFireRangeUpgradeCost(upRangeCost);
                    return true;
                }
                break;
            case POWER:
                int upPowerCost = tower.getPowerUpgradeCost() + 10 * tower.getPowerUpgradeLvl();
                if (balance >= upPowerCost) {
                    balance -= upPowerCost;
                    tower.upgrade(Upgrade.POWER);
                    tower.setPowerUpgradeCost(upPowerCost);
                    return true;
                }
                break;
            case ARMOR:
                int upArmorCost = tower.getHealthUpgradeCost() + 5 * tower.getHealthUpgradeLvl();
                if (balance >= upArmorCost) {
                    balance -= upArmorCost;
                    tower.upgrade(Upgrade.ARMOR);
                    tower.setHealthUpgradeCost(upArmorCost);
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
        enemies.add(new Enemy(gameMap.getStart().getX(), gameMap.getStart().getY(), armyRange, armyPower, armyHealth));
    }

    public void upgradeArmy(int waveCounter) {
        if (waveCounter % 15 == 0) {
            armyRange += 1;
        } else if (waveCounter % 5 == 0) {
            armyPower += 1;
            armyHealth += 10;
        }
    }

    public void restore() {

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

    public int getArmyRange() {
        return armyRange;
    }

    public int getArmyPower() {
        return armyPower;
    }

    public int getArmyHealth() {
        return armyHealth;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }
}
