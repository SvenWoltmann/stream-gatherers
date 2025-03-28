package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.io.IO.println;

class ParallelStreamWithSequentialGatherer {

  private static final int NUMBER_OF_RUNS = 10_000;
  private static final int NUMBER_OF_ELEMENTS_PER_RUN = 10_000;
  private static final boolean PRINT_RESULTS_OF_EACH_RUN = false;

  void main() {
    List<Integer> origin = prepareOriginList();

    Map<String, Counter> statisticsMap = new TreeMap<>(); // TreeMap to sort the result
    for (int i = 0; i < NUMBER_OF_RUNS; i++) {
      Set<String> threadNamesGatherStage = runTest(i, origin);
      addResultToStatisticsMap(threadNamesGatherStage, statisticsMap);
    }

    println("\n---------- Overall statistics ----------");
    println("# of threads");
    statisticsMap.forEach((key, counter) -> println(
        "%-10s -> %4d x (%5.2f %%)".formatted(key, counter.count, 100.0 * counter.count / NUMBER_OF_RUNS)));
  }

  private static List<Integer> prepareOriginList() {
    List<Integer> origin = new ArrayList<>();
    for (int i = 0; i < NUMBER_OF_ELEMENTS_PER_RUN; i++) {
      origin.add(i);
    }
    return origin;
  }

  private Set<String> runTest(int i, List<Integer> origin) {
    println("\n---------- Round %d ----------".formatted(i));

    ThreadLocal<Counter> perThreadCounterStage1 = ThreadLocal.withInitial(Counter::new);

    ThreadLocal<Counter> perThreadCounterStage2 = ThreadLocal.withInitial(Counter::new);
    Counter globalCounterStage2 = new Counter();

    ThreadLocal<Counter> perThreadCounterStage3 = ThreadLocal.withInitial(Counter::new);

    List<ElementStage3> list = origin.parallelStream()

        // intermediate operation 1: parallel mapping
        .map(x -> new ElementStage1(x, Thread.currentThread().getName(), perThreadCounterStage1.get().count++))

        // intermediate operation 2: sequential mapping
        .gather(DemoGatherers.mapSequential(
            x -> new ElementStage2(x, Thread.currentThread().getName(), perThreadCounterStage2.get().count++,
                globalCounterStage2.count++)))

        // intermediate operation 3: parallel mapping
        .map(x -> new ElementStage3(x, Thread.currentThread().getName(), perThreadCounterStage3.get().count++))

        .toList();

    Set<String> threadNamesStage1 = new HashSet<>();
    Set<String> threadNamesStage2 = new HashSet<>();
    Set<String> threadNamesStage3 = new HashSet<>();

    if (PRINT_RESULTS_OF_EACH_RUN) {
      println("list:");
      for (ElementStage3 element : list) {
        println("  " + element);
      }
    }

    for (ElementStage3 element : list) {
      threadNamesStage1.add(element.threadNameMapStage1());
      threadNamesStage2.add(element.threadNameGatherStage());
      threadNamesStage3.add(element.threadNameMapStage3());
    }

    println("Number of threads stage 1, parallel   mapping:  %2d (%s)"
        .formatted(threadNamesStage1.size(), threadNamesStage1));
    println("Number of threads stage 2, sequential mapping:  %2d (%s)"
        .formatted(threadNamesStage2.size(), threadNamesStage2));
    println("Number of threads stage 3, parallel   mapping:  %2d (%s)"
        .formatted(threadNamesStage3.size(), threadNamesStage3));

    return threadNamesStage2;
  }


  private static void addResultToStatisticsMap(Set<String> threadNamesGatherStage, Map<String, Counter> statisticsMap) {
    int numberOfThreads = threadNamesGatherStage.size();
    String key;
    if (numberOfThreads == 1) {
      key = threadNamesGatherStage.contains("main") ? "1 (main)" : "1 (worker)";
    } else {
      key = String.valueOf(numberOfThreads);
    }
    statisticsMap.computeIfAbsent(key, _ -> new Counter()).count++;
  }


  private static class Counter {
    private int count;
  }
}