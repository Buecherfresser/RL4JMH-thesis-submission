package bench.generated.c065;

import org.apache.commons.compress.compressors.pack200.Pack200Utils;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Pack200UtilsBenchmark {

    // State fields for the benchmark inputs (Files)
    private File dummyFromJar;
    private File dummyToJar;
    private Map<String, String> props;

    @Setup
    public void setup() throws IOException {
        // Since the SUT requires File objects and filesystem operations,
        // we must create dummy files for the benchmark to execute the logic.
        // In a real scenario, these would be pre-built JARs.
        
        // Create dummy input and output files
        dummyFromJar = File.createTempFile("from_jar", ".jar");
        dummyToJar = File.createTempFile("to_jar", ".jar");
        
        // Ensure files exist (even if empty, to satisfy JarFile constructor)
        dummyFromJar.createNewFile();
        dummyToJar.createNewFile();

        // Setup properties map
        props = new HashMap<>();
        props.put(org.apache.commons.compress.java.util.jar.Pack200.Packer.SEGMENT_LIMIT, "-1");
    }

    @Benchmark
    public void normalize_from_to_with_props(Blackhole bh) throws IOException {
        // Benchmark the method that performs normalization from one file to another with properties.
        Pack200Utils.normalize(dummyFromJar, dummyToJar, props);
        bh.consume(null);
    }

    @Benchmark
    public void normalize_from_to_no_props(Blackhole bh) throws IOException {
        // Benchmark the method that performs normalization from one file to another without properties.
        Pack200Utils.normalize(dummyFromJar, dummyToJar, null);
        bh.consume(null);
    }

    @Benchmark
    public void normalize_in_place_with_props(Blackhole bh) throws IOException {
        // Benchmark the method that normalizes a JAR in-place with properties.
        Pack200Utils.normalize(dummyFromJar, dummyFromJar, props);
        bh.consume(null);
    }

    @Benchmark
    public void normalize_in_place_no_props(Blackhole bh) throws IOException {
        // Benchmark the method that normalizes a JAR in-place without properties.
        Pack200Utils.normalize(dummyFromJar, dummyFromJar, null);
        bh.consume(null);
    }
}
