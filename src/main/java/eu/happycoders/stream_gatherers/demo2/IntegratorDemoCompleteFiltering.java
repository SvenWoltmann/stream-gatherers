void main() {
  List<String> words = List.of("the", "be", "two", "of", "and", "a", "in", "that");

  List<String> list = words.stream()
      .gather(filtering(string -> string.length() >= 3))
      // .filter(string -> string.length() >= 3)
      .toList();

  System.out.println(list);
}

private static <T> Gatherer<T, Void, T> filtering(Predicate<T> predicate) {
  Gatherer.Integrator<Void, T, T> integrator =
      Gatherer.Integrator.ofGreedy(
          (_, element, downstream) -> {
            if (predicate.test(element)) {
              return downstream.push(element);
            } else {
              return true;
            }
          });

  return Gatherer.of(integrator);
}
