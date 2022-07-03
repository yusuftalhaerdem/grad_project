import java.io.FileNotFoundException;



public class better_test2 extends Thread {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        // finds which is better, opt4 or opt5 by testing them in different files


        int[] opts = {4,5};       // check abc 427 for better understanding
        int file_no = 31;
        int printCycle = 2500;
        int cycleLimit = 250000;
        int population = 100;
        double foragerPercentage = 0.3; // percentage of forager bees
        int foragerCycleLimit = 500;
        int[] file_no_list = {31, 34, 35, 39, 41, 45, 57};
        int[] population_list = {100, 200, 500, 1000, 2000};
        int[] forager_cycle_limit_list = {10, 50, 100, 500, 1000};
        double[] forager_percentage_list = {0.3, 0.5, 0.7};

        int waiting_time_milis= 5000;

        // lets check

        for (int j : file_no_list) {
            int[] limited_opts = {4};       // check abc 427 for better understanding
            MyThread thread = new MyThread(limited_opts, j, printCycle, cycleLimit, population, foragerPercentage, foragerCycleLimit);
            thread.start();

            Thread.sleep(waiting_time_milis); // // waits 10 second
        }



    }
}

