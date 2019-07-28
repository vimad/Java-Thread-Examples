package diningphilosopherproblem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Solver {

    public static void main(String args[]) throws InterruptedException{

        ExecutorService executorService = null;
        Philorsopher[] philorsophers = null;
        Chopstic[] chopstics = new Chopstic[Constants.NUMBER_OF_CHOPSTICS];

        for(int i = 0; i < Constants.NUMBER_OF_CHOPSTICS; i++){
            chopstics[i] = new Chopstic(i);
        }

        try{

            philorsophers = new Philorsopher[Constants.NUMBER_OF_PHILOSOPHORS];
            executorService = Executors.newFixedThreadPool(5);

            for(int i = 0; i < Constants.NUMBER_OF_PHILOSOPHORS; i++){
                philorsophers[i] = new Philorsopher(i, chopstics[i], chopstics[ (i+1) % Constants.NUMBER_OF_CHOPSTICS]);
                executorService.execute(philorsophers[i]);
            }


            Thread.sleep(Constants.SIMULATION_RUNNING_TIME);


            for(Philorsopher p: philorsophers){
                p.setFull(true);
            }
        } finally {
            executorService.shutdown();

            while (!executorService.isTerminated())
                Thread.sleep(1000);

            System.out.println("----------------------------------------------------------------------");
            for(Philorsopher philorsopher: philorsophers){
                System.out.println(philorsopher + " eats " + philorsopher.getEatingCount() +" times");
            }
        }
    }
}
