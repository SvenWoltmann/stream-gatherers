package eu.happycoders.stream_gatherers.demo_parallel;

public record ElementStage3(int element,
                            String threadNameMapStage1, int sequenceNoInThreadMapStage1,
                            String threadNameGatherStage, int threadLocalSequenceNoGatherStage,
                            int globalSequenceNoGatherStage,
                            String threadNameMapStage3, int sequenceNoInThreadMapStage3) {

  public ElementStage3(ElementStage2 elementStage2, String threadNameMapStage3, int sequenceNoInThreadMapStage3) {
    this(elementStage2.element(),
        elementStage2.threadNameMapStage1(), elementStage2.sequenceNoInThreadMapStage1(),
        elementStage2.threadNameGatherStage(), elementStage2.threadLocalSequenceNoGatherStage(),
        elementStage2.globalSequenceNoGatherStage(),
        threadNameMapStage3, sequenceNoInThreadMapStage3);
  }
}
