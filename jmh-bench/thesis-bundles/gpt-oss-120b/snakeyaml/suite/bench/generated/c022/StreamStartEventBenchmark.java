package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamStartEventBenchmark {

    private Mark startMark;
    private Mark endMark;
    private StreamStartEvent preCreatedEvent;

    @Setup(Level.Trial)
    public void setUp() {
        char[] buffer = "sample".toCharArray();
        startMark = new Mark("start", 0, 0, 0, buffer, 0);
        endMark = new Mark("end", 0, 0, 0, buffer, 0);
        preCreatedEvent = new StreamStartEvent(startMark, endMark);
    }

    @Benchmark
    public StreamStartEvent benchmarkConstructor() {
        return new StreamStartEvent(startMark, endMark);
    }

    @Benchmark
    public Event.ID benchmarkGetEventId() {
        return preCreatedEvent.getEventId();
    }

    @Benchmark
    public Mark benchmarkGetStartMark() {
        return preCreatedEvent.getStartMark();
    }

    @Benchmark
    public Mark benchmarkGetEndMark() {
        return preCreatedEvent.getEndMark();
    }
}
