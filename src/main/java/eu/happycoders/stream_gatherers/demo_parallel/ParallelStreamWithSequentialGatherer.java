package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static java.io.IO.println;

class ParallelStreamWithSequentialGatherer {

  private static final int NUMBER_OF_RUNS = 10_000;
  private static final int NUMBER_OF_ELEMENTS_PER_RUN = 10_000;
  private static final boolean PRINT_RESULTS_OF_EACH_RUN = false;

  void main() {
    List<Integer> ints = IntStream.range(0, NUMBER_OF_ELEMENTS_PER_RUN).boxed().toList();

    StageStatistics statisticsStage1 = new StageStatistics("Stage 1, parallel map()");
    StageStatistics statisticsStage2 = new StageStatistics("Stage 2, sequential mapping gatherer");
    MedianCalculator medianCalculator = new MedianCalculator();

    for (int i = 0; i < NUMBER_OF_RUNS; i++) {
      Result result = runTest(i, ints);
      statisticsStage1.addRun(result.threadNamesStage1().size());
      statisticsStage2.addRun(result.threadNamesStage2().size());
      medianCalculator.add(result.totalTimeNanos());
    }

    println("\n---------- Overall statistics ----------");
    println("Median of all round times: %.1f µs".formatted(medianCalculator.getMedian() / 1_000.0));
    statisticsStage1.print();
    statisticsStage2.print();
  }

  private Result runTest(int i, List<Integer> ints) {
    println("\n---------- Round %d ----------".formatted(i));

    ThreadLocal<Counter> threadLocalCounterStage1 = ThreadLocal.withInitial(Counter::new);

    ThreadLocal<Counter> threadLocalCounterStage2 = ThreadLocal.withInitial(Counter::new);
    Counter globalCounterStage2 = new Counter();

    long time = System.nanoTime();

    var list = ints.parallelStream()

        // intermediate operation 1: parallel mapping
        .map(x -> new Box1(x, Thread.currentThread().getName(), threadLocalCounterStage1.get().count++))

        // intermediate operation 2: sequential mapping
        .gather(DemoGatherers.mapSequential(
            x -> new Box2(x, Thread.currentThread().getName(), threadLocalCounterStage2.get().count++,
                globalCounterStage2.count++)))

        .toList();

    time = System.nanoTime() - time;

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

    return new Result(threadNamesStage1, threadNamesStage2, time);
  }

  record Result(Set<String> threadNamesStage1, Set<String> threadNamesStage2, long totalTimeNanos) {
  }
}