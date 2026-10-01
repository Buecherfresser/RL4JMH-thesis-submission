package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.Event.ID;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentEndEventBenchmark {

    private Mark startMark;
    private Mark endMark;
    private boolean explicit;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize inputs using a valid constructor for Mark
        // Mark constructor signature used: Mark(String, int, int, int, char[], int)
        startMark = new Mark("", 0, 0, 0, new char[0], 0);
        endMark = new Mark("", 0, 0, 0, new char[0], 0);
        explicit = true;
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Test construction
        DocumentEndEvent docEndEvent = new DocumentEndEvent(startMark, endMark, explicit);
        bh.consume(docEndEvent);
    }

    @Benchmark
    public void benchmarkGetExplicit(Blackhole bh) {
        // Test getter access
        DocumentEndEvent docEndEvent = new DocumentEndEvent(startMark, endMark, explicit);
        bh.consume(docEndEvent.getExplicit());
    }

    @Benchmark
    public void benchmarkGetEventId(Blackhole bh) {
        // Test event ID retrieval
        DocumentEndEvent docEndEvent = new DocumentEndEvent(startMark, endMark, explicit);
        bh.consume(docEndEvent.getEventId());
    }
}
