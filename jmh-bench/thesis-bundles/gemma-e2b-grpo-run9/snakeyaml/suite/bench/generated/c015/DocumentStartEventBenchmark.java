package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.DumperOptions.Version;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentStartEventBenchmark {

    @Benchmark
    public void benchmarkGetExplicit(Blackhole bh) {
        try {
            // Instantiate the event. We pass nulls for optional/complex fields.
            DocumentStartEvent event = new DocumentStartEvent(
                null, null, true, null, null
            );
            bh.consume(event.getExplicit());
        } catch (Exception e) {
            // Ignore exceptions during benchmark execution
        }
    }

    @Benchmark
    public void benchmarkGetVersion(Blackhole bh) {
        try {
            // Instantiate the event. Passing null for Version, relying on constructor handling.
            DocumentStartEvent event = new DocumentStartEvent(
                null, null, false, null, null
            );
            bh.consume(event.getVersion());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkGetTags(Blackhole bh) {
        try {
            // Create a map to satisfy the constructor requirement
            Map<String, String> tags = new HashMap<>();
            DocumentStartEvent event = new DocumentStartEvent(
                null, null, false, null, tags
            );
            bh.consume(event.getTags());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
