package diningphilosopherproblem;

import java.util.Random;

public class Philorsopher implements Runnable{

    private int id;
    private Chopstic leftChopstic;
    private Chopstic rightChopstc;
    private Random random;
    private int eatingCounter;
    private volatile boolean isFull = false;

    public Philorsopher(int id, Chopstic leftChopstic, Chopstic rightChopstc){
        this.id = id;
        this.leftChopstic = leftChopstic;
        this.rightChopstc = rightChopstc;
        this.random = new Random();
    }

    @Override
    public void run() {

        try{
            while (!isFull){
                if(leftChopstic.pickUp(this, State.LEFT)){
                    if(rightChopstc.pickUp(this, State.RIGHT)){
                        eat();
                        rightChopstc.putDown(this, State.RIGHT);
                    }
                    leftChopstic.putDown(this, State.LEFT);
                }
                think();
            }
        }catch (Exception e){

        }
    }

    private void think() throws InterruptedException{
        System.out.println(this + " is thinking");
        Thread.sleep(random.nextInt(1000));
    }

    private void eat()throws InterruptedException{
        System.out.println(this + " is eating");
        eatingCounter++;
        Thread.sleep(random.nextInt(1000));
    }

    public void setFull(boolean isFull){
        this.isFull = isFull;
    }

    public boolean getFull(){
        return isFull;
    }

    public int getEatingCount(){
        return eatingCounter;
    }

    @Override
    public String toString() {
        return "Philosopher " + id;
    }
}
