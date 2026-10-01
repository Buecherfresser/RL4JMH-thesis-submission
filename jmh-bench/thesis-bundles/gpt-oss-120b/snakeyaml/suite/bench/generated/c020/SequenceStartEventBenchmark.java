package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SequenceStartEventBenchmark {

    private String anchor;
    private String tag;
    private boolean implicit;
    private Mark startMark;
    private Mark endMark;
    private DumperOptions.FlowStyle flowStyle;
    private SequenceStartEvent prebuiltEvent;

    @Setup
    public void setup() {
        anchor = "anchor";
        tag = "tag:yaml.org,2002:seq";
        implicit = true;
        char[] emptyBuffer = new char[0];
        startMark = new Mark("source", 0, 0, 0, emptyBuffer, 0);
        endMark = new Mark("source", 0, 0, 0, emptyBuffer, 0);
        flowStyle = DumperOptions.FlowStyle.FLOW;
        prebuiltEvent = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, flowStyle);
    }

    @Benchmark
    public SequenceStartEvent benchmarkConstructor() {
        return new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, flowStyle);
    }

    @Benchmark
    public Event.ID benchmarkGetEventId() {
        return prebuiltEvent.getEventId();
    }

    @Benchmark
    public String benchmarkGetAnchor() {
        return prebuiltEvent.getAnchor();
    }

    @Benchmark
    public String benchmarkGetTag() {
        return prebuiltEvent.getTag();
    }

    @Benchmark
    public Mark benchmarkGetStartMark() {
        return prebuiltEvent.getStartMark();
    }

    @Benchmark
    public Mark benchmarkGetEndMark() {
        return prebuiltEvent.getEndMark();
    }

    @Benchmark
    public DumperOptions.FlowStyle benchmarkGetFlowStyle() {
        return prebuiltEvent.getFlowStyle();
    }
}
