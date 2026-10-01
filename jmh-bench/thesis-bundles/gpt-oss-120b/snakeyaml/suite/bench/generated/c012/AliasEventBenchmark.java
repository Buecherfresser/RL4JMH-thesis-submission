package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class AliasEventBenchmark {

    private String anchor;
    private Mark startMark;
    private Mark endMark;
    private AliasEvent aliasEvent;

    @Setup(Level.Trial)
    public void setUp() {
        anchor = "anchor1";
        char[] buffer = new char[0];
        // Mark constructor requires an additional buffer index argument
        startMark = new Mark("test", 0, 0, 0, buffer, 0);
        endMark = new Mark("test", 0, 0, 0, buffer, 0);
        aliasEvent = new AliasEvent(anchor, startMark, endMark);
    }

    @Benchmark
    public AliasEvent benchConstructor() {
        return new AliasEvent(anchor, startMark, endMark);
    }

    @Benchmark
    public Event.ID benchGetEventId() {
        return aliasEvent.getEventId();
    }

    @Benchmark
    public String benchGetAnchor() {
        return aliasEvent.getAnchor();
    }

    @Benchmark
    public Mark benchGetStartMark() {
        return aliasEvent.getStartMark();
    }

    @Benchmark
    public Mark benchGetEndMark() {
        return aliasEvent.getEndMark();
    }
}
