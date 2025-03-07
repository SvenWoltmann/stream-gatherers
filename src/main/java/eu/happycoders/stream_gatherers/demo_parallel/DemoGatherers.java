package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

final class DemoGatherers {

  private DemoGatherers() {
  }

  static <T> Gatherer<T, List<T>, List<T>> windowFixed(int windowSize,
                                                       Set<String> threadNamesCollector) {
    AtomicBoolean inTheIntegrator = new AtomicBoolean();

    // Initializer
    Supplier<List<T>> initializer = ArrayList::new;

    // Integrator
    Gatherer.Integrator<List<T>, T, List<T>> integrator =
        Gatherer.Integrator.ofGreedy(
            (state, element, downstream) -> {
              if (inTheIntegrator.compareAndExchange(false, true)) {
                throw new IllegalStateException();
              }

              threadNamesCollector.add(Thread.currentThread().getName());
              state.add(element);
              boolean result;
              if (state.size() == windowSize) {
                result = downstream.push(List.copyOf(state));
                state.clear();
              } else {
                result = true;
              }

              if (!inTheIntegrator.compareAndExchange(true, false)) {
                throw new IllegalStateException();
              }
              return result;
            });

    // Finisher
    BiConsumer<List<T>, Gatherer.Downstream<? super List<T>>> finisher =
        (state, downstream) -> {
          if (!state.isEmpty()) {
            downstream.push(List.copyOf(state));
          }
        };

    return Gatherer.ofSequential(initializer, integrator, finisher);
  }

}
