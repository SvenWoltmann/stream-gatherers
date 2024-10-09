void main() {
  List<String> words = List.of("the", "be", "two", "of", "and", "a", "in", "that");

  Optional<String> first = words.parallelStream()
      .gather(joining())
      .findFirst();

  System.out.println(first);
}

private static Gatherer<String, StringBuilder, String> joining() {
  Supplier<StringBuilder> initializer = StringBuilder::new;

  Gatherer.Integrator<StringBuilder, String, String> integrator =
      Gatherer.Integrator.ofGreedy(
          (state, element, _) -> {
            state.append(element);
            return true;
          });

  BiConsumer<StringBuilder, Gatherer.Downstream<? super String>> finisher =
      (state, downstream) -> downstream.push(state.toString());

  BinaryOperator<StringBuilder> combiner = StringBuilder::append;

  return Gatherer.of(initializer, integrator, combiner, finisher);
}
