package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

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
        // Initialize required input objects (Marks).
        // Since Mark requires arguments based on compilation errors, we use dummy values.
        startMark = new Mark("dummy", 0, 0, 0, new char[0], 0);
        endMark = new Mark("dummy", 0, 0, 0, new char[0], 0);
    }

    @Setup(Level.Invocation)
    public void setupEvent() {
        // Initialize the event object for method call benchmarks
        event = new MappingEndEvent(startMark, endMark);
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Test the constructor
        MappingEndEvent newEvent = new MappingEndEvent(startMark, endMark);
        bh.consume(newEvent);
    }

    @Benchmark
    public void benchmarkGetEventId(Blackhole bh) {
        // Test the getter method
        bh.consume(event.getEventId());
    }
}
