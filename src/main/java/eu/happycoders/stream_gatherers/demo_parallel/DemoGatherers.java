package eu.happycoders.stream_gatherers.demo_parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

final class DemoGatherers {

  private DemoGatherers() {
  }

  static <T> Gatherer<T, List<T>, List<T>> windowFixed(int windowSize,
                                                       ThreadNameCollector threadNamesCollector) {
    AtomicBoolean inTheIntegrator = new AtomicBoolean();

    Supplier<List<T>> initializer = ArrayList::new;

    Gatherer.Integrator<List<T>, T, List<T>> integrator =
        Gatherer.Integrator.ofGreedy(
            (state, element, downstream) -> {
              // Assert that only one thread at a time enters the integrate() method
              if (inTheIntegrator.compareAndExchange(false, true)) {
                throw new AssertionError("Sequential gatherer's integrate() method entered by second thread");
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

              inTheIntegrator.set(false);
              return result;
            });

    BiConsumer<List<T>, Gatherer.Downstream<? super List<T>>> finisher =
        (state, downstream) -> {
          if (!state.isEmpty()) {
            downstream.push(List.copyOf(state));
          }
        };

    return Gatherer.ofSequential(initializer, integrator, finisher);
  }
}
