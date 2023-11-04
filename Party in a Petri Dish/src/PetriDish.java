import java.util.ArrayList;
import java.util.List;

public class PetriDish {
    private int[] dimension;
    private List<Bacteria> bacterias=new ArrayList<>();
    //private FoodUnit[] foodUnits=new ArrayList<>();
    public PetriDish(int[] dimension){
        this.dimension=dimension;

    }

    public void Populate(Object obj){
        if(obj instanceof Bacteria){
            bacterias.add((Bacteria) obj);
        }
        /*else if (obj instanceof FoodUnit) {
            foodUnits.add((FoodUnit) obj);
        }*/

    }
}
