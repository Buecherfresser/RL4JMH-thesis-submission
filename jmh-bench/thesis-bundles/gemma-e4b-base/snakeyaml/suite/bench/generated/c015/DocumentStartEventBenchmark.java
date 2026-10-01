package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;

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

    private DocumentStartEvent documentStartEvent;
    private Mark startMark;
    private Mark endMark;
    private Version version;
    private Map<String, String> tags;

    @Setup(Level.Trial)
    public void setup() {
        // Setup inputs using a valid constructor for Mark: Mark(String, int, int, int, int[], int)
        startMark = new Mark("start", 0, 0, 0, new int[]{0}, 0);
        endMark = new Mark("end", 0, 0, 0, new int[]{0}, 0);
        
        // Use a common version
        version = Version.V1_1; 

        // Setup tags map
        tags = new HashMap<>();
        tags.put("!tag1", "prefix1");
        tags.put("!tag2", "prefix2");

        // Build the subject instance once
        documentStartEvent = new DocumentStartEvent(startMark, endMark, true, version, tags);
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Recreate the object in each invocation to measure construction cost
        DocumentStartEvent event = new DocumentStartEvent(startMark, endMark, true, version, tags);
        bh.consume(event);
    }

    @Benchmark
    public void benchmarkGetExplicit(Blackhole bh) {
        boolean explicit = documentStartEvent.getExplicit();
        bh.consume(explicit);
    }

    @Benchmark
    public void benchmarkGetVersion(Blackhole bh) {
        Version v = documentStartEvent.getVersion();
        bh.consume(v);
    }

    @Benchmark
    public void benchmarkGetTags(Blackhole bh) {
        Map<String, String> tagMap = documentStartEvent.getTags();
        // Consume the map size to ensure content access
        bh.consume(tagMap.size()); 
    }
}
