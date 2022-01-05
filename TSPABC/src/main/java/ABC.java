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
        ArrayList<Integer> path = new ArrayList<Integer>();
        ArrayList<Bee> hive = new ArrayList<Bee>();

        for (ArrayList<Integer> integers : dataList) {
            path.add(integers.get(0));
        }

        for(int i=0; i<population; i++){
            hive.add(new Bee("", new ArrayList<>(path), 0, 0 ));
        }

        return hive;
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
            foragerPath(hive.get(i), dataList );

            hive.get(i).setDistance(getTotalDistanceOfPath(hive.get(i).getPath(), table));
            calculateScore(hive.get(i));
        }



        return hive;

    }

    private void calculateScore(Bee bee) {
        double score= 3000;
        score-= bee.getDistance();
        score-= bee.getNumberOfVehiclesUsed()*1000;

        // todo: make it more complex, it wont suffice with varying parameters
        bee.setScore(score);
    }

    //gives random path to bee
    private void foragerPath(Bee bee, ArrayList<ArrayList<Integer>> dataList) {
        int vehicleCapacity= bee.getVehicleCapacity(); // TODO: we may find a better way to pass this value
        int peopleInVehicle= 0;
        int vehicleCount = 1;

        ArrayList<Integer> path = bee.getPath();
        path.removeIf(n -> (n==0)); //removes each base node
        Collections.shuffle(bee.getPath()); // shuffles the collection

        path.add(0,0);  //each path shall start at base node
        for(int i =1 ; i<path.size(); i++){
            int passengersAtStop = dataList.get(path.get(i)).get(3);
            peopleInVehicle+= passengersAtStop;
            if(peopleInVehicle > vehicleCapacity){ // if a vehicle is full, we start to new vehicle
                path.add(i,0);
                peopleInVehicle = 0;
                vehicleCount++;
            }
        }
        bee.setNumberOfVehiclesUsed(vehicleCount);

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
        double dist = Double.parseDouble(df.format(distance));

        return Double.parseDouble(df.format(distance));

    }

    public ReturnValues waggle(ArrayList<Bee> hive, double bestDistance,
                       ArrayList<ArrayList<Double>> table, int forager_limit, int scout_count,
                               ArrayList<ArrayList<Integer>> dataList){

        ArrayList<Integer> bestPath = new ArrayList<Integer>(), scouts = new ArrayList<Integer>();
        HashMap<Integer, Double> results= new HashMap<Integer, Double>();

        for (Bee bee : hive) {
            if (bee.getRole().equals("F")) {

                foragerPath(bee, dataList);
                bee.setDistance(getTotalDistanceOfPath(bee.getPath(),table));

                // im not so sure what this is so lets keep it for a some more time
                forage(bee, table,forager_limit);

                ReturnValues returnValues = new ReturnValues(bee.getDistance(),bee.getPath());
                if(returnValues.getBestDistance() < bestDistance){
                    bestDistance = returnValues.getBestDistance();
                    bestPath = new ArrayList<Integer>(returnValues.getBestPath());
                }
                results.put(hive.indexOf(bee), returnValues.getBestDistance());

            }else if(bee.getRole().equals("S")){
                scout(bee, table);
            }
        }

        // after processing all bees, set worst performers as scout

        List<Integer> mapKeys = new ArrayList<>(results.keySet());
        List<Double> mapValues = new ArrayList<>(results.values());
        mapValues.sort(Collections.reverseOrder());
        mapKeys.sort(Collections.reverseOrder());

        int count=0;
        for (double val : mapValues) {
            for (int key : mapKeys) {
                double comp1 = results.get(key);
                if (comp1 == val) {
                    if(count<scout_count){
                        scouts.add(key);
                    }else{
                        break;
                    }
                    count++;
                }
            }
        }
        for (Integer scout : scouts) {
            hive.get(scout).setRole("S");
        }

        return new ReturnValues(bestDistance, bestPath);
    }

    public ReturnValues forage(Bee bee, ArrayList<ArrayList<Double>> table, int forager_limit ){
        //ArrayList<Integer> newPath = mutatePath(bee.getPath());


        if(bee.getDistance()<bee.getDistance()){
            bee.setPath(bee.getPath());
            bee.setDistance(bee.getDistance());
            bee.setCycle(0);
        }else{
            bee.setCycle(bee.getCycle()+1);
        }

        if(bee.getCycle()> forager_limit){
            bee.setRole("S");
        }

        return new ReturnValues(bee.getDistance(), bee.getPath());
    }

    // delete
    private void randomizePath(Bee bee) {
        ArrayList<Integer> path = bee.getPath();



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

    public void scout(Bee bee, ArrayList<ArrayList<Double>> table){
        ArrayList<Integer> newPath = new ArrayList<Integer>(bee.getPath());
        Collections.shuffle(newPath);
        bee.setPath(newPath);
        bee.setDistance(getTotalDistanceOfPath(newPath, table));
        bee.setRole("F");
        bee.setCycle(0);
    }


    public void printDetails(int cycle, ArrayList<Integer> bestPath, double bestDistance, String role){

        System.out.println("CYCLE: " + cycle + "\nPATH: " + bestPath +
                "\nDISTANCE: " + bestDistance + "\nBEE:" + role );
    }

    public ReturnValues recruit(ArrayList<Bee> hive, double bestDistance,
                                ArrayList<Integer> bestPath, ArrayList<ArrayList<Double>> table) {

        double newDistance;

        for (Bee bee : hive) {

            bee.setPath(bestPath);

            if (bee.getRole().equals("O")) {

                int random = ThreadLocalRandom.current().nextInt(0, 3);
                //mutation functions
                if(random==0)
                    swapPath(bee);
                else if(random==1)
                    relocatePath(bee);
                else if(random==2)
                    reversePath(bee);

                newDistance = getTotalDistanceOfPath(bee.getPath(), table);

                //bee.setPath(new ArrayList<>(newPath));
                bee.setDistance(newDistance);

                if (bee.getDistance() < bestDistance) {
                    bestDistance = newDistance;
                    bestPath = bee.getPath();
                }
            }
        }
        return new ReturnValues(bestDistance, bestPath);
    }


}
