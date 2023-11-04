public class Bacteria {
    private String sexuality;
    private int x, y;//positions
    private int T_full, T_starve;
    private int eat_counter;

    public Bacteria(String sexuality, int x, int y) {
        this.sexuality = sexuality;
        this.x = x;
        this.y = y;
        this.eat_counter = 0;
    }

    public void Eating() {
        eat_counter++;
    }

    public void Multiply() {
        if (this.IsAsexual()) {
            //duplicates
        } else {
            //find mate
        }
    }

    public void Die() {
        //food units
    }

    public Boolean IsAsexual() {
        return this.sexuality.equals("asexual");
    }
}
