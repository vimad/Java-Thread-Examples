package diningphilosopherproblem;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Chopstic {

    private int id;
    private Lock lock;

    public Chopstic(int id){
       this.id = id;
       this.lock = new ReentrantLock();
    }

    public boolean pickUp(Philorsopher philorsopher, State state) throws InterruptedException{
        if(lock.tryLock(10, TimeUnit.MILLISECONDS)){
            System.out.println(philorsopher + " picked up " + state.toString() + this);
            return true;
        }

        return false;
    }

    public void putDown(Philorsopher philorsopher, State state){
        lock.unlock();
        System.out.println(philorsopher + " put down " + this);
    }

    @Override
    public String toString(){
        return "Chopstic " + id;
    }
}
