package com.sberstart.model;

import com.sberstart.aux.Upgrade;
import com.sberstart.entities.Enemy;
import com.sberstart.entities.GameMap;
import com.sberstart.entities.Tower;

import java.util.Collection;

public interface Model {
    Collection<Tower> getTowers();
    Collection<Enemy> getEnemies();
    GameMap getGameMap();
    void upgradeArmy();
    void upgradeTower(Tower tower, Upgrade up);
    void createEnemy();
    boolean createTower(double x, double y);
    boolean destroyTower(Tower tower);
    void towerAttack();
    void enemyAttack();
    void enemyMove();
    void initModel();
    Tower getTowerByCoord(double x, double y);
    void incWaveCounter();
    int getWaveCounter();
    boolean isGameOver();
    void setGameOver(boolean gameOver);

    int getLives();

    int getBalance();

    int getKilled();

    int getArmyPower();

    int getArmyRange();

    int getArmyHealth();
}
