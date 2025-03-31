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

<img src="/img/demo_parallel/map_only_100.png" style="width:496px;"/>

With 10,000 elements, it was mostly processed by 12 to 15 threads.

<img src="/img/demo_parallel/map_only_10000.png" style="width:496px;"/>

And with 1,000,000 elements, it was mostly processed by 19 or 20 threads.

<img src="/img/demo_parallel/map_only_1000000.v2.png" style="width:496px;"/>

Conclusion: The more objects go through the stream, the higher the probability that more threads are used (which makes sense).

### Two stages: parallel map() + sequential and greedy mapping gatherer

Now let's uncomment the second stage and repeat the tests...

With 100 elements, the map operation (left part of the diagram) was, again, mostly processed by 5 to 8 threads.

The sequential gatherer (right part of the diagram), which I would expect to run in one thread, runs in 1-3 threads:

<img src="/img/demo_parallel/map_and_greedy_mapping_gatherer_100.png" style="width:991px;"/>

Also with 10,000 elements, the first stage looks the same.

The sequential gatherer, however, was processed by fewer threads – mostly by one thread only:

<img src="/img/demo_parallel/map_and_greedy_mapping_gatherer_10000.png" style="width:991px;"/>

With 1,000,000 elements, the first stage looks the same again.

And the sequential gatherer was processed by ewen fewer threads – almost always by one thread only:

<img src="/img/demo_parallel/map_and_greedy_mapping_gatherer_1000000.png" style="width:991px;"/>

Observations:

* Adding the sequential gatherer after the parallel `map()` stage does not change the behaviour of the `map()` stage. 
* Contrary to the first stage, the sequential gatherer is processed by fewer threads the more elements the stream has to process.

### Two stages: parallel map() + sequential and non-greedy mapping gatherer

Now let's change the gatherer from greedy to non-greedy, and this time we start with 1,000,000 elements:

The non-greedy gatherer results in both stages being executed by just one thread: 

<img src="/img/demo_parallel/map_and_non_greedy_mapping_gatherer_1000000.png" style="width:991px;"/>

... or so we thought at the beginning.

But when we reduce the number of elements to 10,000, the picture changes to a probability distribution again:

<img src="/img/demo_parallel/map_and_non_greedy_mapping_gatherer_10000.png" style="width:991px;"/>

And with 100 elements, even more threads are used:

<img src="/img/demo_parallel/map_and_non_greedy_mapping_gatherer_100.png" style="width:991px;"/>

Observations:

* Making the gatherer non-greedy results in each element being processed sequentially in both stages. This makes sense as the pipeline does not know in advance when it will be stopped.
* Both stages are processed by more threads (though never concurrently) the fewer elements the stream has to process.

### Comparing Runtimes

The processing of all elements took the following times (median over 10,000 runs in each case):

* 100 elements: 
  * greedy: 41.2 µs
  * non-greedy: 41.2 µs &rarr; no difference

* 10,000 elements:
    * non-greedy: 295.4 µs
    * greedy: 198.9 µs &rarr; faster by a factor of 1.5

* 1,000,000 elements:
    * greedy: 21,349 µs
    * non-greedy: 10,335 µs &rarr; faster by factor 2

Using `ofGreedy()` can result in a significant performance overhead. Therefore, you should always make sure that you use `ofGreedy()` if appropriate.

The question remains why the stream pipeline sometimes decides to run the sequential gatherer on multiple threads (sequentially).
