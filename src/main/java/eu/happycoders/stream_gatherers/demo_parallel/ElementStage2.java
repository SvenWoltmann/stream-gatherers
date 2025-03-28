package eu.happycoders.stream_gatherers.demo_parallel;

public record ElementStage2(int element,
                            String threadNameMapStage1, int sequenceNoInThreadMapStage1,
                            String threadNameGatherStage, int threadLocalSequenceNoGatherStage,
                            int globalSequenceNoGatherStage) {

  public ElementStage2(ElementStage1 elementStage1,
                       String threadNameGatherStage, int threadLocalSequenceNoGatherStage,
                       int globalSequenceNoGatherStage) {
    this(elementStage1.element(),
        elementStage1.threadNameMapStage1(), elementStage1.sequenceNoInThreadMapStage1(),
        threadNameGatherStage, threadLocalSequenceNoGatherStage, globalSequenceNoGatherStage);
  }
}
