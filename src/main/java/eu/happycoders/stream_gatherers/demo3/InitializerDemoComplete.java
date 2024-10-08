void main() {
  List<String> words = List.of("the", "be", "two", "of", "and", "a", "in", "that");

  List<String> list = words.stream()
      .gather(limiting(3))
      .toList();

  System.out.println(list);
}

private static <T> Gatherer<T, AtomicInteger, T> limiting(int maxSize) {
  // Initializer
  Supplier<AtomicInteger> initializer = AtomicInteger::new;

  // Integrator
  Gatherer.Integrator<AtomicInteger, T, T> integrator =
      (state, element, downstream) -> {
        if (state.getAndIncrement() < maxSize) {
          return downstream.push(element);
        } else {
          return false;
        }
      };

  return Gatherer.ofSequential(initializer, integrator);
}
