package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.error.YAMLException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamReaderBenchmark {

    // Since StreamReader is mutable and stateful, we create it inside the benchmark
    // or rely on the fact that JMH creates a new instance for each benchmark method
    // if we don't use @State fields.

    @Benchmark
    public void testStaticIsPrintable(Blackhole bh) {
        // Test static method, which is stateless
        boolean result = StreamReader.isPrintable("valid yaml content");
        bh.consume(result);
    }

    @Benchmark
    public void testConstructor(Blackhole bh) {
        // Test constructor initialization cost
        try {
            new StreamReader("some yaml string");
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected during setup
        }
        bh.consume(null);
    }

    @Benchmark
    public void testGetIndex(Blackhole bh) {
        // Test a simple getter on a fresh instance
        try {
            StreamReader reader = new StreamReader("some yaml string");
            bh.consume(reader.getIndex());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testPeek(Blackhole bh) {
        // Test peek on a fresh instance
        try {
            StreamReader reader = new StreamReader("some yaml string");
            bh.consume(reader.peek());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testPrefix(Blackhole bh) {
        // Test prefix on a fresh instance
        try {
            StreamReader reader = new StreamReader("some yaml string");
            bh.consume(reader.prefix(10));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testForward(Blackhole bh) {
        // Test forward operation (which modifies internal state)
        try {
            StreamReader reader = new StreamReader("some yaml string");
            reader.forward(5);
            bh.consume(null);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
