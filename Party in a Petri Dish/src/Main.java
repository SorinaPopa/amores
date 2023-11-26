import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        //set how big the petri dish is
        PetriDish petriDish = new PetriDish(new int[]{10, 10});
        petriDish.spawnFoodUnit(5);
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                10,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );

        executor.submit(new Bacteria("sexual", 3, 4, petriDish));

        executor.shutdown();
    }
}