void main() {
  List<String> words = List.of("the", "be", "two", "of", "and", "a", "in", "that");

  List<String> list = words.stream()
      .gather(limiting(3))
      .toList();

  System.out.println(list);
}

private static <T> Gatherer<T, ?, T> limiting(int maxSize) {
  return Gatherer.ofSequential(
      // Initializer
      () -> new Object() {
        int counter = 0;
      },

      // Integrator
      (state, element, downstream) -> {
        if (state.counter++ < maxSize) {
          return downstream.push(element);
        } else {
          return false;
        }
      });
}
