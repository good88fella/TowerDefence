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
import utils.Rect;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class MainApp extends Application {
    private double scale;
    private GraphicsContext gc;
    private GraphicsContext header;
    private Image explosion;
    private Image road;
    private Image field;
    private Queue<ExplosionAnimation> explosionAnimations = new LinkedList<>();
    private final static int MAX_FRAMES_EXPL = 48;
    private final static int ANIM_SPRITE_ROWS = 6;
    private final static int ANIM_SPRITE_COLS = 8;
    private final static int ANIM_FRAME_WIDTH = 256;
    private final static int ANIM_FRAME_HEIGHT = 256;
    private final static int NANOSEC_PER_FRAME = 20_000;
    private final static double HEADER_HEIGHT = 50;

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

    private void DrawFence(Rect rect) {
        gc.setStroke(Color.BROWN);
        gc.strokeRect(rect.getStart().getX() * scale,
                rect.getStart().getY() * scale,
                (rect.getEnd().getX() - rect.getStart().getX()) * scale + scale,
                (rect.getEnd().getY() - rect.getStart().getY()) * scale + scale
                );
    }

    private void DrawHealth(double x, double y, double percentage) {
        double width = scale * 0.85;
        double startx = (scale - width) / 2 + x;
        gc.setFill(Color.DARKGRAY);
        gc.fillRoundRect(startx, y, width, 4, 2, 2);
        gc.setFill(percentage > 0.35 ? Color.GREEN : Color.RED);
        gc.fillRoundRect(startx, y, width * percentage, 4, 2, 2);
    }

    private void DrawTower(Tower tower, long now) {
        double startX = tower.getX() * scale;
        double startY = tower.getY() * scale;
        gc.setFill(Color.DARKKHAKI);
        gc.fillRoundRect(startX, startY, scale, scale, scale / 4, scale / 4);
        gc.setFill(Color.KHAKI);
        double r = scale * 0.9;
        gc.fillOval(startX + (scale - r) / 2 , startY + (scale - r) / 2, r, r);
        gc.save();
        gc.setFill(Color.BLACK);
        double barrelLength = scale * 0.8;
        gc.translate(startX + scale / 2, startY + scale / 2);
        gc.rotate(tower.getAngle() + 90);
        gc.fillRect(-2, 0, 3, barrelLength);
        gc.restore();
        double percentage = tower.getCurrentHealth() / tower.getMaxHealth();
        DrawHealth(startX, startY - 6, percentage);
        double angleRad = Math.toRadians(tower.getAngle() + 180);
        if (tower.isShooting()) {
            explosionAnimations.add(new ExplosionAnimation(startX + scale / 2 + barrelLength * Math.cos(angleRad),
                    startY + scale / 2 + barrelLength * Math.sin(angleRad), now));
            tower.setShooting(false);
        }
    }

    private void DrawEnemy(Enemy enemy) {
        gc.setFill(Color.CHOCOLATE);
        gc.fillOval(enemy.getX() * scale, enemy.getY() * scale, scale, scale);
        DrawHealth(enemy.getX() * scale, enemy.getY() * scale - 6,
                enemy.getCurrentHealth() / enemy.getMaxHealth());
    }

    private void DrawField(long now) {
       // DrawGrid();
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
        for (Rect rect : Game.game.getGameMap().getFieldsCoordinates()) {
            DrawFence(rect);
        }
        for (Enemy enemy : Game.game.getEnemies()) {
            DrawEnemy(enemy);
        }
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

        }
    };

    public static void main(String[] args) {
        Game.game = new Game();
        Thread thread = new Thread(() -> {
            try {
                Game.game.runGame();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        launch();
    }
}
