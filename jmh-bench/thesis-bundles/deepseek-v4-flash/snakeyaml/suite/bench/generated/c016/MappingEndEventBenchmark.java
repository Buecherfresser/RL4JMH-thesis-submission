package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MappingEndEventBenchmark {

    private Mark startMark;
    private Mark endMark;
    private MappingEndEvent event;

    @Setup(Level.Trial)
    public void setup() {
        int[] buffer = "key: value".chars().toArray();
        startMark = new Mark("test.yaml", 0, 0, 0, buffer, 0);
        endMark = new Mark("test.yaml", 10, 0, 10, buffer, 10);
        event = new MappingEndEvent(startMark, endMark);
    }

    @Benchmark
    public MappingEndEvent construct() {
        return new MappingEndEvent(startMark, endMark);
    }

    @Benchmark
    public Event.ID getEventId() {
        return event.getEventId();
    }
}
