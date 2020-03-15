package com.sberstart.presenter;

import com.sberstart.aux.Upgrade;
import com.sberstart.entities.Enemy;
import com.sberstart.entities.GameMap;
import com.sberstart.entities.Tower;
import com.sberstart.model.Model;
import com.sberstart.model.ModelImpl;
import com.sberstart.view.View;

import java.util.Collection;

public class PresenterImpl implements Presenter{
    private Model model;
    private View view;

    private boolean isStarted;
    private int currentCount;
    private int respawn;
    Thread thread;

    public void setView(View view) {
        this.view = view;
    }

    public PresenterImpl() {
        this.model = new ModelImpl();
        gameInit();
        thread = new Thread(() -> {
            try {
                runGame();
            } catch (InterruptedException ignored) {   }
        });
        thread.start();
    }

    public void gameInit() {
        currentCount = 0;
        model.initModel();
    }

    @Override
    public void onExit() {
        thread.interrupt();
    }

    public void runGame() throws InterruptedException {
        while (!Thread.interrupted()) {
            if (isStarted && !model.isGameOver()) {
                if (model.getEnemies().size() == 0 && currentCount == 0) {
                    Thread.sleep(5000);
                    model.incWaveCounter();
                    model.upgradeArmy();
                    currentCount = model.getWaveCounter();
                    view.refreshHeader();
                }
                if (currentCount > 0) {
                    if (respawn == 0) {
                        model.createEnemy();
                        currentCount--;
                        respawn = (int)(15 - 10 * Math.random());
                    } else {
                        respawn--;
                    }
                }
                model.towerAttack();
                model.enemyAttack();
                model.enemyMove();
                view.refreshView();
                view.refreshHeader();
                Thread.sleep(50);
            }
        }
    }

    @Override
    public boolean startGame() {
        if (!isStarted() || model.isGameOver()) {
            gameInit();
            isStarted = true;
            model.setGameOver(false);
            view.refreshHeader();
            view.refreshView();
            view.refreshTowerInfo(null);
            return (true);
        }
        return false;
    }

    @Override
    public void pauseGame() {
        if (isStarted && !model.isGameOver()) {
            isStarted = false;
            view.refreshHeader();
            view.refreshView();
            view.refreshTowerInfo(null);
        }
    }

    @Override
    public void upgradeTower(Tower tower, Upgrade upgrade) {
        if (tower != null && model.getTowers().contains(tower)) {
            model.upgradeTower(tower, upgrade);
            view.refreshTowerInfo(tower);
            view.refreshView();
        }
    }

    @Override
    public Tower selectTower(double x, double y) {
        Tower tower = model.getTowerByCoord(x, y);
        if (tower == null) {
            if (model.createTower(x, y)) {
                view.refreshView();
                view.refreshHeader();
                return null;
            }
        }
        return tower;
    }

    @Override
    public void destroyTower(double x, double y) {
        Tower tower = model.getTowerByCoord(x, y);
        if (tower != null) {
            if (model.destroyTower(tower))
                view.refreshView();
        }
    }

    @Override
    public Collection<Tower> getTowers() {
        return model.getTowers();
    }

    @Override
    public Collection<Enemy> getEnemies() {
        return model.getEnemies();
    }

    @Override
    public GameMap getGameMap() {
        return model.getGameMap();
    }

    @Override
    public boolean isGameOver() {
        return model.isGameOver();
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public int getWaveCounter() {
        return model.getWaveCounter();
    }

    @Override
    public int getLives() {
        return model.getLives();
    }

    @Override
    public int getBalance() {
        return model.getBalance();
    }

    @Override
    public int getKilled() {
        return model.getKilled();
    }

    @Override
    public int getArmyPower() {
        return model.getArmyPower();
    }

    @Override
    public int getArmyRange() {
        return model.getArmyRange();
    }

    @Override
    public int getArmyHealth() {
        return model.getArmyHealth();
    }
}
