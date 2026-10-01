package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
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
    private MappingEndEvent prebuiltEvent;

    @Setup
    public void setup() {
        // The Mark constructor requires an extra integer argument before the buffer.
        startMark = new Mark("start", 0, 0, 0, new char[0], 0);
        endMark = new Mark("end", 0, 0, 0, new char[0], 0);
        prebuiltEvent = new MappingEndEvent(startMark, endMark);
    }

    @Benchmark
    public MappingEndEvent constructEvent() {
        return new MappingEndEvent(startMark, endMark);
    }

    @Benchmark
    public Event.ID getEventId() {
        return prebuiltEvent.getEventId();
    }
}
