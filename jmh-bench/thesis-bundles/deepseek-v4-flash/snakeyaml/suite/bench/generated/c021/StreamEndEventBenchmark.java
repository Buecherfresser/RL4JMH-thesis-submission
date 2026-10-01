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

    private Mark startMark;
    private Mark endMark;
    private StreamEndEvent event;

    @Setup(Level.Trial)
    public void setup() {
        startMark = new Mark("test", 0, 0, 0, new int[0], 0);
        endMark = new Mark("test", 1, 0, 1, new int[0], 0);
        event = new StreamEndEvent(startMark, endMark);
    }

    @Benchmark
    public StreamEndEvent construct() {
        return new StreamEndEvent(startMark, endMark);
    }

    @Benchmark
    public Event.ID getEventId() {
        return event.getEventId();
    }
}
