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
            hive.add(new Bee("", path, 0, 0 ));
        }

        return hive;
    }

    public ArrayList<Bee> assignRoles(ArrayList<Bee> hive, ArrayList<Double> rolePercent,
                                             ArrayList<ArrayList<Double>> table, int population){
        int onlookerCount = (int) (population * rolePercent.get(0));
        int foragerCount = (int) (population * rolePercent.get(1));

        for(int i = 0; i<onlookerCount; i++){
            hive.get(i).setRole("O");
        }

        for(int i= onlookerCount; i<onlookerCount+foragerCount; i++){
            hive.get(i).setRole("F");
            ArrayList<Integer> tempPath= new ArrayList<>(hive.get(i).getPath());

            Collections.shuffle(tempPath);
            hive.get(i).setPath(tempPath);
            hive.get(i).setDistance(getTotalDistanceOfPath(hive.get(i).getPath(), table));
        }

        return hive;

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
                       ArrayList<ArrayList<Double>> table, int forager_limit, int scout_count){

        ArrayList<Integer> bestPath = new ArrayList<Integer>(), scouts = new ArrayList<Integer>();
        HashMap<Integer, Double> results= new HashMap<Integer, Double>();

        for (Bee bee : hive) {
            if (bee.getRole().equals("F")) {
                ReturnValues returnValues = forage(bee, table, forager_limit);
                if(returnValues.getBestDistance() < bestDistance){
                    bestDistance = returnValues.getBestDistance();
                    bestPath = new ArrayList<Integer>(returnValues.getBestPath());
                }
                results.put(hive.indexOf(bee), returnValues.getBestDistance());

            }else if(bee.getRole().equals("S")){
                scout(bee, table);
            }
        }

        // after processing all bees, set worst performers to scout

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
        ArrayList<Integer> newPath = mutatePath(bee.getPath());
        double newDistance = getTotalDistanceOfPath(newPath, table);

        if(newDistance<bee.getDistance()){
            bee.setPath(newPath);
            bee.setDistance(newDistance);
            bee.setCycle(0);
        }else{
            bee.setCycle(bee.getCycle()+1);
        }

        if(bee.getCycle()> forager_limit){
            bee.setRole("S");
        }

        return new ReturnValues(bee.getDistance(), bee.getPath());
    }

    public void scout(Bee bee, ArrayList<ArrayList<Double>> table){
        ArrayList<Integer> newPath = new ArrayList<Integer>(bee.getPath());
        Collections.shuffle(newPath);
        bee.setPath(newPath);
        bee.setDistance(getTotalDistanceOfPath(newPath, table));
        bee.setRole("F");
        bee.setCycle(0);
    }

    public ArrayList<Integer> mutatePath(ArrayList<Integer> path){
        int randomNum = ThreadLocalRandom.current().nextInt(0, path.size()-2);
        Collections.swap(path, randomNum, randomNum+1);
        return path;
    }

    public void printDetails(int cycle, ArrayList<Integer> bestPath, double bestDistance, String role){

        System.out.println("CYCLE: " + cycle + "\nPATH: " + bestPath +
                "\nDISTANCE: " + bestDistance + "\nBEE:" + role );
    }

    public ReturnValues recruit(ArrayList<Bee> hive, double bestDistance,
                                ArrayList<Integer> bestPath, ArrayList<ArrayList<Double>> table) {

        double newDistance = 0;
        ArrayList<Integer> newPath;

        for (Bee bee : hive) {
            if (bee.getRole().equals("O")) {
                newPath = mutatePath(bestPath);
                newDistance = getTotalDistanceOfPath(newPath, table);
                if (newDistance < bestDistance) {
                    bestDistance = newDistance;
                    bestPath = newPath;
                }
            }
        }
        return new ReturnValues(bestDistance, bestPath);
    }
}
