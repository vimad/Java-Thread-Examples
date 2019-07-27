package producerconsumer;

import java.util.ArrayList;
import java.util.List;

class Process {

    private List<Integer> list = new ArrayList<Integer>();
    private final int LIMIT = 5;
    private final int BOTTOM = 0;
    private int value = 0;
    private Object lock = new Object();

    public void produce()throws InterruptedException{
        synchronized (lock){
            while (true) {
                if(list.size() == LIMIT){
                    System.out.println("Waiting for consuming items...");
                    lock.wait();
                } else {
                    list.add(value);
                    System.out.println("Added value " + (value++));
                    lock.notify();
                }
                Thread.sleep(500);
            }
        }
    }

    public void consume() throws InterruptedException{
        synchronized (lock){
            while (true) {
                if(list.size() == BOTTOM){
                    System.out.println("Waiting for adding items...");
                    lock.wait();
                } else {
                    System.out.println("Removed value " + list.remove(--value));
                    lock.notify();
                }
                Thread.sleep(500);
            }
        }
    }
}

public class ProducerConsumerTest {

    public static void main(String args[]){

        Process process = new Process();

        Thread t1 = new Thread(
                () -> {
                    try{
                        process.produce();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
        );

        Thread t2 = new Thread(
                () -> {
                    try{
                        process.consume();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
        );

        t1.start();
        t2.start();
    }
}
