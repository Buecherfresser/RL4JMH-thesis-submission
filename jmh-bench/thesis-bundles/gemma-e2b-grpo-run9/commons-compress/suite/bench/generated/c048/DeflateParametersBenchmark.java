package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DeflateParametersBenchmark {

    // State field to hold the instance of the class under test.
    // Since we are testing method calls on a simple object,
    // initializing it here or in @Setup is acceptable.
    private DeflateParameters parameters;

    @Setup
    public void setup() {
        // Initialize the parameters object once per benchmark run (or thread, depending on JMH configuration)
        this.parameters = new DeflateParameters();
    }

    @Benchmark
    public void testGetCompressionLevel(Blackhole bh) {
        // Test reading a value. We consume the result via Blackhole.
        int level = this.parameters.getCompressionLevel();
        bh.consume(level);
    }

    @Benchmark
    public void testSetCompressionLevel(Blackhole bh) {
        // Test setting a value. We don't need to consume the void return.
        try {
            this.parameters.setCompressionLevel(5);
        } catch (IllegalArgumentException e) {
            // Ignore expected exceptions for this simple benchmark test
        }
    }

    @Benchmark
    public void testSetCompressionLevelInvalid(Blackhole bh) {
        // Test setting an invalid value to ensure the method executes without crashing
        try {
            this.parameters.setCompressionLevel(10); // MAX_LEVEL is 9
        } catch (IllegalArgumentException e) {
            // Expected exception
        }
    }

    @Benchmark
    public void testSetWithZlibHeaderTrue(Blackhole bh) {
        // Test setting the boolean flag to true
        this.parameters.setWithZlibHeader(true);
        bh.consume(true); // Consume the result of the operation (implicitly void, but good practice if it returned something)
    }

    @Benchmark
    public void testGetWithZlibHeader(Blackhole bh) {
        // Test reading the boolean flag
        bh.consume(this.parameters.withZlibHeader());
    }
}
