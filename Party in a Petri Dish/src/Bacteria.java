import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Bacteria implements Runnable {
    private String sexuality;
    private int x, y;//positions
    private int moveX;
    private int moveY;
    private int T_full, T_starve;
    private int eat_counter;
    private Timer timer;

    public Bacteria(String sexuality, int x, int y) {
        this.sexuality = sexuality;
        this.x = x;
        this.y = y;
        this.moveX = 0;
        this.moveY = 0;
        this.eat_counter = 0;
        T_full = 5;
        T_starve = 5;
        this.timer = new Timer();

    }

    public void run() {
        this.StartHungerTimer();
    }

    public void StartHungerTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (T_full > 0) {
                    T_full--;
                    System.out.println(this.toString() + " T_full " + T_full);
                } else {
                    if (T_starve > 0) {
                        T_starve--;
                        System.out.println(this.toString() + " T_starve " + T_starve);
                    } else {
                        Die();
                        timer.cancel();
                    }
                }
            }
        }, 0, 1000);
    }

    private void Die() {
        System.out.println(this.toString() + " died");
    }

    public void seekAndConsume(PetriDish map) {
        List<FoodUnit> foodUnits = map.getFoodUnits();
        int[] currentPosition = new int[]{this.x, this.y};

        // Verificăm dacă există unități de hrană
        if (!foodUnits.isEmpty()) {
            FoodUnit nearestFoodUnit = findNearestFoodUnit(currentPosition, foodUnits);

            if (nearestFoodUnit != null) {
                int[] targetPosition = nearestFoodUnit.getPosition();

                // Deplasăm bacteria către cea mai apropiată unitate de hrană
                moveTowards(targetPosition, map);
            }
        }
    }

    private FoodUnit findNearestFoodUnit(int[] currentPosition, List<FoodUnit> foodUnits) {
        FoodUnit nearestFoodUnit = null;
        double minDistance = Double.MAX_VALUE;

        for (FoodUnit foodUnit : foodUnits) {
            int[] foodUnitPosition = foodUnit.getPosition();
            double distance = calculateDistance(currentPosition, foodUnitPosition);

            if (distance < minDistance) {
                minDistance = distance;
                nearestFoodUnit = foodUnit;
            }
        }

        return nearestFoodUnit;
    }

    private double calculateDistance(int[] position1, int[] position2) {
        int deltaX = position2[0] - position1[0];
        int deltaY = position2[1] - position1[1];
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    private void moveTowards(int[] targetPosition, PetriDish map) {
        int currentX = this.x;
        int currentY = this.y;

        int targetX = targetPosition[0];
        int targetY = targetPosition[1];

        // Calculăm diferența pe axa X și pe axa Y
        int deltaX = targetX - currentX;
        int deltaY = targetY - currentY;

        // Verificăm dacă suntem deja la destinație
        if (deltaX == 0 && deltaY == 0) {
            System.out.println("Bacteria reached the target!");
            return;
        }

        // Alegem direcția în care să ne deplasăm
        moveX = (deltaX > 0) ? 1 : (deltaX < 0) ? -1 : 0;
        moveY = (deltaY > 0) ? 1 : (deltaY < 0) ? -1 : 0;

        // Actualizăm poziția bacteriei
        this.x += moveX;
        this.y += moveY;

        // Apelăm metoda updateMap din clasa Map pentru a actualiza matricea
        map.updateMap(this);

        System.out.println("Bacteria moved towards the target. New position: (" + this.x + ", " + this.y + ")");
    }


    private void FindNearestFoodUnit(int[] currentPosition) {

    }

    public void Multiply() {
        if (this.IsAsexual()) {
            //duplicates
        } else {
            //find mate
        }
    }

    public Boolean IsAsexual() {
        return this.sexuality.equals("asexual");
    }

    public int getMoveX() {
        return moveX;
    }

    public int getMoveY() {
        return moveY;
    }

    public int[] getPosition() {
        return new int[]{this.x, this.y};
    }
}

