void main() {
  List<String> words = List.of("the", "be", "two", "of", "and", "a", "in", "that");

  List<String> list = words.stream()
      .gather(filtering(string -> string.length() >= 3))
      // .filter(string -> string.length() >= 3)
      .toList();

  System.out.println(list);
}

private static Gatherer<String, Void, String> filtering(Predicate<String> predicate) {
  Gatherer.Integrator<Void, String, String> integrator =
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
