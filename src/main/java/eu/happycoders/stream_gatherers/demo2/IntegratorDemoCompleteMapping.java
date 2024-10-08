void main() {
  List<String> words = List.of("the", "be", "two", "of", "and", "a", "in", "that");

  List<String> list = words.stream()
      .gather(mapping(String::toUpperCase))
      // .map(String::toUpperCase)
      .toList();

  System.out.println(list);
}

private static <T, R> Gatherer<T, Void, R> mapping(Function<T, R> mapper) {
  Gatherer.Integrator<Void, T, R> integrator =
      Gatherer.Integrator.ofGreedy((_, element, downstream) -> {
        R mappedElement = mapper.apply(element);
        return downstream.push(mappedElement);
      });

  return Gatherer.of(integrator);
}
