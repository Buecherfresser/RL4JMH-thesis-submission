package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MappingStartEventBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        String anchor;
        String tag;
        boolean implicit;
        Mark startMark;
        Mark endMark;
        DumperOptions.FlowStyle flowStyle;
        MappingStartEvent prebuiltEvent;

        @Setup(Level.Trial)
        public void setUp() {
            anchor = "anchor";
            tag = "tag:yaml.org,2002:map";
            implicit = true;
            // Use the char[] constructor to avoid ambiguity
            startMark = new Mark("test", 0, 0, 0, new char[0], 0);
            endMark = new Mark("test", 0, 0, 0, new char[0], 0);
            flowStyle = DumperOptions.FlowStyle.BLOCK;
            prebuiltEvent = new MappingStartEvent(anchor, tag, implicit, startMark, endMark, flowStyle);
        }
    }

    @Benchmark
    public MappingStartEvent benchmarkConstructor(BenchmarkState s) {
        return new MappingStartEvent(s.anchor, s.tag, s.implicit, s.startMark, s.endMark, s.flowStyle);
    }

    @Benchmark
    public Event.ID benchmarkGetEventId(BenchmarkState s) {
        return s.prebuiltEvent.getEventId();
    }

    @Benchmark
    public String benchmarkGetAnchor(BenchmarkState s) {
        return s.prebuiltEvent.getAnchor();
    }

    @Benchmark
    public String benchmarkGetTag(BenchmarkState s) {
        return s.prebuiltEvent.getTag();
    }

    @Benchmark
    public DumperOptions.FlowStyle benchmarkGetFlowStyle(BenchmarkState s) {
        return s.prebuiltEvent.getFlowStyle();
    }

    @Benchmark
    public Mark benchmarkGetStartMark(BenchmarkState s) {
        return s.prebuiltEvent.getStartMark();
    }

    @Benchmark
    public Mark benchmarkGetEndMark(BenchmarkState s) {
        return s.prebuiltEvent.getEndMark();
    }
}
