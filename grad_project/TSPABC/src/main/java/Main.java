import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        //Control Parameters
        int population = 180;
        double foragerPercent = 0.5, onlookerPercent= 0.5; //, scoutPercent= 0.2;
        //int scoutCount = (int) (population * scoutPercent);
        int foragerCycleLimit = 5, cycleLimit = 300, cycle = 1;
        ArrayList<Double> rolePercent = new ArrayList<>(Arrays.asList(onlookerPercent, foragerPercent));

        //Data source
        FileOperation fileOperation = new FileOperation();
        fileOperation.readCSV("data/city_values_10.txt");
        ArrayList<ArrayList<Integer>> dataList= fileOperation.getDataList();


        ArrayList<Double> bestScores = new ArrayList<Double>();
        ArrayList<ArrayList<Integer>> bestPaths = new ArrayList<ArrayList<Integer>>();

        ABC abc = new ABC();
        ArrayList<ArrayList<Double>> table = abc.makeDistanceTable(dataList);
        ArrayList<Bee> hive = abc.initializeHive(population, dataList);
        hive = abc.assignRoles(hive, rolePercent, table, population, dataList);

        while (cycle<cycleLimit){
            // forager bee phase
            ArrayList<Bee> forager_bees_ordered = abc.waggle(hive, table, dataList);

            abc.printDetails(cycle, forager_bees_ordered.get(0).getPath(),
                    forager_bees_ordered.get(0).getDistance(),"F");

            // onlooker bee phase
            ArrayList<Bee> onlooker_bees_ordered = abc.recruit(hive, forager_bees_ordered, table);

            abc.printDetails(cycle, onlooker_bees_ordered.get(0).getPath(),
                    onlooker_bees_ordered.get(0).getDistance(),"O");

            // scout bee phase
            abc.scoutPhase(hive, foragerCycleLimit, forager_bees_ordered, table, dataList);

            if(forager_bees_ordered.get(0).getScore() <= onlooker_bees_ordered.get(0).getScore()){
                bestPaths.add(forager_bees_ordered.get(0).getPath());
                bestScores.add(forager_bees_ordered.get(0).getScore());
            }else{
                bestPaths.add(onlooker_bees_ordered.get(0).getPath());
                bestScores.add(onlooker_bees_ordered.get(0).getScore());
            }

            cycle++;
        }
        Collections.sort(bestScores);
        System.out.println("debug");
    }
}
