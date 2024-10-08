void main() {
  List<String> words = List.of("the", "be", "two", "of", "and", "a", "in", "that");

  List<List<String>> list = words.stream()
      .gather(windowFixed(3))
      .toList();

  System.out.println(list);
}

private static Gatherer<String, List<String>, List<String>> windowFixed(int windowSize) {
  // Initializer
  Supplier<List<String>> initializer = ArrayList::new;

  // Integrator
  Gatherer.Integrator<List<String>, String, List<String>> integrator =
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
  BiConsumer<List<String>, Gatherer.Downstream<? super List<String>>> finisher =
      (state, downstream) -> {
        if (!state.isEmpty()) {
          downstream.push(List.copyOf(state));
        }
      };

  return Gatherer.ofSequential(initializer, integrator, finisher);
}
