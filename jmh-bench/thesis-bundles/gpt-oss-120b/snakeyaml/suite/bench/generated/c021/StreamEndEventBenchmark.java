package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamEndEventBenchmark {

    private StreamEndEvent event;
    private Mark startMark;
    private Mark endMark;

    @Setup(Level.Trial)
    public void setUp() {
        char[] emptyBuffer = new char[0];
        this.startMark = new Mark("start", 0, 0, 0, emptyBuffer, 0);
        this.endMark = new Mark("end", 0, 0, 0, emptyBuffer, 0);
        this.event = new StreamEndEvent(startMark, endMark);
    }

    @Benchmark
    public StreamEndEvent benchmarkCreateEvent() {
        return new StreamEndEvent(startMark, endMark);
    }

    @Benchmark
    public Event.ID benchmarkGetEventId() {
        return event.getEventId();
    }

    @Benchmark
    public Mark benchmarkGetStartMark() {
        return event.getStartMark();
    }

    @Benchmark
    public Mark benchmarkGetEndMark() {
        return event.getEndMark();
    }
}
