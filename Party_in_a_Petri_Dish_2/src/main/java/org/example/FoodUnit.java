package org.example;

public class FoodUnit {
    private final int x, y, tFull;

    public FoodUnit(int x, int y, int tFull) {
        this.x = x;
        this.y = y;
        this.tFull = tFull;
    }

    public int[] getPosition() {
        return new int[]{this.x, this.y};
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
