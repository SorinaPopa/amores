import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PetriDish {
    private int[] dimension;
    private int[][] matrix;
    private List<Bacteria> bacteriaList = new ArrayList<>();
    private List<FoodUnit> foodUnitList = new ArrayList<>();

    public PetriDish(int[] dimension) {
        this.dimension = dimension;
        this.matrix = new int[dimension[0]][dimension[1]];
    }

    public void update() {
        // Update the state of the Petri Dish over time
        // This can include spawning new food units, updating bacteria positions, etc.

        // For simplicity, let's spawn a new food unit randomly
        spawnFoodUnit();
    }

    private void spawnFoodUnit() {
        Random random = new Random();
        int x = random.nextInt(dimension[0]);
        int y = random.nextInt(dimension[1]);

        FoodUnit newFoodUnit = new FoodUnit(x, y,5);
        foodUnitList.add(newFoodUnit);

        System.out.println("New food unit spawned at (" + x + ", " + y + ")");
    }

    public List<FoodUnit> getFoodUnits() {
        return foodUnitList;
    }

    public void Populate(Object obj) {
        if (obj instanceof Bacteria) {
            bacteriaList.add((Bacteria) obj);
        }
        /*else if (obj instanceof FoodUnit) {
            foodUnits.add((FoodUnit) obj);
        }*/

    }
}
