import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PetriDish {
    private final int[] dimension;
    private Object[][] matrix;
    private List<Bacteria> bacteriaList = new ArrayList<>();
    private List<FoodUnit> foodUnitList = new ArrayList<>();

    public PetriDish(int[] dimension) {
        this.dimension = dimension;
        this.matrix = new Object[dimension[0]][dimension[1]];
        initializeMap();
    }

    private void initializeMap() {
        for (int i = 0; i < dimension[0]; i++) {
            for (int j = 0; j < dimension[1]; j++) {
                matrix[i][j] = null;
            }
        }
    }

    public void spawnFoodUnit(int numberOfFoodUnits) {
        Random random = new Random();
        for (int i = 0; i < numberOfFoodUnits; i++) {
            int x = random.nextInt(dimension[0]);
            int y = random.nextInt(dimension[1]);

            FoodUnit newFoodUnit = new FoodUnit(x, y, 5);
            matrix[x][y] = newFoodUnit;
            foodUnitList.add(newFoodUnit);

            System.out.println("New food unit spawned at (" + x + ", " + y + ")");
        }
    }

    public List<FoodUnit> getFoodUnits() {
        return foodUnitList;
    }

    public void printMatrix() {
        for (int i = 0; i < dimension[0]; i++) {
            for (int j = 0; j < dimension[1]; j++) {
                if (matrix[i][j] == null) {
                    System.out.print("null ");
                } else {
                    System.out.print(matrix[i][j].toString() + " ");
                }
            }
            System.out.println();
        }
    }
}
