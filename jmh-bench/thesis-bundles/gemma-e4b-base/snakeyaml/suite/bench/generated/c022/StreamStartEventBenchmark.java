package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamStartEventBenchmark {

    private StreamStartEvent streamStartEvent;
    private Mark startMark;
    private Mark endMark;

    @Setup(Level.Trial)
    public void setup() {
        // Fix: Mark requires specific arguments in its constructor.
        // We use dummy values to satisfy the constructor signature:
        // Mark(String, int, int, int, char[], int)
        
        char[] dummyChars = new char[0];
        
        startMark = new Mark("start", 0, 0, 0, dummyChars, 0);
        endMark = new Mark("end", 0, 0, 0, dummyChars, 0);
        
        // Construct the subject under test once
        streamStartEvent = new StreamStartEvent(startMark, endMark);
    }

    /**
     * Benchmarks the retrieval of the Event ID from the StreamStartEvent.
     */
    @Benchmark
    public void benchmarkGetEventId(Blackhole bh) {
        Event.ID id = streamStartEvent.getEventId();
        bh.consume(id);
    }
    
    /**
     * Benchmarks the internal state access (though this is usually covered by getEventId,
     * we ensure the object itself is accessed).
     */
    @Benchmark
    public void benchmarkObjectAccess(Blackhole bh) {
        // Accessing the object itself to prevent dead code elimination
        bh.consume(streamStartEvent);
    }
}
