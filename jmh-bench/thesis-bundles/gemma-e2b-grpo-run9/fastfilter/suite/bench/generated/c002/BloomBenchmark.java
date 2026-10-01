package bench.generated.c002;

import org.fastfilter.bloom.Bloom;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BloomBenchmark {

    private Bloom bloom;

    @Setup
    public void setup() {
        // Initialize a small Bloom filter instance.
        // We use a small number of keys and a standard bitsPerKey.
        try {
            // Constructing the filter involves hashing and adding keys.
            long[] keys = {1L, 2L, 3L, 4L, 5L};
            this.bloom = Bloom.construct(keys, 10.0);
        } catch (Exception e) {
            // Handle potential exceptions during construction if necessary,
            // though for this simple case, we ignore them for benchmarking purposes.
            System.err.println("Bloom construction failed: " + e.getMessage());
        }
    }

    @Benchmark
    public void mayContain(Blackhole bh) {
        // Test a key that might or might not be contained.
        bh.consume(this.bloom.mayContain(1L));
    }

    @Benchmark
    public void add(Blackhole bh) {
        // Test the mutating add operation.
        this.bloom.add(100L);
        bh.consume(null); // Void method consumes nothing.
    }

    @Benchmark
    public void getBitCount(Blackhole bh) {
        // Test a read-only method.
        bh.consume(this.bloom.getBitCount());
    }
}
