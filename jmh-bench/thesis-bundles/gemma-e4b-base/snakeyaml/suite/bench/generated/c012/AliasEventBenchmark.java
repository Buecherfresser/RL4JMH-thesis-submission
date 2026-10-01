package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class AliasEventBenchmark {

    private AliasEvent aliasEvent;

    @Setup(Level.Trial)
    public void setup() {
        // Prepare necessary inputs for AliasEvent construction
        String anchor = "myAnchor";
        
        // Mark requires complex construction based on compiler errors. 
        // We use dummy values to satisfy the constructor signature: 
        // Mark(String, int, int, int, char[], int)
        Mark startMark = new Mark(null, 0, 0, 0, new char[0], 0);
        Mark endMark = new Mark(null, 0, 0, 0, new char[0], 0);

        // Construct the subject under test once per trial
        aliasEvent = new AliasEvent(anchor, startMark, endMark);
    }

    @Benchmark
    public void benchmarkGetEventId(Blackhole bh) {
        // Test the getEventId() method
        Event.ID id = aliasEvent.getEventId();
        bh.consume(id);
    }
}
