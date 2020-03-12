import entities.Enemy;
import entities.Tower;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import utils.GameObject;
import utils.Orientation;
import utils.Rect;
import utils.Upgrade;

import java.util.*;

public class MainApp extends Application {
    private double scale;
    private GraphicsContext gc;
    private GraphicsContext header;
    private GraphicsContext gcTowerInfo;
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

    private void addLines(double width, double height, Group group) {
        for (int i = 0; i <= height; i++) {
            Line line = new Line(0, i * scale, width * scale,  i * scale);
            line.setStroke(Color.LIGHTGREY);
            group.getChildren().add(line);
        }
        for (int i = 0; i <= width; i++) {
            Line line = new Line(i * scale, 0, i * scale, height * scale);
            line.setStroke(Color.LIGHTGREY);
            group.getChildren().add(line);
        }
    }

    @Override
    public void stop() throws Exception {
        thread.interrupt();
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

        Button startFinish = new Button("Start/Stop");
        startFinish.setMaxWidth(Double.MAX_VALUE);
        startFinish.setMaxHeight(Double.MAX_VALUE);
        GridPane.setMargin(startFinish, new Insets(10));

        Button exit = new Button("Exit");
        exit.setMaxWidth(Double.MAX_VALUE);
        exit.setMaxHeight(Double.MAX_VALUE);
        GridPane.setMargin(exit, new Insets(10));

        buttonBox.setStyle("-fx-background-color: #006400");

        buttonBox.add(startFinish, 0, 0);
        buttonBox.add(exit, 1, 0);
        buttonBox.setAlignment(Pos.CENTER);

        Canvas canvasTowerInfo = new Canvas(width * 0.2, HEADER_HEIGHT);
        AnchorPane anchorPane = new AnchorPane();
        gcTowerInfo = canvasTowerInfo.getGraphicsContext2D();
        gcTowerInfo.setFill(Color.DARKGREEN);
        gcTowerInfo.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        VBox vButtons = new VBox(2);
        Button button1 = new Button("\u2191 something");
        button1.setPrefWidth(width * 0.05);
        button1.setFont(Font.font(9));
        button1.setMaxHeight(13);
        button1.setOnAction(event -> {
            if (game.upgradeTower(selected, Upgrade.RANGE))
                redrawTowerInfo = true;
        });
        vButtons.getChildren().add(button1);
        Button button2 = new Button("\u2191 something");
        button2.setPrefWidth(width * 0.05);
        button2.setFont(Font.font(9));
        button2.setMaxHeight(13);
        button2.setOnAction(event -> {
            if (game.upgradeTower(selected, Upgrade.POWER))
                redrawTowerInfo = true;
        });
        vButtons.getChildren().add(button2);
        Button button3 = new Button("\u2191 something");
        button3.setPrefWidth(width * 0.05);
        button3.setFont(Font.font(9));
        button3.setMaxHeight(13);
        button3.setOnAction(event -> {
            if (game.upgradeTower(selected, Upgrade.ARMOR))
                redrawTowerInfo = true;
        });
        vButtons.getChildren().add(button3);
        AnchorPane.setRightAnchor(vButtons, 5.0);
        AnchorPane.setTopAnchor(vButtons, 5.0);
        AnchorPane.setBottomAnchor(vButtons, 5.0);


        anchorPane.getChildren().add(canvasTowerInfo);
        anchorPane.getChildren().add(vButtons);

        Canvas canvasArmyInfo = new Canvas(width * 0.2, HEADER_HEIGHT);
        AnchorPane anchorPane1 = new AnchorPane();
        GraphicsContext gcArmyInfo = canvasArmyInfo.getGraphicsContext2D();
        gcArmyInfo.setFill(Color.ANTIQUEWHITE);
        gcArmyInfo.fillRect(0, 0, gcArmyInfo.getCanvas().getWidth(), gcArmyInfo.getCanvas().getHeight());
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
            selected = game.getTowerOnClick((int)(event.getX() / scale), (int)(event.getY() / scale));
            redrawTowerInfo = true;
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
        double percentage = tower.getCurrentHealth() / tower.getMaxHealth();
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
        healthDataList.add(new HealthData(startX, startY - 6, enemy.getCurrentHealth() / enemy.getMaxHealth()));
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
                if (game.getLives() == 0) {
                    drawGameOver();
                }
            }
            if (game.isHeaderRedraw()) {
                header.setFont(Font.font("Herculanum", 20));
                header.setFill(Color.DARKGREEN);
                header.fillRect(0, 0, header.getCanvas().getWidth(), header.getCanvas().getHeight());
                header.setStroke(Color.BLACK);
                header.strokeRoundRect(2, 2, header.getCanvas().getWidth() - 4, header.getCanvas().getHeight() - 4, 5, 5);
                header.setFill(Color.WHITE);
                header.fillText(String.format("Level: %d", game.getWaveCounter()), 5, 22);
                header.fillText(String.format("Lives: %d", game.getLives()), 5, 48);
                header.fillText(String.format("Gold: %d", game.getBalance()), 150, 22);
                header.fillText(String.format("Killed: %d", game.getKilled()), 150, 48);
                game.setHeaderRedraw(false);
            }
            if (redrawTowerInfo) {
                gcTowerInfo.setFont(Font.font("Herculanum", 15));
                gcTowerInfo.setFill(Color.DARKGREEN);
                gcTowerInfo.fillRect(0, 0, gcTowerInfo.getCanvas().getWidth(), gcTowerInfo.getCanvas().getHeight());
                gcTowerInfo.setStroke(Color.BLACK);
                gcTowerInfo.strokeRoundRect(2, 2, gcTowerInfo.getCanvas().getWidth() - 4, gcTowerInfo.getCanvas().getHeight() - 4, 5, 5);
                gcTowerInfo.setFill(Color.WHITE);
                if (selected != null) {
                    gcTowerInfo.fillText(String.format("Range: %.1f", selected.getFireRange()), 5, 16);
                    gcTowerInfo.fillText(String.format("Power: %.1f", selected.getPower()), 5, 36);
                    gcTowerInfo.fillText(String.format("Armory: %.1f/%.1f", selected.getCurrentHealth(), selected.getMaxHealth()), 5, 55);
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
