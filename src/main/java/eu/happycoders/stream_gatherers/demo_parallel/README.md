# Sequential Gatherer Called From Multiple Threads

> **_NOTE:_**  
> 
> This README is not yet finished. Will update it soon.

## Observation

One might think that the `integrate()` method of a stream gatherer instantiated with `ofSequential()` is, even in a parallel stream, only called by a single thread.

However, this is not the case. We can also observe that the `integrate()` method is called from multiple threads (though never concurrently).

The probability that a sequential gatherer is called from different threads depends on various factors, such as the following:

* the number of elements,
* the operations performed in the stream,
* whether the integrator was created with or without `ofGreedy()`.

## Question

The question is: 

Why would the stream pipeline incur the overhead of memory barriers for the state object to execute the `integrate()` method in different threads – when it could just as well execute it in a single thread without memory barriers?

## Proof – at Simone's Repeated Request ;-)

In this package, you'll find the `ParallelStreamWithSequentialGatherer` class.

It will
1. Create a list of 10,000 `Integer` objects.
1. Create a parallel stream of that list. 
1. Map them in parallel to a `Box1` (which stores the element, the thread which called the `map()` operation, and a thread-local sequence number). 
1. Map them with a sequential gatherer to a `Box2` (which stores all the data of `Box1` plus the thread which called the `integrate()` method, the thread-local sequence number, and a global sequence number). 
1. Print some statistics.

It will repeat this 10,000 times and print out statistics about how many threads called the parallel `map()` operation and how many threads called the sequential gatherer's `integrate()` method. 

In the next section, you will see these statistics for various combinations of list sizes and greedy/non-greedy integrators. 

## Statistics

The following statistics were created on a laptop with an Intel Core i7-12800H (10 physical cores).

### Single stage: parallel map()

If we look at the parallel mapping only (with the second stage commented out), we see the following:

With 100 elements, in most runs, the stream was processed by 5 to 8 threads.

![](/img/demo_parallel/map_only_100.png)

With 10,000 elements, in most runs, the stream was processed by 12 to 15 threads.

![](/img/demo_parallel/map_only_10000.png)

With 1,000,000 elements, in most runs, the stream was processed by 19 or 20 threads.

![](/img/demo_parallel/map_only_1000000.v2.png)

Conclusion: The more objects go through the stream, the higher the probability that more threads are used (which makes sense).

### Two stages: parallel map() + sequential and greedy mapping gatherer

TODO

### Two stages: parallel map() + sequential and non-greedy mapping gatherer

TODO
