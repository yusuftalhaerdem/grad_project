import java.io.FileNotFoundException;


class MyThread1 extends Thread{
    int[] opts = {0,1,2,3,4};
    int file_no = 31;
    int printCycle = 2500;
    int cycleLimit = 250000;
    int population = 100;
    double foragerPercentage = 0.5;
    int foragerCycleLimit = 500;

    public MyThread1(int[] opts, int file_no, int printCycle, int cycleLimit, int population, double foragerPercentage, int foragerCycleLimit) {
        this.opts = opts;
        this.file_no = file_no;
        this.printCycle = printCycle;
        this.cycleLimit = cycleLimit;
        this.population = population;
        this.foragerPercentage = foragerPercentage;
        this.foragerCycleLimit = foragerCycleLimit;
    }
    @Override
    public void run() {
        try {
            Controller.wishing(opts, file_no, printCycle, cycleLimit, population, foragerPercentage, foragerCycleLimit);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
public class better_test1 extends Thread {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {


        int[] opts = {4};       // check abc 427 for better understanding
        int file_no = 31;
        int printCycle = 25000;
        int cycleLimit = 2500000;
        int population = 100;
        double foragerPercentage = 0.5; // percentage of forager bees
        int foragerCycleLimit = 500;
        int[] file_no_list = {31, 34, 35, 39, 41, 45, 57};
        int[] population_list = {100, 200, 300, 500, 1000};
        int[] forager_cycle_limit_list = {10, 50, 100, 500, 1000};
        double[] forager_percentage_list = {0.3, 0.5, 0.7};



        //wish.wishing(opts, file_no, printCycle, cycleLimit, population, foragerPercentage, foragerCycleLimit);

        // lets check

        for (int j : population_list) {
            int cycleBalancer = j/population; // keeps the cycle count same
            MyThread1 thread = new MyThread1(opts, file_no, printCycle/cycleBalancer,
                    cycleLimit/cycleBalancer, j, foragerPercentage, foragerCycleLimit);
            thread.start();

            Thread.sleep(10000); // // waits 10 second
        }
        for (double j : forager_percentage_list) {
            MyThread1 thread = new MyThread1(opts, file_no, printCycle, cycleLimit, population, j, foragerCycleLimit);
            thread.start();

            Thread.sleep(10000); // // waits 10 second
        }
        for (int j : forager_cycle_limit_list) {
            MyThread1 thread = new MyThread1(opts, file_no, printCycle, cycleLimit, population, foragerPercentage, j);
            thread.start();

            Thread.sleep(10000); // // waits 10 second
        }



    }
}

