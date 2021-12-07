import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;



public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        //Control Parameters
        int population = 180;
        double foragerPercent = 0.5, onlookerPercent= 0.5, scoutPercent= 0.2;
        int scoutCount = (int) (population * scoutPercent);
        int foragerLimit = 500, cycleLimit = 300, cycle = 1;
        ArrayList<Double> rolePercent = new ArrayList<>(Arrays.asList(onlookerPercent, foragerPercent));

        //Data source
        FileOperation fileOperation = new FileOperation();
        fileOperation.readCSV("data/data_12.csv");
        ArrayList<ArrayList<Integer>> dataList= fileOperation.getDataList();

        //Global variables
        double bestDistance = Double.MAX_VALUE;
        ArrayList<Integer> bestPath = new ArrayList<Integer>();
        //result

        ABC abc = new ABC();
        ArrayList<ArrayList<Double>> table = abc.makeDistanceTable(dataList);
        ArrayList<Bee> hive = abc.initializeHive(population, dataList);
        hive = abc.assignRoles(hive, rolePercent, table, population);

        while (cycle<cycleLimit){
            ReturnValues returnValues = abc.waggle(hive, bestDistance, table, foragerLimit, scoutCount);
            if(returnValues.getBestDistance()<bestDistance){
                bestDistance = returnValues.getBestDistance();
                bestPath = new ArrayList<Integer>(returnValues.getBestPath());
                abc.printDetails(cycle, bestPath, bestDistance,"F");
            }

            ReturnValues onlookerReturnValues = abc.recruit(hive, bestDistance, bestPath, table);
            if(onlookerReturnValues.getBestDistance()<bestDistance){
                bestDistance = onlookerReturnValues.getBestDistance();
                bestPath = new ArrayList<Integer>(onlookerReturnValues.getBestPath());
                abc.printDetails(cycle, bestPath, bestDistance,"O");
            }
            cycle++;
        }
    }
}
