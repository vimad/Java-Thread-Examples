package virtualthreads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SimpleExample {

    static void main() throws InterruptedException, ExecutionException {
        Thread vThread = Thread.startVirtualThread(() -> {
            System.out.println("Virtual threads make concurrency effortless!" + "See for yourself.");
        });
        vThread.join();

        var startedThread = Thread.ofVirtual().start(() -> System.out.println("Hello world!"));
        startedThread.join();

        var unstartedThread = Thread.ofVirtual().unstarted(() -> System.out.println("Hello world! - unstarted"));
        unstartedThread.start();

        try (var virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> future = virtualExecutor.submit(SimpleExample::callService);
            System.out.println(future.get());
        }

        Thread thread = Thread.startVirtualThread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("You probably won't see this if the join method below commented because VThreads are daemon threads");
            } catch (InterruptedException e) {
            }
        });
        thread.join();

    }

    private static String callService() {
        return "Hello!";
    }
}
