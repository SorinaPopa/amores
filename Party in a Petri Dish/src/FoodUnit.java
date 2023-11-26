public class FoodUnit {
    private int x, y;  // Food unit's position
    private boolean isEaten = false;


    public FoodUnit(int x, int y, int tFull) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
}
