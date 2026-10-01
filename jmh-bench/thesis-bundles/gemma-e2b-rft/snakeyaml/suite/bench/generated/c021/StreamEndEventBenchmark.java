package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.Event.ID;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamEndEventBenchmark {

    private Mark setupStartMark;
    private Mark setupEndMark;
    private StreamEndEvent event;

    @Setup
    public void setup() {
        // Fix: Instantiate Mark objects with arguments to satisfy the compiler,
        // based on the constructor signatures reported in the compilation errors.
        // Assuming a constructor signature like Mark(String, int, int, int, char[], int) is required.
        this.setupStartMark = new Mark("start", 0, 0, 0, new char[0], 0);
        this.setupEndMark = new Mark("end", 0, 0, 0, new char[0], 0);
        this.event = new StreamEndEvent(setupStartMark, setupEndMark);
    }

    @Benchmark
    public void testGetEventId(Blackhole bh) {
        // Call the subject method exactly once per @Benchmark invocation.
        // Consume the result via Blackhole.
        ID id = event.getEventId();
        bh.consume(id);
    }
}
