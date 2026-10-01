package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.SequenceStartEvent;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SequenceStartEventBenchmark {

    // State fields for inputs
    private String anchor;
    private String tag;
    private boolean implicit;
    private Mark startMark;
    private Mark endMark;
    private DumperOptions.FlowStyle flowStyle;

    @Setup
    public void setup() {
        // Build fixed payloads once in @Setup
        this.anchor = "test_anchor";
        this.tag = "!!seq";
        this.implicit = false;
        
        // Initialize Mark objects to null to avoid constructor issues if Mark lacks a no-arg constructor
        this.startMark = null; 
        this.endMark = null;
        
        this.flowStyle = DumperOptions.FlowStyle.BLOCK;
    }

    @Benchmark
    public void createSequenceStartEvent(Blackhole bh) {
        // Call the subject method exactly once per invocation
        SequenceStartEvent event = new SequenceStartEvent(
                anchor,
                tag,
                implicit,
                startMark,
                endMark,
                flowStyle
        );
        bh.consume(event);
    }

    @Benchmark
    public void getEventId(Blackhole bh) {
        // Test the overridden method
        SequenceStartEvent event = new SequenceStartEvent(
                anchor,
                tag,
                implicit,
                startMark,
                endMark,
                flowStyle
        );
        bh.consume(event.getEventId());
    }
}
