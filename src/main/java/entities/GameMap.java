package entities;

import utils.Rect;

import java.util.ArrayList;
import java.util.List;

public class GameMap {

    private int width;
    private int height;
    private char[][] matrix;
    private Rect.Point start;
    private Rect.Point finish;
    private List<Rect> fieldsCoordinates;

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        matrix = new char[height][width];
        fieldsCoordinates = new ArrayList<>();
    }

    public void draw() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

    public void fillMap() {
        fieldsCoordinates.add(new Rect(new Rect.Point(0, 0), new Rect.Point(49, 0)));
        fieldsCoordinates.add(new Rect(new Rect.Point(0, 24), new Rect.Point(49, 24)));
        fieldsCoordinates.add(new Rect(new Rect.Point(0, 2), new Rect.Point(0, 23)));
        fieldsCoordinates.add(new Rect(new Rect.Point(49, 2), new Rect.Point(49, 23)));

        fieldsCoordinates.add(new Rect(new Rect.Point(2, 1), new Rect.Point(9, 22)));
        fieldsCoordinates.add(new Rect(new Rect.Point(11, 2), new Rect.Point(19, 23)));
        fieldsCoordinates.add(new Rect(new Rect.Point(21, 1), new Rect.Point(27, 22)));
        fieldsCoordinates.add(new Rect(new Rect.Point(29, 2), new Rect.Point(37, 23)));
        fieldsCoordinates.add(new Rect(new Rect.Point(39, 1), new Rect.Point(47, 22)));
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++)
                matrix[i][j] = '#';
        }
        for (Rect rect : fieldsCoordinates) {
            for (int i = (int) rect.getStart().getY(); i <= rect.getEnd().getY(); i++) {
                for (int j = (int) rect.getStart().getX(); j <= rect.getEnd().getX(); j++) {
                    matrix[i][j] = '.';
                }
            }
        }
    }

    public Rect.Point getStart() {
        return start;
    }

    public void setStart(Rect.Point start) {
        this.start = start;
    }

    public Rect.Point getFinish() {
        return finish;
    }

    public void setFinish(Rect.Point finish) {
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

    public List<Rect> getFieldsCoordinates() {
        return fieldsCoordinates;
    }

    public void setFieldsCoordinates(List<Rect> fieldsCoordinates) {
        this.fieldsCoordinates = fieldsCoordinates;
    }
}
