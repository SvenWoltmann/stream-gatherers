package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

class ParallelStreamWithSequentialGatherer {
  void main() {
    List<String> words = new ArrayList<>();
    for (int i = 0; i < 150; i++) {
      words.add(String.valueOf(i));
    }

    Set<String> threadNamesFirstMapping = ConcurrentHashMap.newKeySet();
    Set<String> threadNamesGatherer = ConcurrentHashMap.newKeySet();
    Set<String> threadNamesSecondMapping = ConcurrentHashMap.newKeySet();
    LongAdder windowsCounter = new LongAdder();

    List<Integer> list = words.parallelStream()
        .map(x -> {
          threadNamesFirstMapping.add(Thread.currentThread().getName());
          return x.length();
        })
        .gather(DemoGatherers.windowFixed(10, threadNamesGatherer))
        .map(x -> {
          windowsCounter.increment();
          threadNamesSecondMapping.add(Thread.currentThread().getName());
          return x.size();
        })
        .toList();

    System.out.println("list = " + list);

    System.out.println("threadNamesFirstMapping.size()  = " + threadNamesFirstMapping.size());
    System.out.println("threadNamesGatherer.size()      = " + threadNamesGatherer.size());
    System.out.println("threadNamesGatherer             = " + threadNamesGatherer);
    System.out.println("windowsCounter.sum()            = " + windowsCounter.sum());
    System.out.println("threadNamesSecondMapping.size() = " + threadNamesSecondMapping.size());
  }
}