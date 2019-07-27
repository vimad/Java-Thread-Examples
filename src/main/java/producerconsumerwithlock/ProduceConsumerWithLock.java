package producerconsumerwithlock;

import com.sun.corba.se.spi.orbutil.threadpool.Work;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Worker{
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    public void producer() throws InterruptedException{
        lock.lock();
        System.out.println("called producer");
        condition.await();
        System.out.println("called producer again");
        lock.unlock();
    }

    public void consumer() throws InterruptedException{
        lock.lock();
        Thread.sleep(2000);
        System.out.println("called consumer");
        condition.signal();
        lock.unlock();
    }
}

public class ProduceConsumerWithLock {

    public static void main(String args[]){

        Worker worker = new Worker();

        Thread t1 = new Thread(()->{
            try {
                worker.producer();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });

        Thread t2 = new Thread(()->{
            try {
                worker.consumer();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
    }
}
