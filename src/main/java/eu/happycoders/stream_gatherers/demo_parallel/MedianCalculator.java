package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.ArrayList;
import java.util.List;

/// This class is not thread-safe!
class MedianCalculator {

  private final List<Long> values = new ArrayList<>();

  public void add(long value) {
    values.add(value);
  }

  public long getMedian() {
    values.sort(null);
    return values.get(values.size() / 2);
  }
}
