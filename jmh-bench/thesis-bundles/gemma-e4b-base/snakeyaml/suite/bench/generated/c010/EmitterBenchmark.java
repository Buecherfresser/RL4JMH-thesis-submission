package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class EmitterBenchmark {

    private Emitter emitter;
    private ByteArrayOutputStream outputStream;
    private DumperOptions options;

    // Input events for testing different paths
    private Event simpleScalarEvent;
    private Event complexMappingStartEvent;
    private Event streamStartEvent;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Setup DumperOptions
        options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setCanonical(false);

        // 2. Setup Output Stream
        outputStream = new ByteArrayOutputStream();
        java.io.Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        
        // 3. Setup Emitter
        emitter = new Emitter(writer, options);

        // 4. Setup Input Events
        
        // Simple Scalar Event (e.g., a basic string)
        // Required signature: String value, String tag, ImplicitTuple implicit, String anchor, Mark startMark, Mark endMark, ScalarStyle style
        simpleScalarEvent = new ScalarEvent("Hello World", null, null, null, null, null, ScalarStyle.PLAIN);

        // Complex Mapping Start Event (e.g., start of a dictionary)
        // Required signature: String tag, String anchor, boolean isFlow, Mark startMark, Mark endMark, FlowStyle style
        complexMappingStartEvent = new MappingStartEvent(null, null, false, null, null, FlowStyle.BLOCK);
        
        // Stream Start Event (start of the entire YAML stream)
        streamStartEvent = new StreamStartEvent(null, null);
    }

    @Benchmark
    public void benchmarkSimpleScalarEmission(Blackhole bh) throws IOException {
        // Test the path for emitting a single scalar value
        emitter.emit(simpleScalarEvent);
        bh.consume(outputStream.toString());
    }

    @Benchmark
    public void benchmarkMappingStartEmission(Blackhole bh) throws IOException {
        // Test the path for starting a mapping structure
        emitter.emit(complexMappingStartEvent);
        bh.consume(outputStream.toString());
    }

    @Benchmark
    public void benchmarkStreamStartEmission(Blackhole bh) throws IOException {
        // Test the path for starting the entire YAML stream
        emitter.emit(streamStartEvent);
        bh.consume(outputStream.toString());
    }
}
