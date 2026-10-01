package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.events.Event;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MappingStartEventBenchmark {

    private MappingStartEvent mappingStartEvent;
    private String anchor;
    private String tag;
    private boolean implicit;
    private Mark startMark;
    private Mark endMark;
    private DumperOptions.FlowStyle flowStyle;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize inputs
        anchor = "myAnchor";
        tag = "tag:yaml.org,2002:map";
        implicit = false;
        
        // Construct minimal Marks using the complex constructor signature:
        // Mark(String, int, int, int, char[], int)
        // We use dummy values for the complex fields (char[] and the third int)
        startMark = new Mark("", 1, 1, 0, new char[0], 0);
        endMark = new Mark("", 2, 1, 0, new char[0], 0);

        flowStyle = DumperOptions.FlowStyle.BLOCK;

        // Construct the subject once per trial
        mappingStartEvent = new MappingStartEvent(
                anchor, tag, implicit, startMark, endMark, flowStyle
        );
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Re-construct the event in the benchmark to measure construction time
        MappingStartEvent event = new MappingStartEvent(
                anchor, tag, implicit, startMark, endMark, flowStyle
        );
        bh.consume(event);
    }

    @Benchmark
    public void benchmarkGetEventId(Blackhole bh) {
        // Test the getter method
        Event.ID id = mappingStartEvent.getEventId();
        bh.consume(id);
    }
}
