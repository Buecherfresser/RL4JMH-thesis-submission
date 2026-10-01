package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentEndEventBenchmark {

    private Mark startMark;
    private Mark endMark;
    private DocumentEndEvent explicitEvent;
    private DocumentEndEvent implicitEvent;

    @Setup(Level.Trial)
    public void setUp() {
        char[] buffer = new char[0];
        startMark = new Mark("start", 0, 0, 0, buffer, 0);
        endMark = new Mark("end", 0, 0, 0, buffer, 0);
        explicitEvent = new DocumentEndEvent(startMark, endMark, true);
        implicitEvent = new DocumentEndEvent(startMark, endMark, false);
    }

    @Benchmark
    public DocumentEndEvent createExplicit() {
        return new DocumentEndEvent(startMark, endMark, true);
    }

    @Benchmark
    public DocumentEndEvent createImplicit() {
        return new DocumentEndEvent(startMark, endMark, false);
    }

    @Benchmark
    public boolean getExplicitExplicit() {
        return explicitEvent.getExplicit();
    }

    @Benchmark
    public boolean getExplicitImplicit() {
        return implicitEvent.getExplicit();
    }

    @Benchmark
    public Event.ID getEventIdExplicit() {
        return explicitEvent.getEventId();
    }

    @Benchmark
    public Event.ID getEventIdImplicit() {
        return implicitEvent.getEventId();
    }
}
