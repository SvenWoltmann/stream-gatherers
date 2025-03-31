package eu.happycoders.stream_gatherers.demo_parallel;

public record Box2(int element,
                   String threadNameMapStage1, int sequenceNoInThreadMapStage1,
                   String threadNameGatherStage, int threadLocalSequenceNoGatherStage,
                   int globalSequenceNoGatherStage) {

  public Box2(Box1 box1,
              String threadNameGatherStage, int threadLocalSequenceNoGatherStage,
              int globalSequenceNoGatherStage) {
    this(box1.element(),
        box1.threadNameMapStage1(), box1.sequenceNoInThreadMapStage1(),
        threadNameGatherStage, threadLocalSequenceNoGatherStage, globalSequenceNoGatherStage);
  }
}
