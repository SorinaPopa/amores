package org.example;

public class FoodUnit {
    private int x, y, tFull;  // Food unit's position
    private boolean isEaten = false;

    public FoodUnit(int x, int y, int tFull) {
        this.x = x;
        this.y = y;
        this.tFull = tFull;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isEaten() {
        return isEaten;
    }

    public void markAsEaten() {
        isEaten = true;
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

    public int gettFull() {
        return tFull;
    }
}
