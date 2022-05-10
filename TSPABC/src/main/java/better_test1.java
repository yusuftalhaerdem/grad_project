import java.io.FileNotFoundException;



public class better_test1 extends Thread {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        // more focussed on population


        int[] opts = {4,0,1,2,3};       // check abc 427 for better understanding
        int file_no = 31;
        int printCycle = 2500;
        int cycleLimit = 250000;
        int population = 100;
        double foragerPercentage = 0.5; // percentage of forager bees
        int foragerCycleLimit = 500;
        int[] file_no_list = {31, 34, 35, 39, 41, 45, 57};
        int[] population_list = {100, 200, 500, 1000, 2000};
        int[] forager_cycle_limit_list = {10, 50, 100, 500, 1000};
        double[] forager_percentage_list = {0.3, 0.5, 0.7};

        int waiting_time_milis= 5000;

        // lets check

        for (int j : population_list) {
            MyThread thread = new MyThread(opts, file_no, printCycle, cycleLimit, j, foragerPercentage, foragerCycleLimit);
            thread.start();

            Thread.sleep(waiting_time_milis); // // waits 10 second
        }
        for (int j : population_list) {
            int cycleBalancer = j/population; // keeps the cycle count same
            MyThread thread = new MyThread(opts, file_no, printCycle/cycleBalancer,
                    cycleLimit/cycleBalancer, j, foragerPercentage, foragerCycleLimit);
            thread.start();

            Thread.sleep(waiting_time_milis); // // waits 10 second
        }



    }
}

