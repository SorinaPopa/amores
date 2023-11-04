import java.util.Timer;
import java.util.TimerTask;

public class Bacteria {
    private String sexuality;
    private int x, y;//positions
    private int T_full, T_starve;
    private int eat_counter;
    private Timer timer;

    public Bacteria(String sexuality, int x, int y) {
        this.sexuality = sexuality;
        this.x = x;
        this.y = y;
        this.eat_counter = 0;
        T_full = 5;
        T_starve = 5;
        this.timer = new Timer();
        Start();
    }

    public void Start() {
        this.StartHungerTimer();
    }

    public void StartHungerTimer(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (T_full > 0) {
                    T_full--;
                    System.out.println("T_full " + T_full);
                } else {
                    if (T_starve > 0) {
                        T_starve--;
                        System.out.println("T_starve " + T_starve);
                    }
                    else {
                        Die();
                        timer.cancel();
                    }
                }
            }
        }, 0, 1000);
    }

    private void Die() {
        System.out.println("the bacteria died");
    }

    public void SeekAndConsume(){
        int[] currentPosition = new int[] {this.x, this.y};
        int[] foodPosition = {0, 0};

        // move the bacteria towards the closest food unit
        while (!currentPosition.equals(foodPosition)) {
            int nextX = currentPosition[0];
            int nextY = currentPosition[1];

            // Move towards the target (food position)
            if (nextX < foodPosition[0]) {
                nextX++;
            } else if (nextX > foodPosition[0]) {
                nextX--;
            } else if (nextY < foodPosition[1]) {
                nextY++;
            } else if (nextY > foodPosition[1]) {
                nextY--;
            }
            currentPosition = new int[] {nextX, nextY};
        }

        // consume the food unit
        this.eat_counter++;
        if(eat_counter == 10) {
            Multiply();
        }
        // delete food unit from map
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
}
