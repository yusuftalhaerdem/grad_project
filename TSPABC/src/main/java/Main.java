import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        ArrayList<Double> scores= new ArrayList<Double>();

        ArrayList<String> testList= new ArrayList<>();
        //ArrayList<Integer> opts_list = new ArrayList<Integer>();

        // 11: 41714.906  12: 43642.979  13: 50327.554  14: 49752.703
        /*
        double best_score_10_nodes = 1;
        double best_score_11_nodes = 1;
        double best_score_12_nodes = 1;
        double best_score_13_nodes = 1;
        double best_score_14_nodes = 1;
        double best_score_15_nodes = 1;

         */

        int[] opts = {5};       // check abc 427 for better understanding

        String fileName = "data/optimized_31_city.txt";



        int CycleLimit = Integer.MAX_VALUE;
        CycleLimit = 250000;
        //int printCount = 100;
        int printCycle = 2500;
        int writeCycle = 250000;

        // for loop between different mutation types
        for (int opt: opts) {

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
                    }
                }
                TestOut.fileWriter(
                        "opt" + String.valueOf(opt) +
                        " test" + String.valueOf(i)+
                        " foragerPercent"+String.valueOf(foragerPercent)+
                        " foragerCycleLimit"+String.valueOf(foragerCycleLimit)+
                        " population"+String.valueOf(population)+
                        " printCycles"+String.valueOf(printCycle)+
                        " file_name"+String.valueOf(fileName)
                        , testList);
                testList.clear();
                Collections.sort(bestScores);
                scores.add(bestScores.get(0));


                ArrayList<Integer> path = new ArrayList<Integer>();
                path = abc.getStartingPath(dataList);
                double totalDistance = abc.getTotalDistanceOfPath(path, table);
                ArrayList<Integer> pathTaken = new ArrayList<Integer>();

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

}
