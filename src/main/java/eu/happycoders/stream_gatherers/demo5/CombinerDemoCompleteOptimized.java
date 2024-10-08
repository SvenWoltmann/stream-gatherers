void main() {

  List<String> words = List.of("the", "be", "two", "of", "and", "a", "in", "that");

  List<String> list = words.parallelStream()
      .gather(best(Comparator.comparing(String::length)))
      .toList();

  System.out.println(list);
}

private static Gatherer<String, ?, String> best(
    Comparator<String> comparator) {
  return Gatherer.of(
      // Initializer
      () -> new Object() {
        String bestElement;
      },

      // Integrator
      Gatherer.Integrator.ofGreedy((state, element, _) -> {
        if (state.bestElement == null || comparator.compare(element, state.bestElement) > 0) {
          state.bestElement = element;
        }
        return true;
      }),

      // Finisher
      (state1, state2) -> {
        if (state1.bestElement == null) {
          return state2;
        } else if (state2.bestElement == null) {
          return state1;
        } else if (comparator.compare(state1.bestElement, state2.bestElement) > 0) {
          return state1;
        } else {
          return state2;
        }
      },

      // Combiner
      (state, downstream) -> {
        if (state.bestElement != null) {
          downstream.push(state.bestElement);
        }
      });
}
