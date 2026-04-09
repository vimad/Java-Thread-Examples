package virtualthreads;

import java.util.HashSet;
import java.util.Set;

/*
All virtual threads belong to a single thread group; there is no API to create a virtual thread with a different thread group.
 If we create a virtual thread and invoke the getThreadGroup() method, we will always get instances of the same ThreadGroup.
 */
public class VirtualThreadGroupExample {

    public static void main(String[] args) throws InterruptedException {
        Set<ThreadGroup> threadGroups = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            Thread vThread = Thread.ofVirtual().start(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threadGroups.add(vThread.getThreadGroup());
        }
        Thread.sleep(1000); // Wait for threads to complete
        System.out.println("Unique thread groups: " + threadGroups.size());
        System.out.println("Thread group: " + threadGroups.iterator().next());
    }
}
