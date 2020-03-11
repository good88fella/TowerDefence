package entities;

public class GameMap {

    private int width;
    private int height;
    private char[][] matrix;
    private char[] wayToSuccess;
    private Point start;
    private Point finish;

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        matrix = new char[height][width];
    }

    public static class Point {
        private double x;
        private double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getFinish() {
        return finish;
    }

    public void setFinish(Point finish) {
        this.finish = finish;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public char[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(char[][] matrix) {
        this.matrix = matrix;
    }

    public char[] getWayToSuccess() {
        return wayToSuccess;
    }

    public void setWayToSuccess(char[] wayToSuccess) {
        this.wayToSuccess = wayToSuccess;
    }
}
