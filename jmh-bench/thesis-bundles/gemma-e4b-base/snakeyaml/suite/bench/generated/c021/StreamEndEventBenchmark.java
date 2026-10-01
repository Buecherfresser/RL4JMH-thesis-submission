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

    private StreamEndEvent streamEndEvent;
    private Mark startMark;
    private Mark endMark;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize required dependencies (Marks).
        // Since Mark does not have a no-argument constructor, we use a constructor
        // that matches the required signature, providing dummy values.
        this.startMark = new Mark("dummy", 0, 0, 0, new char[0], 0);
        this.endMark = new Mark("dummy", 0, 0, 0, new char[0], 0);

        // Initialize the Subject Under Test
        this.streamEndEvent = new StreamEndEvent(startMark, endMark);
    }

    @Benchmark
    public void benchmark_getEventId(Blackhole bh) {
        // Measure the retrieval of the event ID
        ID id = streamEndEvent.getEventId();
        bh.consume(id);
    }

    @Benchmark
    public void benchmark_accessStartMark(Blackhole bh) {
        // Measure access to the start mark (inherited from Event)
        Mark start = ((Event) streamEndEvent).getStartMark();
        bh.consume(start);
    }

    @Benchmark
    public void benchmark_accessEndMark(Blackhole bh) {
        // Measure access to the end mark (inherited from Event)
        Mark end = ((Event) streamEndEvent).getEndMark();
        bh.consume(end);
    }

    @Benchmark
    public void benchmark_construction(Blackhole bh) {
        // Measure the cost of creating the event object
        StreamEndEvent event = new StreamEndEvent(startMark, endMark);
        bh.consume(event);
    }
}
