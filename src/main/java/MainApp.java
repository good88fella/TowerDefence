import entities.Enemy;
import entities.Tower;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import utils.Rect;
import view.ShowFire;

public class MainApp extends Application implements ShowFire {
    private double scale;
    private GraphicsContext gc;

    private void addLines(double width, double height, Group group) {
        for (int i = 0; i <= height; i++) {
            Line line = new Line(0, i * scale, width * scale, i * scale);
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
            gc.clearRect(100, 80, 300, 50);
            gc.fillText(String.format("x: %f, y: %f", event.getX(), event.getY()), 100 ,100);
            Game.game.createTower((int)(event.getX() / scale), (int)(event.getY() / scale));
        });
        Group root = new Group();
        addLines(width, height, root);
        root.getChildren().add(canvas);

        DrawField();


       // Image image = new Image("explosion.png");
       // final ImageView imageView = new ImageView(image);




        Scene scene = new Scene(root);
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

    private void DrawTower(Tower tower) {
        double startX = tower.getX() * scale;
        double startY = tower.getY() * scale;
        gc.setFill(Color.DARKKHAKI);
        gc.fillRoundRect(startX, startY, scale, scale, scale / 4, scale / 4);
        gc.setFill(Color.KHAKI);
        double r = scale * 0.9;
        gc.fillOval(startX + (scale - r) / 2 , startY + (scale - r) / 2, r, r);
        gc.save();
        gc.setFill(Color.BLACK);
        gc.translate(startX + scale / 2, startY + scale / 2);
        gc.rotate(tower.getAngle() + 90);
        gc.fillRect(-2, 0, 3, scale * 0.8);
        gc.restore();
        double percentage = tower.getCurrentHealth() / tower.getMaxHealth();
        DrawHealth(startX, startY - 6, percentage);
    }

    private void DrawEnemy(Enemy enemy) {
        gc.setFill(Color.CHOCOLATE);
        gc.fillOval(enemy.getX() * scale, enemy.getY() * scale, scale, scale);
        DrawHealth(enemy.getX() * scale, enemy.getY() * scale - 6,
                enemy.getCurrentHealth() / enemy.getMaxHealth());
    }

    private void DrawField() {
       // DrawGrid();
        for (Tower tower : Game.game.getTowers()) {
            DrawTower(tower);
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
                DrawField();
                Game.game.setNeedRedraw(false);
            }
        }
    };

    public static void main(String[] args) {
        Game.game = new Game();
        launch();
    }

    @Override
    public void showFire(double x, double y, double targetX, double targetY) {

    }
}
