package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.StreamStartEvent;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamStartEventBenchmark {

    private Mark startMark;
    private Mark endMark;
    private StreamStartEvent event;

    @Setup(Level.Trial)
    public void setUp() {
        startMark = new Mark("bench", 0, 0, 0, new int[] {'a'}, 0);
        endMark = new Mark("bench", 0, 0, 0, new int[] {'z'}, 0);
        event = new StreamStartEvent(startMark, endMark);
    }

    @Benchmark
    public StreamStartEvent construct() {
        return new StreamStartEvent(startMark, endMark);
    }

    @Benchmark
    public Event.ID getEventId() {
        return event.getEventId();
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
    public String toString() {
        return event.toString();
    }
}
