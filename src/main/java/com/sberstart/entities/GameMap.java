package com.sberstart.entities;

import com.sberstart.aux.Rect;

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

    public void setFinish(Rect.Point finish) {
        this.finish = finish;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public char[][] getMatrix() {
        return matrix;
    }

    public List<Rect> getFieldsCoordinates() {
        return fieldsCoordinates;
    }

}
