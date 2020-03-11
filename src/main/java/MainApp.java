import entities.Tower;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import view.ShowFire;

public class MainApp extends Application implements ShowFire {
    private double scale;
    private GraphicsContext gc;

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
            Game.game.createTower(event.getX() / scale, event.getY() / scale);
        });
        gc.setFill(Color.BLACK);
        gc.fillRect(10, 10, 100, 100);
        Group root = new Group();

        root.getChildren().add(canvas);

        DrawField();


        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        at.start();
    }

    private void DrawFence() {

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
        gc.setFill(Color.DARKGRAY);
        gc.fillRoundRect(startX, startY - 6, scale, 4, 2, 2);
        gc.setFill(percentage > 0.35 ? Color.GREEN : Color.RED);
        gc.fillRoundRect(startX, startY - 6, scale * percentage, 4, 2, 2);
    }

    private void DrawField() {
        double width = Game.game.getGameMap().getWidth();
        double height = Game.game.getGameMap().getHeight();
        gc.setLineWidth(1);
        gc.setStroke(Color.LIGHTGREY);
        gc.setFill(Color.WHITE);
        gc.fillRect(0,0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        for (int i = 0; i <= height; i++) {
            gc.moveTo(0, i * scale);
            gc.lineTo(gc.getCanvas().getWidth(), i * scale);
            gc.stroke();
        }
        for (int j = 0; j <= width; j++) {
            gc.moveTo(j * scale, 0);
            gc.lineTo(j * scale, gc.getCanvas().getHeight());
            gc.stroke();
        }
        for (Tower tower : Game.game.getTowers()) {
            DrawTower(tower);
        }
    }

    protected AnimationTimer at = new AnimationTimer(){
        @Override
        public void handle(long now) {
            if (Game.game.isGameOver()) {
                at.stop();
                return;
            }
            gc.clearRect(100, 200, 300, 50);
            gc.fillText(String.format("x: %d", now), 100 ,250);
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
