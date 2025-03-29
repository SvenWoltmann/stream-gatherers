package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.IntStream;

import static java.io.IO.println;

class ParallelStreamWithSequentialGatherer {

  private static final int NUMBER_OF_RUNS = 10_000;
  private static final int NUMBER_OF_ELEMENTS_PER_RUN = 10_000;
  private static final boolean PRINT_RESULTS_OF_EACH_RUN = false;

  void main() {
    List<Integer> origin = IntStream.range(0, NUMBER_OF_ELEMENTS_PER_RUN).boxed().toList();

    Map<String, Counter> statisticsMapStage1 = new TreeMap<>(); // TreeMap to sort the result
    Map<String, Counter> statisticsMapStage2 = new TreeMap<>();
    for (int i = 0; i < NUMBER_OF_RUNS; i++) {
      ThreadNames threadNames = runTest(i, origin);
      addResultToStatisticsMap(threadNames.threadNamesStage1(), statisticsMapStage1);
      addResultToStatisticsMap(threadNames.threadNamesStage2(), statisticsMapStage2);
    }

    println("\n---------- Overall statistics ----------");
    println("Stage 1, parallel map()");
    println("# of threads");
    statisticsMapStage1.forEach((key, counter) -> println(
        "%-10s -> %4d x (%5.2f %%)".formatted(key, counter.count, 100.0 * counter.count / NUMBER_OF_RUNS)));
    println("\nStage 2, sequential mapping gatherer");
    println("# of threads");
    statisticsMapStage2.forEach((key, counter) -> println(
        "%-10s -> %4d x (%5.2f %%)".formatted(key, counter.count, 100.0 * counter.count / NUMBER_OF_RUNS)));
  }

  private ThreadNames runTest(int i, List<Integer> origin) {
    println("\n---------- Round %d ----------".formatted(i));

    ThreadLocal<Counter> perThreadCounterStage1 = ThreadLocal.withInitial(Counter::new);

    ThreadLocal<Counter> perThreadCounterStage2 = ThreadLocal.withInitial(Counter::new);
    Counter globalCounterStage2 = new Counter();

    var list = origin.parallelStream()

        // intermediate operation 1: parallel mapping
        .map(x -> new ElementStage1(x, Thread.currentThread().getName(), perThreadCounterStage1.get().count++))

        // intermediate operation 2: sequential mapping
        .gather(DemoGatherers.mapSequential(
            x -> new ElementStage2(x, Thread.currentThread().getName(), perThreadCounterStage2.get().count++,
                globalCounterStage2.count++)))

        .toList();

    Set<String> threadNamesStage1 = new HashSet<>();
    Set<String> threadNamesStage2 = new HashSet<>();

    if (PRINT_RESULTS_OF_EACH_RUN) {
      println("list:");
      for (var element : list) {
        println("  " + element);
      }
    }

    for (var element : list) {
      threadNamesStage1.add(element.threadNameMapStage1());
      threadNamesStage2.add(element.threadNameGatherStage());
    }

    println("Number of threads stage 1, parallel   mapping:  %2d (%s)"
        .formatted(threadNamesStage1.size(), threadNamesStage1));
    println("Number of threads stage 2, sequential mapping:  %2d (%s)"
        .formatted(threadNamesStage2.size(), threadNamesStage2));

    return new ThreadNames(threadNamesStage1, threadNamesStage2);
  }

  record ThreadNames(Set<String> threadNamesStage1, Set<String> threadNamesStage2) {
  }

  private static void addResultToStatisticsMap(Set<String> threadNamesGatherStage, Map<String, Counter> statisticsMap) {
    int numberOfThreads = threadNamesGatherStage.size();
    String key = String.valueOf(numberOfThreads);
    statisticsMap.computeIfAbsent(key, _ -> new Counter()).count++;
  }

  private static class Counter {
    private int count;
  }
}