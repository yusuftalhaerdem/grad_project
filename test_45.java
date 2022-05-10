import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class test_45 {
    public static void main(String[] args) throws FileNotFoundException {

        ArrayList<Double> scores= new ArrayList<Double>();

        ArrayList<String> testList= new ArrayList<>();

        int[] opts = {4};       // check abc 427 for better understanding

        String fileName = "data/optimized_45_city.txt";



        int CycleLimit = Integer.MAX_VALUE;
        CycleLimit = 250000;
        //int printCount = 100;
        int printCycle = 2500;
        int writeCycle = 250000;

        // for loop between different mutation types
        for (int opt = opts[0]; opt<=opts[opts.length-1];opt++) {
/*
            int foragerCycleLimit = 10;
            for ( int forager_clc_opt =0; forager_clc_opt<5;forager_clc_opt++){
                if( forager_clc_opt== 0){
                    foragerCycleLimit = 10;
                }else if( forager_clc_opt== 1){
                    foragerCycleLimit = 50;
                }else if( forager_clc_opt== 2){
                    foragerCycleLimit = 100;
                }else if( forager_clc_opt== 3){
                    foragerCycleLimit = 500;
                }else if( forager_clc_opt== 4){
                    foragerCycleLimit = 1000;
                }
*/
/*
            int population = 100;
            for (int population_option = 0; population_option<5;population_option++){
                if(population_option == 0){
                    population = 100;
                }
                else if(population_option == 1){
                    population = 200;
                }
                else if (population_option== 2){
                    population = 300;
                }
                else if (population_option== 3){
                    population = 500;
                }
                else if (population_option== 4){
                    population = 1000;
                }
                */
            //double foragerPercent = 0.5, onlookerPercent = 0.5; //, 3/7 is the best ratio

            for (int i = 0; i < 10; i++) {
                //Control Parameters
                int population = 100;
                double foragerPercent = 0.5, onlookerPercent = 0.5; //, 3/7 is the best ratio
                //int scoutCount = (int) (population * scoutPercent);  15 69911.963  14 49752.703
                int foragerCycleLimit = 500;
                int cycleLimit = CycleLimit, cycle = 1;
                ArrayList<Double> rolePercent = new ArrayList<>(Arrays.asList(onlookerPercent, foragerPercent));

                //Data source
                FileOperation fileOperation = new FileOperation();
                fileOperation.readCSV(fileName);
                ArrayList<ArrayList<Integer>> dataList = fileOperation.getDataList();


                ArrayList<Double> bestScores = new ArrayList<Double>();
                ArrayList<ArrayList<Integer>> bestPaths = new ArrayList<ArrayList<Integer>>();

                ABC abc = new ABC();
                ArrayList<ArrayList<Double>> table = abc.makeDistanceTable(dataList);
                ArrayList<Bee> hive = abc.initializeHive(population, dataList);
                hive = abc.assignRoles(hive, rolePercent, table, population, dataList);

                while (cycle < cycleLimit) {
                    // forager bee phase
                    ArrayList<Bee> forager_bees_ordered = abc.waggle(hive, table, dataList);

                    // prints forager phase details
                    abc.printDetails(cycle, forager_bees_ordered.get(0).getPath(),
                            forager_bees_ordered.get(0).getDistance(), "F");

                    // onlooker bee phase
                    ArrayList<Bee> onlooker_bees_ordered = abc.recruit(hive, forager_bees_ordered, table, dataList, opt);

                    // prints onlooker phase details
                    abc.printDetails(cycle, onlooker_bees_ordered.get(0).getPath(),
                            onlooker_bees_ordered.get(0).getDistance(), "O");

                    // scout bee phase
                    abc.scoutPhase(hive, foragerCycleLimit, forager_bees_ordered, table, dataList);

                    if (forager_bees_ordered.get(0).getScore() <= onlooker_bees_ordered.get(0).getScore()) {
                        bestPaths.add(forager_bees_ordered.get(0).getPath());
                        bestScores.add(forager_bees_ordered.get(0).getScore());
                    } else {
                        bestPaths.add(onlooker_bees_ordered.get(0).getPath());
                        bestScores.add(onlooker_bees_ordered.get(0).getScore());
                    }

                    cycle++;

                    if (cycle % printCycle == 0) {
                        int currentPrint = (cycle% writeCycle) / printCycle;
                        Collections.sort(bestScores);
                        testList.add(String.valueOf(currentPrint) + " " + String.valueOf(bestScores.get(0)));
                        System.out.println(String.valueOf(currentPrint) +" "+bestScores.get(0));

/*
                        if(cycle % writeCycle == 0) {
                            int writeCycleNo = cycle / writeCycle;
                            String outputFileName =
                                    "opt" + String.valueOf(opt) +
                                            //" foragerCycleLimit"+ String.valueOf(foragerCycleLimit) +
                                            " test"+String.valueOf(i)+
                                            " write_cycle" + String.valueOf(writeCycleNo-1);

                            TestOut.fileWriter(outputFileName, testList);
                            testList.clear();
                            Collections.sort(bestScores);
                            scores.add(bestScores.get(0));
                            // break;
                        }

 */
                    }
                }
                TestOut.fileWriter(
                        "45 opt" + String.valueOf(opt) +
                                " test" + String.valueOf(i)+
                                " foragerPercent"+String.valueOf(foragerPercent)+
                                " foragerCycleLimit"+String.valueOf(foragerCycleLimit)+
                                " population"+String.valueOf(population)+
                                " printCycles"+String.valueOf(printCycle)
                        , testList);
                testList.clear();
                Collections.sort(bestScores);
                scores.add(bestScores.get(0));


                ArrayList<Integer> path = new ArrayList<Integer>();
                path = abc.getStartingPath(dataList);
                double totalDistance = abc.getTotalDistanceOfPath(path, table);
                ArrayList<Integer> pathTaken = new ArrayList<Integer>();

                //System.out.println("0");
            }

        }
        System.out.println("\n\n");
        double total =0;
        for(int i =0;i<scores.size();i++){
            total+= scores.get(i);
            System.out.print(i);
            System.out.println("th result is= "+ scores.get(i));
        }
        double average = total / scores.size();
        System.out.println("average of those values are: "+ average);
    }

    /*
    private static void tsp_god(ArrayList<ArrayList<Double>> table, ArrayList<Integer> path, double distance, ArrayList<Integer> pathTaken) {

        for(int i=0; i<path.size();i++){
            pathTaken.add(path.remove(i));
            tsp_god(table, path,distance,pathTaken);
        }

    }*/
}
