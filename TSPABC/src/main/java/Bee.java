import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.util.ArrayList;

@Getter
@Setter
@ToString
public class Bee {
    private String role;
    private ArrayList<Integer> path; //stores all nodes in each bee, will randomize foragers
    private double distance;
    private int cycle; //number of iterations on current solution

    public Bee(String role, ArrayList<Integer> path, int distance, int cycle) {
        this.role = role;
        this.path = path;
        this.distance = distance;
        this.cycle = cycle;
    }
}
