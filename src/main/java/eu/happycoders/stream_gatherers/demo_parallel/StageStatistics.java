package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.Map;
import java.util.TreeMap;

import static java.io.IO.println;

/// This class is not thread-safe!
class StageStatistics {

  private final String stageName;
  private final Map<Integer, Counter> countersByNumberOfThreads = new TreeMap<>();
  private int numberOfRuns;

  StageStatistics(String stageName) {
    this.stageName = stageName;
  }

  void addRun(int numberOfThreads) {
    countersByNumberOfThreads.computeIfAbsent(numberOfThreads, _ -> new Counter()).count++;
    numberOfRuns++;
  }

  void print() {
    println();
    println(stageName);
    println("# of threads  ->  # of runs");
    countersByNumberOfThreads.forEach((key, counter) -> println(
        "%-13s ->  %4d (%5.2f %%)".formatted(key, counter.count, 100.0 * counter.count / numberOfRuns)));
  }
}

