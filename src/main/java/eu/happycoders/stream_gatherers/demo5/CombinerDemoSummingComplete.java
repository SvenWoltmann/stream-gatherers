void main() {
    List<String> words = List.of("the", "be", "two", "of", "and", "a", "in", "that");

    Optional<Integer> first = words.parallelStream()
            .map(String::length)
            .gather(summing())
            .findFirst();

    System.out.println("first = " + first);
}

private Gatherer<Integer, ?, Integer> summing() {
    class Adder {
        int sum;

        Adder() {
            this(0);
        }

        Adder(int sum) {
            this.sum = sum;
        }
    }

    Supplier<Adder> initializer = Adder::new;

    Gatherer.Integrator<Adder, Integer, Integer> integrator =
            Gatherer.Integrator.ofGreedy((state, element, _) -> {
                state.sum += element;
                return true;
            });

    BinaryOperator<Adder> combiner =
            (adder1, adder2) -> new Adder(adder1.sum + adder2.sum);

    BiConsumer<Adder, Gatherer.Downstream<? super Integer>> finisher =
            (state, downstream) -> downstream.push(state.sum);

    return Gatherer.of(initializer, integrator, combiner, finisher);
}
