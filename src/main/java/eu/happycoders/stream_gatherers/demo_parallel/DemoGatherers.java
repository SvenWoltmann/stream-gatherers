package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.function.Function;
import java.util.stream.Gatherer;

final class DemoGatherers {

  private DemoGatherers() {
  }

  public static <T, R> Gatherer<T, Void, R> mapSequential(Function<T, R> mapper) {
    Gatherer.Integrator<Void, T, R> integrator =
        Gatherer.Integrator.ofGreedy((_, element, downstream) -> {
          R mappedElement = mapper.apply(element);
          return downstream.push(mappedElement);
        });

    return Gatherer.ofSequential(integrator);
  }
}
