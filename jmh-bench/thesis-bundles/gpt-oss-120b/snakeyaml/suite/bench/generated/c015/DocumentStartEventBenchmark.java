package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.error.Mark;
import java.util.Map;
import java.util.HashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentStartEventBenchmark {

    private Mark startMark;
    private Mark endMark;
    private boolean explicit;
    private Map<String, String> tags;
    private DocumentStartEvent prebuiltEvent;

    @Setup
    public void setUp() {
        this.startMark = new Mark("start", 0, 0, 0, new char[0], 0);
        this.endMark = new Mark("end", 0, 0, 0, new char[0], 0);
        this.explicit = true;
        this.tags = new HashMap<>();
        this.tags.put("!foo!", "tag:example.com,2023:foo");
        this.tags.put("!bar!", "tag:example.com,2023:bar");
        this.prebuiltEvent = new DocumentStartEvent(startMark, endMark, explicit, null, tags);
    }

    @Benchmark
    public DocumentStartEvent benchmarkConstructor() {
        return new DocumentStartEvent(startMark, endMark, explicit, null, tags);
    }

    @Benchmark
    public boolean benchmarkGetExplicit() {
        return prebuiltEvent.getExplicit();
    }

    @Benchmark
    public Map<String, String> benchmarkGetTags() {
        return prebuiltEvent.getTags();
    }

    @Benchmark
    public void benchmarkConsumeAll(Blackhole bh) {
        bh.consume(prebuiltEvent.getExplicit());
        bh.consume(prebuiltEvent.getTags());
    }
}
