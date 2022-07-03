import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.util.ArrayList;

@Getter
@Setter
@ToString
public class Bee implements Comparable<Bee> {
    private String role;
    private ArrayList<Integer> path; //stores all nodes in each bee, will randomize foragers
    private double distance;
    private double score;
    private int cycle; //number of iterations on current solution
    private int numberOfVehiclesUsed;
    private static int vehicleCapacity;

    static {
        vehicleCapacity = 27;
    }

    public static int getVehicleCapacity() {
        return vehicleCapacity;
    }

    public Bee(String role, ArrayList<Integer> path) {
        this.role = role;
        this.path = path;
        this.distance = Double.MAX_VALUE;
        this.cycle = 0;
        this.numberOfVehiclesUsed = 0;
        this.score = Double.MAX_VALUE;
    }

    @Override
    public int compareTo(Bee o) {
        return Double.compare(score, o.score);
        //return 0;
    }
}
