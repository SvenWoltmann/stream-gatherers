package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.function.Function;
import java.util.stream.Gatherer;

final class DemoGatherers {

  private DemoGatherers() {
  }

  static <T, R> Gatherer<T, ?, R> mapSequential(Function<T, R> mapper) {
    Gatherer.Integrator<Object, T, R> integrator =
        Gatherer.Integrator.of((object, element, downstream) -> {
          R mappedElement = mapper.apply(element);
          return downstream.push(mappedElement);
        });

    return Gatherer.ofSequential(Object::new, integrator);
  }
}
