import java.io.FileNotFoundException;

public class test_for_results {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        // get results for different problems

        int[] opts = {0,1,2,3};       // check abc 427 for better understanding
        int printCycle = 100;
        int cycleLimit = 10000;
        int population = 100;
        double foragerPercentage = 0.3; // percentage of forager bees
        int foragerCycleLimit = 500;
        int file_no = 11;
        int[] file_no_list = {10,11,12,13,14,15};

        int waiting_time_milis= 50000;


        // lets check

        MyThread thread = new MyThread( opts, file_no, printCycle, cycleLimit, population, foragerPercentage, foragerCycleLimit);
        thread.start();

        // Thread.sleep(waiting_time_milis); // // waits 10 second



    }
}
