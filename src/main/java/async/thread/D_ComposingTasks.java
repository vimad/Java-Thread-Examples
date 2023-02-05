package async.thread;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

public class D_ComposingTasks {

    record TravelPage(Quotation quotation, Weather weather) {
    }

    record Weather(String server, String weather) {
    }

    record Quotation(String server, int amount) {
    }

    public static void main(String[] args) throws InterruptedException {
        run();
    }

    private static int quotationThreadIndex = 0;
    private static ThreadFactory quotationThreadFactory =
          r -> new Thread(r, "Quotation-" + quotationThreadIndex++);

    private static int weatherThreadIndex = 0;
    private static ThreadFactory weatherThreadFactory =
          r -> new Thread(r, "Weather-" + weatherThreadIndex++);

    private static int minThreadIndex = 0;
    private static ThreadFactory minThreadFactory =
          r -> new Thread(r, "Min-" + minThreadIndex++);

    public static void run() throws InterruptedException {

        ExecutorService quotationExecutor =
              Executors.newFixedThreadPool(4, quotationThreadFactory);
        ExecutorService weatherExecutor =
              Executors.newFixedThreadPool(4, weatherThreadFactory);
        ExecutorService minExecutor =
              Executors.newFixedThreadPool(1, minThreadFactory);

        Random random = new Random();

        List<Supplier<Weather>> weatherTasks = buildWeatherTasks(random);
        List<Supplier<Quotation>> quotationTasks = buildQuotationTasks(random);

        List<CompletableFuture<Weather>> weatherCFs = new ArrayList<>();
        for (Supplier<Weather> weatherTask : weatherTasks) {
            CompletableFuture<Weather> weatherCF =
                  CompletableFuture.supplyAsync(weatherTask, weatherExecutor);
            weatherCFs.add(weatherCF);
        }

        CompletableFuture<Weather> anyOfWeather =
              CompletableFuture
                    .anyOf(weatherCFs.toArray(CompletableFuture[]::new))
                    .thenApply(weather -> (Weather) weather);


        List<CompletableFuture<Quotation>> quotationCFs = new ArrayList<>();
        for (Supplier<Quotation> quotationTask : quotationTasks) {
            CompletableFuture<Quotation> quotationCF =
                  CompletableFuture
                        .supplyAsync(quotationTask, quotationExecutor);
            quotationCFs.add(quotationCF);
        }

        CompletableFuture<Void> allOfQuotations =
              CompletableFuture.allOf(quotationCFs.toArray(CompletableFuture[]::new));

        CompletableFuture<Quotation> bestQuotationCF =
              allOfQuotations.thenApplyAsync(
                    v -> {
                        System.out.println("AllOf then apply " + Thread.currentThread());
                        return quotationCFs.stream()
                              .map(CompletableFuture::join)
                              .min(Comparator.comparing(Quotation::amount))
                              .orElseThrow();
                    },
                    minExecutor
              );

        CompletableFuture<Void> done =
              bestQuotationCF.thenCompose(
                          quotation ->
                                anyOfWeather.thenApply(
                                      weather -> new TravelPage(quotation, weather)))
                    .thenAccept(System.out::println);
        done.join();

        quotationExecutor.shutdown();
        weatherExecutor.shutdown();
        minExecutor.shutdown();
    }

    private static List<Supplier<Weather>> buildWeatherTasks(Random random) {
        Supplier<Weather> fetchWeatherA =
              () -> {
                  try {
                      Thread.sleep(random.nextInt(80, 120));
                  } catch (InterruptedException e) {
                      throw new RuntimeException(e);
                  }
                  System.out.println("WA running in " + Thread.currentThread());
                  return new Weather("Server A", "Sunny");
              };
        Supplier<Weather> fetchWeatherB =
              () -> {
                  try {
                      Thread.sleep(random.nextInt(80, 120));
                  } catch (InterruptedException e) {
                      throw new RuntimeException(e);
                  }
                  System.out.println("WB running in " + Thread.currentThread());
                  return new Weather("Server B", "Mostly Sunny");
              };
        Supplier<Weather> fetchWeatherC =
              () -> {
                  try {
                      Thread.sleep(random.nextInt(80, 120));
                  } catch (InterruptedException e) {
                      throw new RuntimeException(e);
                  }
                  System.out.println("WC running in " + Thread.currentThread());
                  return new Weather("Server C", "Almost Sunny");
              };

        var weatherTasks =
              List.of(fetchWeatherA, fetchWeatherB, fetchWeatherC);
        return weatherTasks;
    }


    private static List<Supplier<Quotation>> buildQuotationTasks(Random random) {
        Supplier<Quotation> fetchQuotationA =
              () -> {
                  try {
                      Thread.sleep(random.nextInt(80, 120));
                  } catch (InterruptedException e) {
                      throw new RuntimeException(e);
                  }
                  System.out.println("QA running in " + Thread.currentThread());
                  return new Quotation("Server A", random.nextInt(40, 60));
              };
        Supplier<Quotation> fetchQuotationB =
              () -> {
                  try {
                      Thread.sleep(random.nextInt(80, 120));
                  } catch (InterruptedException e) {
                      throw new RuntimeException(e);
                  }
                  System.out.println("QB running in " + Thread.currentThread());
                  return new Quotation("Server B", random.nextInt(30, 70));
              };
        Supplier<Quotation> fetchQuotationC =
              () -> {
                  try {
                      Thread.sleep(random.nextInt(80, 120));
                  } catch (InterruptedException e) {
                      throw new RuntimeException(e);
                  }
                  System.out.println("QC running in " + Thread.currentThread());
                  return new Quotation("Server C", random.nextInt(40, 80));
              };

        var quotationTasks =
              List.of(fetchQuotationA, fetchQuotationB, fetchQuotationC);
        return quotationTasks;
    }
}
