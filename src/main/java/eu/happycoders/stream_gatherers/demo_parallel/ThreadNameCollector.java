package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ThreadNameCollector {

  private final Set<String> names = new HashSet<>();
  private final List<ThreadSwitch> switches = new ArrayList<>();

  private String lastThreadName;
  private int index;

  void add(String threadName) {
    if (!threadName.equals(lastThreadName)) {
      switches.add(new ThreadSwitch(threadName, index));
      lastThreadName = threadName;
      names.add(threadName);
    }
    index++;
  }

  int numberOfThreads() {
    return names.size();
  }

  List<ThreadSwitch> threadSwitches() {
    return switches;
  }

  record ThreadSwitch(String name, int index) {
  }
}
