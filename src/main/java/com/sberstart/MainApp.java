package com.sberstart;

import com.sberstart.entities.Enemy;
import com.sberstart.entities.Tower;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import com.sberstart.aux.GameObject;
import com.sberstart.aux.Orientation;
import com.sberstart.aux.Rect;
import com.sberstart.aux.Upgrade;

import java.util.*;

public class MainApp extends Application {
    private double scale;
    private GraphicsContext gc;
    private GraphicsContext header;
    private GraphicsContext gcTowerInfo;
    private GraphicsContext gcArmyInfo;
    private VBox buttonsTower;
    private Button startPause;
    private Image explosion;
    private Image road;
    private Image field;
    private Queue<ExplosionAnimation> explosionAnimations = new LinkedList<>();
    private List<HealthData> healthDataList = new ArrayList<>();
    private final static int MAX_FRAMES_EXPL = 48;
    private final static int ANIM_SPRITE_ROWS = 6;
    private final static int ANIM_SPRITE_COLS = 8;
    private final static int ANIM_FRAME_WIDTH = 256;
    private final static int ANIM_FRAME_HEIGHT = 256;
    private final static int NANOSEC_PER_FRAME = 20_000;
    private final static double HEADER_HEIGHT = 68;
    private static Thread thread;
    private Tower selected;
    private boolean redrawTowerInfo;
    private static Game game;

    private static class HealthData {
        private double x;
        private double y;
        private double percentage;

        public HealthData(double x, double y, double percentage) {
            this.x = x;
            this.y = y;
            this.percentage = percentage;
        }
    }

    private static class ExplosionAnimation {
        private double x;
        private double y;
        private int stage;
        private long now;

        public ExplosionAnimation(double x, double y, long now) {
            this.x = x;
            this.y = y;
            this.now = now;
            this.stage = 0;
        }
    }

    @Override
    public void stop() {
        thread.interrupt();
    }

    private void redrawAll() {
        redrawTowerInfo = true;
        game.setNeedRedraw(true);
        game.setHeaderRedraw(true);
    }

    Button createUpgradeButton(double width, EventHandler<ActionEvent> value) {
        Button button = new Button();
        button.setPrefWidth(width * 0.05);
        button.setFont(Font.font(9));
        button.setMaxHeight(13);
        button.setOnAction(value);
        return button;
    }

    private void startStopHandle() {
        if (!game.isStarted()) {
            game.setStarted(true);
            game.setGameOver(false);
            startPause.setText("Pause");
            redrawAll();
        } else {
            if (game.isGameOver()) {
                game.gameInit();
                game.setGameOver(false);
                redrawAll();
                startPause.setText("Pause");
            } else {
                game.setStarted(false);
                redrawAll();
                startPause.setText("Start");
            }
        }
    }

    HBox createHeader(double width) {
        HBox hBox = new HBox();
        Canvas info = new Canvas(width * 0.4, HEADER_HEIGHT);
        header = info.getGraphicsContext2D();

        GridPane buttonBox = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(HEADER_HEIGHT);
        column1.setPrefWidth(width * 0.1);
        buttonBox.getColumnConstraints().add(column1);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(HEADER_HEIGHT);
        column2.setPrefWidth(width * 0.1);
        buttonBox.getColumnConstraints().add(column2);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(100);
        buttonBox.getRowConstraints().add(row1);

        startPause= new Button("Start");
        startPause.setMaxWidth(Double.MAX_VALUE);
        startPause.setMaxHeight(Double.MAX_VALUE);
        startPause.setOnAction(event -> startStopHandle());
        GridPane.setMargin(startPause, new Insets(10));

        Button exit = new Button("Exit");
        exit.setMaxWidth(Double.MAX_VALUE);
        exit.setMaxHeight(Double.MAX_VALUE);
        exit.setOnAction(event -> ((Stage)exit.getScene().getWindow()).close());
        GridPane.setMargin(exit, new Insets(10));

        buttonBox.setStyle("-fx-background-color: #006400");

        buttonBox.add(startPause, 0, 0);
        buttonBox.add(exit, 1, 0);
        buttonBox.setAlignment(Pos.CENTER);

        Canvas canvasTowerInfo = new Canvas(width * 0.2, HEADER_HEIGHT);
        AnchorPane anchorPane = new AnchorPane();
        gcTowerInfo = canvasTowerInfo.getGraphicsContext2D();
        gcTowerInfo.setFill(Color.DARKGREEN);
        gcTowerInfo.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        buttonsTower = new VBox(2);
        Button button1 = createUpgradeButton(width, event -> {
            if (game.upgradeTower(selected, Upgrade.RANGE)) {
                redrawTowerInfo = true;
                game.setHeaderRedraw(true);
            }
        });
        buttonsTower.getChildren().add(button1);
        Button button2 = createUpgradeButton(width, event -> {
            if (game.upgradeTower(selected, Upgrade.POWER)) {
                redrawTowerInfo = true;
                game.setHeaderRedraw(true);
            }
        });
        buttonsTower.getChildren().add(button2);
        Button button3 = createUpgradeButton(width, event -> {
            if (game.upgradeTower(selected, Upgrade.ARMOR)) {
                redrawTowerInfo = true;
                game.setHeaderRedraw(true);
            }
        });
        buttonsTower.getChildren().add(button3);
        AnchorPane.setRightAnchor(buttonsTower, 5.0);
        AnchorPane.setTopAnchor(buttonsTower, 5.0);
        AnchorPane.setBottomAnchor(buttonsTower, 5.0);

        anchorPane.getChildren().add(canvasTowerInfo);
        anchorPane.getChildren().add(buttonsTower);

        Canvas canvasArmyInfo = new Canvas(width * 0.2, HEADER_HEIGHT);
        AnchorPane anchorPane1 = new AnchorPane();
        gcArmyInfo = canvasArmyInfo.getGraphicsContext2D();
        anchorPane1.getChildren().add(canvasArmyInfo);

        hBox.getChildren().add(info);
        hBox.getChildren().add(anchorPane);
        hBox.getChildren().add(anchorPane1);
        hBox.getChildren().add(buttonBox);
        return hBox;
    }

    @Override
    public void start(Stage primaryStage) {
        double width = game.getGameMap().getWidth();
        double height = game.getGameMap().getHeight();
        this.scale = Math.min(1500.0 / width, 1500.0 / height);
        redrawTowerInfo = true;
        Canvas canvas = new Canvas(width * scale, height * scale);
        gc = canvas.getGraphicsContext2D();
        canvas.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                selected = game.getTowerOnClick((int)(event.getX() / scale), (int)(event.getY() / scale));
                redrawTowerInfo = true;
            } else if (event.getButton() == MouseButton.SECONDARY) {
                game.destroyTower((int)(event.getX() / scale), (int)(event.getY() / scale));
            }
        });
        VBox vBox = new VBox();
        HBox hBox = createHeader(width * scale);
        
        vBox.getChildren().add(hBox);
        vBox.getChildren().add(canvas);

        explosion = new Image("images/explosion.png");
        road = new Image("images/road.jpg");
        field = new Image("images/field.jpg");

        Scene scene = new Scene(vBox);
        primaryStage.setTitle("Tower Defence");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        at.start();
        redrawAll();
    }

    private void drawHealth(double x, double y, double percentage) {
        double width = scale * 0.85;
        double startx = (scale - width) / 2 + x;
        gc.setFill(Color.DARKGRAY);
        gc.fillRoundRect(startx, y, width, 4, 2, 2);
        gc.setFill(percentage > 0.35 ? Color.GREEN : Color.RED);
        gc.fillRoundRect(startx, y, width * percentage, 4, 2, 2);
    }

    private void drawBarrel(double startX, double startY, double barrelLength, double angle) {
        gc.save();
        gc.setFill(Color.BLACK);
        gc.translate(startX + scale / 2, startY + scale / 2);
        gc.rotate(angle + 90);
        gc.fillRect(-2, 0, 3, barrelLength);
        gc.restore();
    }

    private void addToExploseAnim(GameObject obj, double x, double y, double barrelLength, long now) {
        double angleRad = Math.toRadians(obj.getAngle() + 180);
        if (obj.isShooting()) {
            explosionAnimations.add(new ExplosionAnimation(x + scale / 2 + barrelLength * Math.cos(angleRad),
                    y + scale / 2 + barrelLength * Math.sin(angleRad), now));
            obj.setShooting(false);
        }
    }

    private void drawTower(Tower tower, long now) {
        double startX = tower.getX() * scale;
        double startY = tower.getY() * scale;
        gc.setFill(Color.DARKKHAKI);
        gc.fillRoundRect(startX, startY, scale, scale, scale / 4, scale / 4);
        gc.setFill(Color.KHAKI);
        double r = scale * 0.9;
        gc.fillOval(startX + (scale - r) / 2 , startY + (scale - r) / 2, r, r);
        double barrelLength = scale * 0.8;
        drawBarrel(startX, startY, barrelLength, tower.getAngle());
        double percentage = (double)tower.getCurrentHealth() / tower.getMaxHealth();
        healthDataList.add(new HealthData(startX, startY - 6, percentage));
        addToExploseAnim(tower, startX, startY, barrelLength, now);
    }

    private void drawWheels(Orientation orientation, double startX, double startY) {
        gc.setFill(Color.color(0.3, 0.3, 0.3));
        double wheelsSize = scale * 0.9;
        double arcR = wheelsSize / 8;
        if (orientation == Orientation.VERTICAL) {
            gc.fillRoundRect(startX + (scale - wheelsSize) / 2,
                    startY + (scale - wheelsSize) / 2,
                    wheelsSize / 5, wheelsSize, arcR, arcR);
            gc.fillRoundRect(startX + (scale - wheelsSize) / 2 + wheelsSize * 4 / 5,
                    startY + (scale - wheelsSize) / 2,
                    wheelsSize / 5, wheelsSize, arcR, arcR);
        } else {
            gc.fillRoundRect(startX + (scale - wheelsSize) / 2,
                    startY + (scale - wheelsSize) / 2,
                    wheelsSize, wheelsSize / 5, arcR, arcR);
            gc.fillRoundRect(startX + (scale - wheelsSize) / 2,
                    startY + (scale - wheelsSize) / 2 + wheelsSize * 4 / 5,
                    wheelsSize, wheelsSize / 5, arcR, arcR);
        }
    }

    private void drawEnemy(Enemy enemy, long now) {
        double startX = enemy.getX() * scale;
        double startY = enemy.getY() * scale;
        gc.setFill(Color.BROWN);
        double towerDiameter = scale * 0.7;
        gc.fillOval(startX + (scale - towerDiameter) / 2,
                startY + (scale - towerDiameter) / 2, towerDiameter, towerDiameter);
        if (enemy.getPrevPoint() == null)
            drawWheels(Orientation.HORIZONTAL, startX, startY);
        else {
            Rect.Point prevPoint = enemy.getPrevPoint();
            if (prevPoint.getX() == enemy.getX()) {
                drawWheels(Orientation.VERTICAL, startX, startY);
            } else {
                drawWheels(Orientation.HORIZONTAL, startX, startY);
            }
        }
        double barrelLength = scale * 0.7;
        drawBarrel(startX, startY, barrelLength, enemy.getAngle());
        healthDataList.add(new HealthData(startX, startY - 6, (double)enemy.getCurrentHealth() / enemy.getMaxHealth()));
        addToExploseAnim(enemy, startX, startY, barrelLength, now);
    }

    private void drawField(long now) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        char[][] matrix = game.getGameMap().getMatrix();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                gc.drawImage(matrix[i][j] == '#' ? road : field, 0, 0, 200, 200, j * scale, i * scale, scale, scale);
            }
        }
        for (Tower tower : game.getTowers()) {
            drawTower(tower, now);
        }
        for (Enemy enemy : game.getEnemies()) {
            drawEnemy(enemy, now);
        }
        for (HealthData healthData : healthDataList) {
            drawHealth(healthData.x, healthData.y, healthData.percentage);
        }
        healthDataList.clear();
    }

    private void drawGameOver() {
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        double gameOverWidth = width * 0.8;
        double gameOverHeight = height * 0.3;
        int size = 50;

        gc.setFill(Color.color(1,0,0, 0.5));
        gc.fillRoundRect((width - gameOverWidth) / 2, (height - gameOverHeight) / 2, gameOverWidth, gameOverHeight, 20, 20);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Comic Sans MS", size));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("GAME OVER", (width - gameOverWidth) / 2 + gameOverWidth / 2,
                (height - gameOverHeight) / 2 + gameOverHeight / 2);
    }


    private void fillGc(GraphicsContext gc) {
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setStroke(Color.BLACK);
        gc.strokeRoundRect(2, 2, gc.getCanvas().getWidth() - 4, gc.getCanvas().getHeight() - 4, 5, 5);
        gc.setFill(Color.WHITE);
    }

    protected AnimationTimer at = new AnimationTimer(){
        @Override
        public void handle(long now) {
            if (game.isNeedRedraw()) {
                drawField(now);
                game.setNeedRedraw(false);
                Iterator<ExplosionAnimation> iterExplAnim = explosionAnimations.iterator();
                while (iterExplAnim.hasNext()) {
                    ExplosionAnimation explAnim = iterExplAnim.next();
                    if (explAnim.stage >= MAX_FRAMES_EXPL) {
                        iterExplAnim.remove();
                        game.setNeedRedraw(true);
                    }
                    else {
                        if (now - explAnim.now >= NANOSEC_PER_FRAME) {
                            int startX = explAnim.stage % ANIM_SPRITE_COLS;
                            int startY = explAnim.stage / ANIM_SPRITE_COLS;
                            gc.drawImage(explosion, startX * ANIM_FRAME_WIDTH,
                                    startY * ANIM_FRAME_HEIGHT, ANIM_FRAME_WIDTH, ANIM_FRAME_HEIGHT,
                                    explAnim.x - scale /2 , explAnim.y - scale /2 , scale, scale);
                            explAnim.now = now;
                            explAnim.stage++;
                            game.setNeedRedraw(true);
                        }
                    }
                }
                if (game.isGameOver()) {
                    drawGameOver();
                    startPause.setText("Start");
                }
            }
            if (game.isHeaderRedraw()) {
                header.setFont(Font.font("Herculanum", 20));
                fillGc(header);
                header.fillText(String.format("Level: %d", game.getWaveCounter()), 5, 22);
                header.fillText(String.format("Lives: %d", game.getLives()), 5, 48);
                header.fillText(String.format("Gold: %d", game.getBalance()), 170, 22);
                header.fillText(String.format("Killed: %d", game.getKilled()), 170, 48);

                gcArmyInfo.setFont(Font.font("Herculanum", 15));
                fillGc(gcArmyInfo);
                gcArmyInfo.fillText(String.format("Range: %d", game.getArmyRange()), 5, 16);
                gcArmyInfo.fillText(String.format("Power: %d", game.getArmyPower()), 5, 36);
                gcArmyInfo.fillText(String.format("Max armory: %d", game.getArmyHealth()), 5, 55);
                game.setHeaderRedraw(false);
            }
            if (redrawTowerInfo) {
                gcTowerInfo.setFont(Font.font("Herculanum", 15));
                fillGc(gcTowerInfo);
                if (selected != null) {
                    buttonsTower.setVisible(true);
                    ((Button)buttonsTower.getChildren().get(0)).setText("\u2191 " + selected.getFireRangeUpgradeCost());
                    ((Button)buttonsTower.getChildren().get(1)).setText("\u2191 " + selected.getPowerUpgradeCost());
                    ((Button)buttonsTower.getChildren().get(2)).setText("\u2191 " + selected.getHealthUpgradeCost());
                    gcTowerInfo.fillText(String.format("Range: %d", selected.getFireRange()), 5, 16);
                    gcTowerInfo.fillText(String.format("Power: %d", selected.getPower()), 5, 36);
                    gcTowerInfo.fillText(String.format("Armory: %d/%d", selected.getCurrentHealth(), selected.getMaxHealth()), 5, 55);
                    gcTowerInfo.fillText(String.format("lvl: %d", selected.getFireRangeUpgradeLvl()), 120, 16);
                    gcTowerInfo.fillText(String.format("lvl: %d", selected.getPowerUpgradeLvl()), 120, 36);
                    gcTowerInfo.fillText(String.format("lvl: %d", selected.getHealthUpgradeLvl()), 120, 55);
                } else {
                    buttonsTower.setVisible(false);
                }
                redrawTowerInfo = false;
            }
        }
    };

    public static void main(String[] args) {
        game = new Game();
        thread = new Thread(() -> {
            try {
                game.runGame();
            } catch (InterruptedException ignored) {   }
        });
        thread.start();
        launch();
    }
}
