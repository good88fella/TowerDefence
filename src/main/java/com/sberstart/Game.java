package com.sberstart;

import com.sberstart.entities.Enemy;
import com.sberstart.entities.GameMap;
import com.sberstart.entities.Tower;
import com.sberstart.aux.Rect;
import com.sberstart.aux.Upgrade;

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
    private int respawn;

    public void gameInit() {
        this.gameMap = new GameMap(50, 25);
        this.gameMap.fillMap();
        this.gameMap.setStart(new Rect.Point(0, 1));
        this.gameMap.setFinish(new Rect.Point(49, 1));
        enemies.clear();
        towers.clear();
        balance = 30;
        waveCounter = 0;
        lives = 5;
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
                if (enemies.size() == 0 && currentCount == 0) {
                    Thread.sleep(5000);
                    waveCounter++;
                    upgradeArmy(waveCounter);
                    currentCount = waveCounter;
                    headerRedraw = true;
                }
                if (currentCount > 0) {
                    if (respawn == 0) {
                        createEnemy();
                        currentCount--;
                        respawn = (int)(15 - 10 * Math.random());
                    } else {
                        respawn--;
                    }
                }
                towerAttack();
                enemyAttack();
                enemyMove();
                Thread.sleep(50);
            }
        }
    }

    private void towerAttack() {
        synchronized (towers) {
            Iterator<Tower> iterTowers = towers.iterator();
            while (iterTowers.hasNext() && !isGameOver) {
                Tower tower = iterTowers.next();
                if (tower.isAlive()) {
                    if (tower.getAttackSpeed() == 0) {
                        if (tower.fire(enemies)) {
                            balance += (10 + waveCounter);
                            killed++;
                            headerRedraw = true;
                        }
                        tower.resetAttackSpeed();
                    } else {
                        tower.decAttackSpeed();
                    }
                } else {
                    iterTowers.remove();
                }
                needRedraw = true;
            }
        }
    }

    private void enemyAttack() {
        synchronized (enemies) {
            Iterator<Enemy> iterEnemies = enemies.iterator();
            while (iterEnemies.hasNext()) {
                Enemy enemy = iterEnemies.next();
                if (enemy.isAlive()) {
                    if (enemy.getAttackSpeed() == 0) {
                        enemy.fire(towers);
                        enemy.resetAttackSpeed();
                    } else {
                        enemy.decAttackSpeed();
                    }
                } else {
                    iterEnemies.remove();
                }
                needRedraw = true;
            }
        }
    }

    private void enemyMove() {
        synchronized (enemies) {
            Iterator<Enemy> iterEnemies = enemies.iterator();
            while (iterEnemies.hasNext()) {
                Enemy enemy = iterEnemies.next();
                if (enemy.isAlive()) {
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
                if (balance >= tower.getFireRangeUpgradeCost()) {
                    balance -= tower.getFireRangeUpgradeCost();
                    tower.upgrade(Upgrade.RANGE);
                    tower.setFireRangeUpgradeCost(tower.getFireRangeUpgradeCost() + 40 * tower.getFireRangeUpgradeLvl());
                    return true;
                }
                break;
            case POWER:
                if (balance >= tower.getPowerUpgradeCost()) {
                    balance -= tower.getPowerUpgradeCost();
                    tower.upgrade(Upgrade.POWER);
                    tower.setPowerUpgradeCost(tower.getPowerUpgradeCost() + 10 * tower.getPowerUpgradeLvl());
                    return true;
                }
                break;
            case ARMOR:
                if (balance >= tower.getHealthUpgradeCost()) {
                    balance -= tower.getHealthUpgradeCost();
                    tower.upgrade(Upgrade.ARMOR);
                    tower.setHealthUpgradeCost(tower.getHealthUpgradeCost() + 5 * tower.getHealthUpgradeLvl());
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
