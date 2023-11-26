public class Main {
    public static void main(String[] args) {
        //set how big the petri dish is
        PetriDish petriDish = new PetriDish(new int[]{10, 10});
        petriDish.spawnFoodUnit(5);
        //x and y to be random positions on the petri dish
        Bacteria mara = new Bacteria("sexual", 3, 5);
        System.out.println(mara.IsAsexual());
    }
}