package bench.generated.c042;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.IOException;
import org.yaml.snakeyaml.serializer.Serializer;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.emitter.Emitable;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.nodes.Node;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SerializerBenchmark {

    private Node sampleNode;

    @Setup(Level.Trial)
    public void setUp() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        data.put("age", 30);
        data.put("active", true);
        Representer representer = new Representer(new DumperOptions());
        sampleNode = representer.represent(data);
    }

    private Serializer newSerializer() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos);
        DumperOptions options = new DumperOptions();
        Emitable emitter = new Emitter(writer, options);
        Resolver resolver = new Resolver();
        return new Serializer(emitter, resolver, options, null);
    }

    @Benchmark
    public void benchmarkOpen(Blackhole bh) throws IOException {
        Serializer serializer = newSerializer();
        serializer.open();
        bh.consume(serializer);
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        Serializer serializer = newSerializer();
        serializer.open();
        serializer.close();
        bh.consume(serializer);
    }

    @Benchmark
    public void benchmarkSerialize(Blackhole bh, SerializeState state) throws IOException {
        state.serializer.serialize(sampleNode);
        bh.consume(state.serializer);
    }

    @State(Scope.Benchmark)
    public static class SerializeState {
        Serializer serializer;

        @Setup(Level.Trial)
        public void doSetup() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(baos);
            DumperOptions options = new DumperOptions();
            Emitable emitter = new Emitter(writer, options);
            Resolver resolver = new Resolver();
            serializer = new Serializer(emitter, resolver, options, null);
            serializer.open();
        }
    }
}
