import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ReturnValues {
    private double bestDistance;
    private ArrayList<Integer> bestPath;

    public ReturnValues(double bestDistance, ArrayList<Integer> bestPath) {
        this.bestDistance = bestDistance;
        this.bestPath =bestPath;
    }
}
