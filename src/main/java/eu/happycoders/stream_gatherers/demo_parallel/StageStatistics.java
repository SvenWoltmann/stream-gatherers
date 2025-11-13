package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.Map;
import java.util.TreeMap;


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
    IO.println();
    IO.println(stageName);
    IO.println("# of threads  ->  # of runs");
    countersByNumberOfThreads.forEach((key, counter) -> IO.println(
        "%-13s ->  %4d (%5.2f %%)".formatted(key, counter.count, 100.0 * counter.count / numberOfRuns)));
  }
}

