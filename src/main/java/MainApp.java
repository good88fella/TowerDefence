import entities.Enemy;
import entities.Tower;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import utils.GameObject;
import utils.Orientation;
import utils.Rect;

import java.util.*;

public class MainApp extends Application {
    private double scale;
    private GraphicsContext gc;
    private GraphicsContext header;
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
    private final static double HEADER_HEIGHT = 50;
    private static Thread thread;

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

    @Override
    public void start(Stage primaryStage) throws Exception {
        Game game = Game.game;

        double width = game.getGameMap().getWidth();
        double height = game.getGameMap().getHeight();
        this.scale = Math.min(1500.0 / width, 1500.0 / height);

        Canvas canvas = new Canvas(width * scale, height * scale);
        gc = canvas.getGraphicsContext2D();
        canvas.setOnMousePressed(event -> {
            Game.game.createTower((int)(event.getX() / scale), (int)(event.getY() / scale));
        });
        VBox vBox = new VBox();
        Canvas canvas1 = new Canvas(width * scale, 50);
        header = canvas1.getGraphicsContext2D();
        
        vBox.getChildren().add(canvas1);
        vBox.getChildren().add(canvas);

        explosion = new Image("explosion.png");
        road = new Image("road.jpg");
        field = new Image("field.jpg");

        Scene scene = new Scene(vBox);
        primaryStage.setTitle("Tower Defence");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        at.start();
    }

    private void DrawHealth(double x, double y, double percentage) {
        double width = scale * 0.85;
        double startx = (scale - width) / 2 + x;
        gc.setFill(Color.DARKGRAY);
        gc.fillRoundRect(startx, y, width, 4, 2, 2);
        gc.setFill(percentage > 0.35 ? Color.GREEN : Color.RED);
        gc.fillRoundRect(startx, y, width * percentage, 4, 2, 2);
    }

    private void DrawBarrel(double startX, double startY, double barrelLength, double angle) {
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

    private void DrawTower(Tower tower, long now) {
        double startX = tower.getX() * scale;
        double startY = tower.getY() * scale;
        gc.setFill(Color.DARKKHAKI);
        gc.fillRoundRect(startX, startY, scale, scale, scale / 4, scale / 4);
        gc.setFill(Color.KHAKI);
        double r = scale * 0.9;
        gc.fillOval(startX + (scale - r) / 2 , startY + (scale - r) / 2, r, r);
        double barrelLength = scale * 0.8;
        DrawBarrel(startX, startY, barrelLength, tower.getAngle());
        double percentage = tower.getCurrentHealth() / tower.getMaxHealth();
        healthDataList.add(new HealthData(startX, startY - 6, percentage));
        addToExploseAnim(tower, startX, startY, barrelLength, now);
    }

    private void DrawWheels(Orientation orientation, double startX, double startY) {
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

    private void DrawEnemy(Enemy enemy, long now) {
        double startX = enemy.getX() * scale;
        double startY = enemy.getY() * scale;
        gc.setFill(Color.BROWN);
        double towerDiameter = scale * 0.7;
        gc.fillOval(startX + (scale - towerDiameter) / 2,
                startY + (scale - towerDiameter) / 2, towerDiameter, towerDiameter);
        if (enemy.getPrevPoint() == null)
            DrawWheels(Orientation.HORIZONTAL, startX, startY);
        else {
            Rect.Point prevPoint = enemy.getPrevPoint();
            if (prevPoint.getX() == enemy.getX()) {
                DrawWheels(Orientation.VERTICAL, startX, startY);
            } else {
                DrawWheels(Orientation.HORIZONTAL, startX, startY);
            }
        }
        double barrelLength = scale * 0.7;
        DrawBarrel(startX, startY, barrelLength, enemy.getAngle());
        healthDataList.add(new HealthData(startX, startY - 6, enemy.getCurrentHealth() / enemy.getMaxHealth()));
        addToExploseAnim(enemy, startX, startY, barrelLength, now);
    }

    private void DrawField(long now) {
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        char[][] matrix = Game.game.getGameMap().getMatrix();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                gc.drawImage(matrix[i][j] == '#' ? road : field, 0, 0, 200, 200, j * scale, i * scale, scale, scale);
            }
        }

        for (Tower tower : Game.game.getTowers()) {
            DrawTower(tower, now);
        }
        for (Enemy enemy : Game.game.getEnemies()) {
            DrawEnemy(enemy, now);
        }
        for (HealthData healthData : healthDataList) {
            DrawHealth(healthData.x, healthData.y, healthData.percentage);
        }
        healthDataList.clear();
    }

    protected AnimationTimer at = new AnimationTimer(){
        @Override
        public void handle(long now) {
            if (Game.game.isGameOver()) {
                at.stop();
                return;
            }
            if (Game.game.isNeedRedraw()) {
                DrawField(now);
                Game.game.setNeedRedraw(false);
                Iterator<ExplosionAnimation> iterExplAnim = explosionAnimations.iterator();
                while (iterExplAnim.hasNext()) {
                    ExplosionAnimation explAnim = iterExplAnim.next();
                    if (explAnim.stage >= MAX_FRAMES_EXPL) {
                        iterExplAnim.remove();
                        Game.game.setNeedRedraw(true);
                    }
                    else {
                        if (now - explAnim.now >= NANOSEC_PER_FRAME) {
                            int startX = explAnim.stage % ANIM_SPRITE_COLS;
                            int startY = explAnim.stage / ANIM_SPRITE_COLS;
                            gc.drawImage(explosion, startX, startY, ANIM_FRAME_WIDTH, ANIM_FRAME_HEIGHT,
                                    explAnim.x - scale , explAnim.y - scale , scale * 2 , scale * 2);
                            explAnim.now = now;
                            explAnim.stage++;
                            Game.game.setNeedRedraw(true);
                        }
                    }
                }
            }
            if (Game.game.isHeaderRedraw()) {
                header.setFill(Color.DARKGREEN);
                header.fillRect(0, 0, header.getCanvas().getWidth(), header.getCanvas().getHeight());
                header.setFill(Color.WHITE);
                header.fillText(String.format("Level: %d", Game.game.getWaveCounter()), 5, 20);
                header.fillText(String.format("Lives: %d", Game.game.getLives()), 5, 45);
                header.fillText(String.format("Gold: %d", Game.game.getBalance()), 100, 20);
                header.fillText(String.format("Killed: %d", Game.game.getKilled()), 100, 45);
                Game.game.setHeaderRedraw(false);
            }
        }
    };

    public static void main(String[] args) {
        Game.game = new Game();
        thread = new Thread(() -> {
            try {
                Game.game.runGame();
            } catch (InterruptedException ignored) {   }
        });
        thread.start();
        launch();
    }
}
