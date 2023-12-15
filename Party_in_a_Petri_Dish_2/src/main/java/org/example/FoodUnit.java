package org.example;

import java.util.concurrent.locks.ReentrantLock;

public class FoodUnit {
    private final int x, y, tFull;

    private final ReentrantLock lock = new ReentrantLock();

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

    public ReentrantLock getLock() {
        return lock;
    }
}
