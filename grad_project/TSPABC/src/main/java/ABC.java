import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class ABC {

    public ArrayList<ArrayList<Double>> makeDistanceTable(ArrayList<ArrayList<Integer>> dataList){

        ArrayList<ArrayList<Double>> table = new ArrayList<ArrayList<Double>>();

        for(int i=0; i<dataList.size(); i++){
            ArrayList<Double> distance = new ArrayList<Double>();
            ArrayList<Integer> list1 = dataList.get(i);
            for (ArrayList<Integer> list2 : dataList) {
                double dist = euclideanDistance(list1.get(1), list1.get(2),
                        list2.get(1), list2.get(2));
                distance.add(dist);
            }
            table.add(distance);
        }
        return table;
    }


    public double euclideanDistance(int x1, int x2, int y1, int y2) {
        return Math.hypot(x1 - y1, x2 - y2);
    }

    public ArrayList<Bee> initializeHive(int population, ArrayList<ArrayList<Integer>> dataList){
        ArrayList<Bee> hive = new ArrayList<Bee>();

        ArrayList<Integer> path = getStartingPath(dataList);

        for(int i=0; i<population; i++){    // creates bee with that path
            hive.add(new Bee("", new ArrayList<>(path)));
        }

        return hive;

    }

    public ArrayList<Integer> getStartingPath(ArrayList<ArrayList<Integer>> dataList){
        // makes all paths 1,2,3,4,5,6,7,...,n
        ArrayList<Integer> path = new ArrayList<Integer>();
        int currentLoad = 0;
        for (ArrayList<Integer> data : dataList) {
            if(data.get(0) ==0)
                continue;
            int peoplesAtStop = data.get(3);
            currentLoad+= peoplesAtStop;

            // if bus is overload we use extra bus.
            if(currentLoad > Bee.getVehicleCapacity()){
                path.add(0);
                path.add(data.get(0));
                currentLoad = peoplesAtStop;
            }else{
                path.add(data.get(0));
            }
        }
        return path;
    }

    public ArrayList<Bee> assignRoles(ArrayList<Bee> hive, ArrayList<Double> rolePercent,
                                      ArrayList<ArrayList<Double>> table, int population,
                                      ArrayList<ArrayList<Integer>> dataList){
        int onlookerCount = (int) (population * rolePercent.get(0));
        int foragerCount = (int) (population * rolePercent.get(1));
        int scoutCount = 0;
        //scoutCount = (int) (population * rolePercent.get(2));

        int i = 0;
        for(; i<onlookerCount; i++){
            hive.get(i).setRole("O");
        }
        for(; i<onlookerCount+scoutCount; i++){
            hive.get(i).setRole("S");
        }
        for(; i<onlookerCount+foragerCount+scoutCount; i++){
            hive.get(i).setRole("F");

            // starts forager bees
            foragerPath(hive.get(i), dataList, table);

            // and calculates their distance and score
            hive.get(i).setDistance(
                    getTotalDistanceOfPath(hive.get(i).getPath(), table));
            hive.get(i).setScore(
                    calculateScore(hive.get(i).getDistance(), hive.get(i).getNumberOfVehiclesUsed()) );
        }

        return hive;

    }

    private double calculateScore(/*Bee bee,*/ double distance, int numberOfVehiclesUsed) {
        double score= 0;
        score += distance;
        score += numberOfVehiclesUsed*1000;

        // todo: make it more complex, it shouldn't suffice varying parameters

        return score;
    }

    //gives random path to bee
    private void foragerPath(Bee bee, ArrayList<ArrayList<Integer>> dataList,
                             ArrayList<ArrayList<Double>> distanceTable) {
        int vehicleCapacity= Bee.getVehicleCapacity(); // TODO: we may find a better way to pass this value
        int peopleInVehicle= 0;
        int vehicleCount = 1;

        ArrayList<Integer> path = (ArrayList<Integer>) bee.getPath().clone();

        path.removeIf(n -> (n==0)); //removes each base node
        Collections.shuffle(path); // shuffles the collection

        path.add(0,0);  //each path shall start at base node
        for(int i =1 ; i<path.size(); i++){
            int passengersAtStop = dataList.get(path.get(i)).get(3); // gets the passenger count at stop/node
            peopleInVehicle+= passengersAtStop;
            if(peopleInVehicle > vehicleCapacity){ // if a vehicle is full, we start to new vehicle
                path.add(i,0);
                peopleInVehicle = 0;
                vehicleCount++;
            }
        }


        double new_distance = getTotalDistanceOfPath(path,distanceTable);
        double new_score = calculateScore(new_distance,vehicleCount);
        // if new score is better than previous one, we switch
        if(new_score< bee.getScore()){
            bee.setCycle(0);
            bee.setDistance(new_distance);
            bee.setPath(path);
            bee.setScore(new_score);
            bee.setNumberOfVehiclesUsed(vehicleCount);
        }else{ // we increase the cylcle count of bee
            bee.setCycle(bee.getCycle()+1);
        }

    }

    public double getTotalDistanceOfPath(ArrayList<Integer> path, ArrayList<ArrayList<Double>> table){

        double distance = 0;
        ArrayList<Integer> tempPath2 = new ArrayList<Integer>();
        path.add(path.get(0));

        for(int i=1; i<path.size(); i++){
            tempPath2.add(path.get(i));
        }

        path.remove(path.size()-1);

        //zipping
        ArrayList<ArrayList<Integer>> coordinates = new ArrayList<ArrayList<Integer>>();
        for(int i=0; i<path.size(); i++){
            coordinates.add(new ArrayList<Integer>(Arrays.asList(path.get(i), tempPath2.get(i))));
        }


        for (int i=0; i<coordinates.size()-1;i++) {
            distance += table.get(coordinates.get(i).get(0)).get(coordinates.get(i).get(1));
        }

        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);
        //double dist = Double.parseDouble(df.format(distance));

        return Double.parseDouble(df.format(distance));

    }

    public ArrayList<Bee> waggle(ArrayList<Bee> hive, ArrayList<ArrayList<Double>> table,
                                 ArrayList<ArrayList<Integer>> dataList){

        ArrayList<Bee> foragerList = new ArrayList<Bee>();

        for (Bee bee : hive) {
            if (bee.getRole().equals("F")) {

                foragerPath(bee, dataList, table);      // finds new path and changes if it is better one
                // adds forager bee into the list
                foragerList.add(bee);
            }
        }

        Collections.sort(foragerList);

        return foragerList;
    }

    // merges paths
    private ArrayList<Integer> mergePaths(ArrayList<ArrayList<Integer>> paths) {
        ArrayList<Integer> path = new ArrayList<>();
        for(int i =0; i< paths.size();i++){
            path.add(0);
            path.addAll(paths.get(i));
        }
        return path;
    }

    // separates paths
    private ArrayList<ArrayList<Integer>> separatePaths(ArrayList<Integer> path) {
        ArrayList<ArrayList<Integer>> paths = new ArrayList<>();

        ArrayList<Integer> partialPath = new ArrayList<>();
        // first we divide the paths
        for(int i = 1; i< path.size();i++){
            if(path.get(i) == 0){
                paths.add(partialPath);
                partialPath = new ArrayList<>();
            }else{
                partialPath.add(path.get(i));
            }
        }
        paths.add(partialPath);
        return paths;
    }

    // single node swapping
    private void swapPath(Bee bee) {
        ArrayList<Integer> path = bee.getPath();
        ArrayList<ArrayList<Integer>> paths = separatePaths(path);

        for(int i= 0; i <paths.size(); i++){

            // each path has a chance to have a swap operation
            int random = ThreadLocalRandom.current().nextInt(0, 2);

            if(random == 1 && paths.get(i).size()>1 ){
                // removes a value from arraylist and adds it to another location
                int numberToSwap= paths.get(i).remove(ThreadLocalRandom.current().nextInt(0, paths.get(i).size()));
                int replaceLocation = ThreadLocalRandom.current().nextInt(0, paths.get(i).size());

                paths.get(i).add(replaceLocation, numberToSwap);
            }
        }


        // gives new path to bee
        path = mergePaths(paths);
        bee.setPath(path);

    }

    // multi node swapping
    private void relocatePath(Bee bee) {
        // we may improve this function to choose longer strings to swap

        ArrayList<Integer> path = bee.getPath();

        ArrayList<ArrayList<Integer>> paths = separatePaths(path);

        for(int i= 0; i <paths.size(); i++){

            // each path has a chance to have a swap operation
            int random = ThreadLocalRandom.current().nextInt(0, 2);

            if(random == 1){
                // removes a value from arraylist and adds it to another location
                int swapStartPoint= ThreadLocalRandom.current().nextInt(0, paths.get(i).size());
                int swapSize = ThreadLocalRandom.current().nextInt(0, paths.get(i).size() - swapStartPoint+1);
                int relocationPoint = ThreadLocalRandom.current().nextInt(0, paths.get(i).size() - swapSize+1);

                // removes nodes one by one and adds them into listToSwap arrayList
                ArrayList<Integer> listToSwap = new ArrayList<>();
                for(int currentSwap = 0 ; currentSwap<swapSize; currentSwap++){
                    listToSwap.add(paths.get(i).remove(swapStartPoint));
                }

                // then adds all removed nodes into new location
                paths.get(i).addAll(relocationPoint,listToSwap);

            }
        }

        // gives new path to bee
        path = mergePaths(paths);
        bee.setPath(path);

    }

    // we may add the reversed string to a different location
    private void reversePath(Bee bee) {
        ArrayList<Integer> path = bee.getPath();
        ArrayList<ArrayList<Integer>> paths = separatePaths(path);

        //middleware
        for(int i= 0; i <paths.size(); i++){
            // each path has a chance to have a swap operation
            int random = ThreadLocalRandom.current().nextInt(0, 2);

            if(random == 1){
                int reverseStart = ThreadLocalRandom.current().nextInt(0, paths.get(i).size());
                int reverseAmount = ThreadLocalRandom.current().nextInt(0, paths.get(i).size()-reverseStart);
                ArrayList<Integer> deletedNodes = new ArrayList<>();

                // removes the nodes to be reversed from path
                for(int j = 0; j<reverseAmount;j++){
                    deletedNodes.add(paths.get(i).remove(reverseStart));
                }
                // then adds the removed nodes in reversed order
                for(int j = 0; j<reverseAmount; j++){
                    paths.get(i).add(reverseStart,deletedNodes.remove(0));
                }

            }
        }



        path = mergePaths(paths);
        bee.setPath(path);
    }


    public void printDetails(int cycle, ArrayList<Integer> bestPath, double bestDistance, String role){

        System.out.println("CYCLE: " + cycle + "\nPATH: " + bestPath +
                "\nDISTANCE: " + bestDistance + "\nBEE:" + role );
    }

    public ArrayList<Bee> recruit(ArrayList<Bee> hive, ArrayList<Bee> forager_bees_ordered, ArrayList<ArrayList<Double>> table) {

        double newDistance;
        ArrayList<Bee> onlookerBees= new ArrayList<Bee>();

        for (Bee bee : hive) {

            if (bee.getRole().equals("O")) {

                Bee chosenBee = rouletteSelection(forager_bees_ordered);
                ArrayList<Integer> path = (ArrayList<Integer>) chosenBee.getPath().clone();

                //
                bee.setPath(path); // soon we will mutate.
                bee.setNumberOfVehiclesUsed(chosenBee.getNumberOfVehiclesUsed());
                bee.setDistance(chosenBee.getDistance());

                int random = ThreadLocalRandom.current().nextInt(0, 3);
                //mutation functions
                if(random==0)
                    swapPath(bee);
                else if(random==1)
                    relocatePath(bee);
                else if(random==2)
                    reversePath(bee);

                newDistance = getTotalDistanceOfPath(bee.getPath(), table);

                bee.setDistance(newDistance);
                bee.setScore(calculateScore(bee.getDistance(), bee.getNumberOfVehiclesUsed()));

                //adds onlooker bee to the list
                onlookerBees.add(bee);
            }
        }

        Collections.sort(onlookerBees);

        return onlookerBees;
    }

    private Bee rouletteSelection(ArrayList<Bee> forager_bees_ordered) {
        double total_score = 0;

        // we only select the best bees among the hive.
        int elite_forager_bee_count = (int) Math.floor(forager_bees_ordered.size()/5);

        // creates a roulette whell according to its values
        for(int i = 0; i< elite_forager_bee_count; i++){
            total_score += 1/(forager_bees_ordered.get(i).getScore());
        }
        // spins the roulette
        double random_value = Math.random()*total_score;
        total_score = 0;

        // and finds where it stopped
        for (int i = 0;i<elite_forager_bee_count; i++){
            total_score += 1/(forager_bees_ordered.get(i).getScore());
            if(total_score>random_value){
                return forager_bees_ordered.get(i);
            }
        }
        return null;

    }


    public void scoutPhase(ArrayList<Bee> hive, int foragerCycleLimit, ArrayList<Bee> forager_bees_ordered,
                           ArrayList<ArrayList<Double>> table, ArrayList<ArrayList<Integer>> dataList) {
        int elite_forager_bee_count = (int) Math.floor(forager_bees_ordered.size()/5);

        ArrayList<Integer> startingPath = getStartingPath(dataList);

        for(int i = 0; i<elite_forager_bee_count;i++){
            Bee bee = forager_bees_ordered.get(i);
            if (bee.getCycle()>foragerCycleLimit){
                bee.setPath(startingPath);
                bee.setScore(Double.MAX_VALUE);
                bee.setDistance(Double.MAX_VALUE);
            }
        }
        for(int i = elite_forager_bee_count; i<forager_bees_ordered.size() ;i++){
            Bee bee = forager_bees_ordered.get(i);
            bee.setPath(startingPath);
            bee.setScore(Double.MAX_VALUE);
            bee.setDistance(Double.MAX_VALUE);
        }

    }
}
