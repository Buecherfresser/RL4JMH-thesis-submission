package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.lz77support.Parameters;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParametersBenchmark {

    // State field to hold the Parameters instance.
    // Since Parameters is immutable and its construction is relatively cheap,
    // we can reuse a single instance for read-only operations.
    private Parameters parameters;

    @Setup
    public void setup() {
        // Initialize parameters with a reasonable window size (e.g., 1024, a power of two)
        // and default settings.
        try {
            this.parameters = Parameters.builder(1024).build();
        } catch (IllegalArgumentException e) {
            // Should not happen with 1024, but good practice.
            System.err.println("Failed to build Parameters: " + e.getMessage());
        }
    }

    @Benchmark
    public void getLazyMatching(Blackhole bh) {
        // Test a read-only method.
        bh.consume(this.parameters.getLazyMatching());
    }

    @Benchmark
    public void getLazyMatchingThreshold(Blackhole bh) {
        // Test a read-only method.
        bh.consume(this.parameters.getLazyMatchingThreshold());
    }

    @Benchmark
    public void getMaxBackReferenceLength(Blackhole bh) {
        // Test a read-only method.
        bh.consume(this.parameters.getMaxBackReferenceLength());
    }

    @Benchmark
    public void getMaxCandidates(Blackhole bh) {
        // Test a read-only method.
        bh.consume(this.parameters.getMaxCandidates());
    }

    @Benchmark
    public void getMaxLiteralLength(Blackhole bh) {
        // Test a read-only method.
        bh.consume(this.parameters.getMaxLiteralLength());
    }

    @Benchmark
    public void getMaxOffset(Blackhole bh) {
        // Test a read-only method.
        bh.consume(this.parameters.getMaxOffset());
    }

    @Benchmark
    public void getMinBackReferenceLength(Blackhole bh) {
        // Test a read-only method.
        bh.consume(this.parameters.getMinBackReferenceLength());
    }

    @Benchmark
    public void getNiceBackReferenceLength(Blackhole bh) {
        // Test a read-only method.
        bh.consume(this.parameters.getNiceBackReferenceLength());
    }

    @Benchmark
    public void getWindowSize(Blackhole bh) {
        // Test a read-only method.
        bh.consume(this.parameters.getWindowSize());
    }
}
