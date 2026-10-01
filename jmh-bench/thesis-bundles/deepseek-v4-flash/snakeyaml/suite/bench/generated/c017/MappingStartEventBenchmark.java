package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingStartEvent;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MappingStartEventBenchmark {

    private MappingStartEvent event;
    private MappingStartEvent otherEvent;
    private String anchor;
    private String tag;
    private Mark startMark;
    private Mark endMark;
    private DumperOptions.FlowStyle flowStyle;

    @Setup(Level.Trial)
    public void setup() {
        anchor = "anchor";
        tag = "tag:yaml.org,2002:map";
        char[] chars = "line one\nline two\n".toCharArray();
        startMark = new Mark("input", 0, 0, 0, chars, 0);
        endMark = new Mark("input", 9, 1, 0, chars, 9);
        flowStyle = DumperOptions.FlowStyle.BLOCK;
        event = new MappingStartEvent(anchor, tag, true, startMark, endMark, flowStyle);
        otherEvent = new MappingStartEvent(anchor, tag, true, startMark, endMark, flowStyle);
    }

    @Benchmark
    public MappingStartEvent newMappingStartEvent() {
        return new MappingStartEvent(anchor, tag, true, startMark, endMark, flowStyle);
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
    public String toStringBenchmark() {
        return event.toString();
    }

    @Benchmark
    public boolean equalsBenchmark() {
        return event.equals(otherEvent);
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return event.hashCode();
    }
}
