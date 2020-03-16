package com.sberstart.presenter;

import com.sberstart.aux.Upgrade;
import com.sberstart.entities.Enemy;
import com.sberstart.entities.GameMap;
import com.sberstart.entities.Tower;
import com.sberstart.view.View;
import com.sberstart.view.ViewImpl;

import java.util.Collection;

public interface Presenter  {
    boolean startGame();
    void pauseGame();
    boolean isGameOver();
    boolean isStarted();
    void upgradeTower(Tower tower, Upgrade upgrade);
    Tower selectTower(double x, double y);
    void destroyTower(double x, double y);
    GameMap getGameMap();
    Collection<Tower> getTowers();
    Collection<Enemy> getEnemies();
    void onExit();

    int getWaveCounter();

    int getLives();

    int getBalance();

    int getKilled();

    int getArmyPower();

    int getArmyRange();

    int getArmyHealth();

    void setView(View view);
}
