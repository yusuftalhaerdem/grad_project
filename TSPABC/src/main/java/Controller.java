import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Controller {
    public static void wishing(int[] opts, int file_no, int printCycle, int cycleLimit, int population,
                               double foragerPercentage,int foragerCycleLimit) throws FileNotFoundException {

        ArrayList<Double> scores= new ArrayList<Double>();
        ArrayList<String> testList= new ArrayList<>();


        String fileName = "data/optimized_"+String.valueOf(file_no)+"_city.txt";

        double  onlookerPercent = 1-foragerPercentage; //, 3/7 is the best ratio


        //int printCount = 100;
        int writeCycle = 250000;

        // for loop between different mutation types
        for (int opt : opts) {

            for (int i = 0; i < 10; i++) {
                //Control Parameters
                //int scoutCount = (int) (population * scoutPercent);  15 69911.963  14 49752.703
                int cycle = 1;

                ArrayList<Double> rolePercent = new ArrayList<>(Arrays.asList(onlookerPercent, foragerPercentage));

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
                        bestPaths.add(0,forager_bees_ordered.get(0).getPath());
                        bestScores.add(forager_bees_ordered.get(0).getScore());
                    } else {
                        bestPaths.add(0,onlooker_bees_ordered.get(0).getPath());
                        bestScores.add(onlooker_bees_ordered.get(0).getScore());
                    }

                    cycle++;

                    if (cycle % printCycle == 0) {
                        int currentPrint = (cycle% writeCycle) / printCycle;
                        Collections.sort(bestScores);
                        testList.add(String.valueOf(currentPrint) + " " + String.valueOf(bestScores.get(0)));
                        System.out.println(String.valueOf(currentPrint) +" "+bestScores.get(0));

                    }
                }
                TestOut.fileWriter(
                        String.valueOf(file_no)+" opt" + String.valueOf(opt) +
                                " test" + String.valueOf(i)+
                                " foragerPercent"+String.valueOf(foragerPercentage)+
                                " foragerCycleLimit"+String.valueOf(foragerCycleLimit)+
                                " population"+String.valueOf(population)+
                                " printCycles"+String.valueOf(printCycle)
                        , testList);
                testList.clear();
                Collections.sort(bestScores);
                scores.add(bestScores.get(0));


                TestOut.pathWriter(String.valueOf(file_no)+"path opt" + String.valueOf(opt) +
                        " test" + String.valueOf(i)+
                        " foragerPercent"+String.valueOf(foragerPercentage)+
                        " foragerCycleLimit"+String.valueOf(foragerCycleLimit)+
                        " population"+String.valueOf(population)+
                        " printCycles"+String.valueOf(printCycle),bestPaths.get(0)
                );


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
}
