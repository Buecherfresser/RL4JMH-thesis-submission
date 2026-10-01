package bench.generated.c019;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SequenceEndEventBenchmark {

    private Mark startMark;
    private Mark endMark;
    private SequenceEndEvent sequenceEndEvent;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize required inputs (Marks) using a valid constructor
        // Mark(String name, int line, int column, int depth, char[] chars, int charOffset)
        startMark = new Mark("start", 0, 0, 0, new char[0], 0);
        endMark = new Mark("end", 1, 1, 1, new char[0], 0);
    }

    @Setup(Level.Invocation)
    public void setupEvent() {
        // Initialize the SUT instance for invocation-level tests
        sequenceEndEvent = new SequenceEndEvent(startMark, endMark);
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Test constructor performance
        SequenceEndEvent event = new SequenceEndEvent(startMark, endMark);
        bh.consume(event);
    }

    @Benchmark
    public void benchmarkGetEventId(Blackhole bh) {
        // Test method call performance
        bh.consume(sequenceEndEvent.getEventId());
    }
}
