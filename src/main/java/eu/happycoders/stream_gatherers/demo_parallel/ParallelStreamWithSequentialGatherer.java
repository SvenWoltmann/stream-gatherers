package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.io.IO.println;

class ParallelStreamWithSequentialGatherer {

  void main() {
    List<String> words = new ArrayList<>();
    for (int i = 0; i < 1_000; i++) {
      words.add(String.valueOf(i));
    }

    Set<String> threadNamesFirstMapping = ConcurrentHashMap.newKeySet();
    ThreadNameCollector threadNamesGatherer = new ThreadNameCollector();
    Set<String> threadNamesSecondMapping = ConcurrentHashMap.newKeySet();

    List<Integer> list = words.parallelStream()

        // intermediate operation 1: parallel mapping
        .map(x -> {
          threadNamesFirstMapping.add(Thread.currentThread().getName());
          return x.length();
        })

        // intermediate operation 2: sequential gatherer
        .gather(DemoGatherers.windowFixed(10, threadNamesGatherer))

        // intermediate operation 3: parallel mapping
        .map(x -> {
          threadNamesSecondMapping.add(Thread.currentThread().getName());
          return x.size();
        })
        .toList();

    println("list = " + list);

    println("Number of threads op 1, parallel mapping:     %2d".formatted(threadNamesFirstMapping.size()));
    println("Number of threads op 2, sequential gathering: %2d (threads: %s)"
        .formatted(threadNamesGatherer.numberOfThreads(), threadNamesGatherer.threadSwitches()));
    println("Number of threads op 3, parallel mapping:     %2d".formatted(threadNamesSecondMapping.size()));
  }
}