package com.sberstart.model;

import com.sberstart.aux.Rect;
import com.sberstart.aux.Upgrade;
import com.sberstart.entities.Enemy;
import com.sberstart.entities.GameMap;
import com.sberstart.entities.Tower;

import java.util.*;

public class ModelImpl implements Model {
    private GameMap gameMap;
    private List<Enemy> enemies = Collections.synchronizedList(new ArrayList<>());
    private Set<Tower> towers = Collections.synchronizedSet(new HashSet<>());
    private volatile boolean isGameOver = false;
    private int balance;
    private int lives;
    private int killed;
    private int armyRange;
    private int armyPower;
    private int armyHealth;
    private int waveCounter;

    @Override
    public boolean isGameOver() {
        return isGameOver;
    }

    @Override
    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    @Override
    public void towerAttack() {
        synchronized (towers) {
            Iterator<Tower> iterTowers = towers.iterator();
            while (iterTowers.hasNext() && !isGameOver) {
                Tower tower = iterTowers.next();
                if (tower.isAlive()) {
                    if (tower.getAttackSpeed() == 0) {
                        if (tower.fire(enemies)) {
                            balance += (10 + waveCounter);
                            killed++;
                        }
                        tower.resetAttackSpeed();
                    } else {
                        tower.decAttackSpeed();
                    }
                } else {
                    iterTowers.remove();
                }
            }
        }
    }

    @Override
    public void enemyAttack() {
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
            }
        }
    }

    @Override
    public void enemyMove() {
        synchronized (enemies) {
            Iterator<Enemy> iterEnemies = enemies.iterator();
            while (iterEnemies.hasNext()) {
                Enemy enemy = iterEnemies.next();
                if (enemy.isAlive()) {
                    boolean ifFinished = enemy.move(gameMap);
                    if (ifFinished) {
                        lives--;
                    }
                } else {
                    iterEnemies.remove();
                }
                if (lives <= 0) {
                    isGameOver = true;
                }
            }
        }
    }

    @Override
    public GameMap getGameMap() {
        return gameMap;
    }

    @Override
    public void initModel() {
        gameMap = new GameMap(50, 25);
        gameMap.fillMap();
        gameMap.setStart(new Rect.Point(0, 1));
        gameMap.setFinish(new Rect.Point(49, 1));
        enemies.clear();
        towers.clear();
        balance = 30;
        lives = 1;
        killed = 0;
        armyRange = 3;
        armyPower = 1;
        armyHealth = 10;
        waveCounter = 0;
    }

    @Override
    public boolean createTower(double x, double y) {
        if (balance - 10 >= 0 && isAvailableField(x, y) && !isGameOver) {
            towers.add(new Tower(x, y));
            balance -= 10;
            return true;
        }
        return false;
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

    @Override
    public void upgradeTower(Tower tower, Upgrade up) {
        switch (up) {
            case RANGE:
                if (balance >= tower.getFireRangeUpgradeCost()) {
                    balance -= tower.getFireRangeUpgradeCost();
                    tower.upgrade(Upgrade.RANGE);
                    tower.setFireRangeUpgradeCost(tower.getFireRangeUpgradeCost() + 40 * tower.getFireRangeUpgradeLvl());
                }
                break;
            case POWER:
                if (balance >= tower.getPowerUpgradeCost()) {
                    balance -= tower.getPowerUpgradeCost();
                    tower.upgrade(Upgrade.POWER);
                    tower.setPowerUpgradeCost(tower.getPowerUpgradeCost() + 10 * tower.getPowerUpgradeLvl());
                }
                break;
            case ARMOR:
                if (balance >= tower.getHealthUpgradeCost()) {
                    balance -= tower.getHealthUpgradeCost();
                    tower.upgrade(Upgrade.ARMOR);
                    tower.setHealthUpgradeCost(tower.getHealthUpgradeCost() + 5 * tower.getHealthUpgradeLvl());
                }
                break;
        }
    }



    @Override
    public Tower getTowerByCoord(double x, double y) {
        if (towers.contains(new Tower(x, y))) {
            for (Tower tower : towers) {
                if (tower.getX() == x && tower.getY() == y)
                    return tower;
            }
        }
        return null;
    }

    @Override
    public boolean destroyTower(Tower tower) {
        return towers.remove(tower);
    }

    @Override
    public void createEnemy() {
        enemies.add(new Enemy(gameMap.getStart().getX(), gameMap.getStart().getY(), armyRange, armyPower, armyHealth));
    }


    @Override
    public void upgradeArmy() {
        if (waveCounter % 15 == 0) {
            armyRange += 1;
        } else if (waveCounter % 5 == 0) {
            armyPower += 1;
            armyHealth += 10;
        }
    }

    @Override
    public Collection<Tower> getTowers() {
        return towers;
    }

    @Override
    public Collection<Enemy> getEnemies() {
        return enemies;
    }

    @Override
    public void incWaveCounter() {
        waveCounter++;
    }

    @Override
    public int getWaveCounter() {
        return waveCounter;
    }

    @Override
    public int getLives() {
        return lives;
    }

    @Override
    public int getBalance() {
        return balance;
    }

    @Override
    public int getKilled() {
        return killed;
    }

    @Override
    public int getArmyPower() {
        return armyPower;
    }

    @Override
    public int getArmyRange() {
        return armyRange;
    }

    @Override
    public int getArmyHealth() {
        return armyHealth;
    }
}
