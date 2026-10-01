package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.SequenceStartEvent;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SequenceStartEventBenchmark {

    private SequenceStartEvent event;
    private String anchor;
    private String tag;
    private Mark startMark;
    private Mark endMark;
    private DumperOptions.FlowStyle flowStyle;

    @Setup(Level.Trial)
    public void setUp() {
        anchor = "seqAnchor";
        tag = "tag:yaml.org,2002:seq";
        startMark = new Mark("test", 0, 0, 0, new int[]{10}, 0);
        endMark = new Mark("test", 1, 0, 1, new int[]{10}, 1);
        flowStyle = DumperOptions.FlowStyle.BLOCK;
        event = new SequenceStartEvent(anchor, tag, true, startMark, endMark, flowStyle);
    }

    @Benchmark
    public SequenceStartEvent constructor() {
        return new SequenceStartEvent(anchor, tag, true, startMark, endMark, flowStyle);
    }

    @Benchmark
    public Event.ID getEventId() {
        return event.getEventId();
    }

    @Benchmark
    public String getAnchor() {
        return event.getAnchor();
    }

    @Benchmark
    public String getTag() {
        return event.getTag();
    }

    @Benchmark
    public Mark getStartMark() {
        return event.getStartMark();
    }

    @Benchmark
    public Mark getEndMark() {
        return event.getEndMark();
    }

    @Benchmark
    public DumperOptions.FlowStyle getFlowStyle() {
        return event.getFlowStyle();
    }

    @Benchmark
    public String toString() {
        return event.toString();
    }
}
