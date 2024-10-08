void main() {
  List<String> words = List.of("the", "be", "two", "of", "and", "a", "in", "that");

  List<List<String>> list = words.stream()
      .gather(windowFixed(3))
      .toList();

  System.out.println(list);
}

private static <T> Gatherer<T, List<T>, List<T>> windowFixed(int windowSize) {
  // Initializer
  Supplier<List<T>> initializer = ArrayList::new;

  // Integrator
  Gatherer.Integrator<List<T>, T, List<T>> integrator =
      Gatherer.Integrator.ofGreedy(
          (state, element, downstream) -> {
            state.add(element);
            if (state.size() == windowSize) {
              boolean result = downstream.push(List.copyOf(state));
              state.clear();
              return result;
            } else {
              return true;
            }
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
