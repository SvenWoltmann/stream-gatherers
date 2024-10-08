void main() {

  List<String> words = List.of("the", "be", "two", "of", "and", "a", "in", "that");

  List<String> list = words.parallelStream()
      .gather(best(Comparator.comparing(String::length)))
      .toList();

  System.out.println(list);
}

private static Gatherer<String, AtomicReference<String>, String> best(
    Comparator<String> comparator) {
  // Initializer
  Supplier<AtomicReference<String>> initializer = AtomicReference::new;

  // Integrator
  Gatherer.Integrator<AtomicReference<String>, String, String> integrator =
      Gatherer.Integrator.ofGreedy((state, element, _) -> {
        String bestElement = state.get();
        if (bestElement == null || comparator.compare(element, bestElement) > 0) {
          state.set(element);
        }
        return true;
      });

  // Finisher
  BiConsumer<AtomicReference<String>, Gatherer.Downstream<? super String>> finisher =
      (state, downstream) -> {
        String bestElement = state.get();
        if (bestElement != null) {
          downstream.push(bestElement);
        }
      };

  // Combiner
  BinaryOperator<AtomicReference<String>> combiner =
      (state1, state2) -> {
        String bestElement1 = state1.get();
        String bestElement2 = state2.get();

        if (bestElement1 == null) {
          return state2;
        } else if (bestElement2 == null) {
          return state1;
        } else if (comparator.compare(bestElement1, bestElement2) > 0) {
          return state1;
        } else {
          return state2;
        }
      };

  return Gatherer.of(initializer, integrator, combiner, finisher);
}
