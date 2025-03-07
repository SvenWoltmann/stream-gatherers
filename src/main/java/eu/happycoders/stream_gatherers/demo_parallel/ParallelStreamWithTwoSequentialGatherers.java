package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

class ParallelStreamWithTwoSequentialGatherers {
  void main() {
    List<String> words = new ArrayList<>();
    for (int i = 0; i < 2000; i++) {
      words.add(String.valueOf(i));
    }

    Set<String> threadNamesFirstMapping = ConcurrentHashMap.newKeySet();

    Set<String> threadNamesGatherer1 = ConcurrentHashMap.newKeySet();

    LongAdder windows1Counter = new LongAdder();
    Set<String> threadNamesSecondMapping = ConcurrentHashMap.newKeySet();

    Set<String> threadNamesGatherer2 = ConcurrentHashMap.newKeySet();

    LongAdder windows2Counter = new LongAdder();
    Set<String> threadNamesThirdMapping = ConcurrentHashMap.newKeySet();

    List<Integer> list = words.parallelStream()
        .map(x -> {
          threadNamesFirstMapping.add(Thread.currentThread().getName());
          return x.length();
        })
        .gather(DemoGatherers.windowFixed(10, threadNamesGatherer1))
        .map(x -> {
          windows1Counter.increment();
          threadNamesSecondMapping.add(Thread.currentThread().getName());
          return x.size();
        })
        .gather(DemoGatherers.windowFixed(10, threadNamesGatherer2))
        .map(x -> {
          windows2Counter.increment();
          threadNamesThirdMapping.add(Thread.currentThread().getName());
          return x.size();
        })
        .toList();

    System.out.println("list = " + list);

    System.out.println("threadNamesFirstMapping.size()  = " + threadNamesFirstMapping.size());
    System.out.println("threadNamesGatherer1.size()     = " + threadNamesGatherer1.size());
    System.out.println("threadNamesGatherer1            = " + threadNamesGatherer1);
    System.out.println("windows1Counter.sum()           = " + windows1Counter.sum());
    System.out.println("threadNamesSecondMapping.size() = " + threadNamesSecondMapping.size());
    System.out.println("threadNamesGatherer2.size()     = " + threadNamesGatherer2.size());
    System.out.println("threadNamesGatherer2            = " + threadNamesGatherer2);
    System.out.println("windows2Counter.sum()           = " + windows2Counter.sum());
    System.out.println("threadNamesThirdMapping.size()  = " + threadNamesThirdMapping.size());
  }
}