package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockedBloomBenchmark {

    // State fields for the benchmark
    private org.fastfilter.bloom.BlockedBloom bloomFilter;
    private long[] keys;

    @Setup
    public void setup() {
        // Initialize a fixed set of keys for construction.
        try {
            // Use a small set of keys for fast construction
            this.keys = new long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L};
            // Construct the filter. This is acceptable for testing construction time.
            this.bloomFilter = org.fastfilter.bloom.BlockedBloom.construct(this.keys, 11);
        } catch (Exception e) {
            // Handle potential exceptions during setup if necessary
        }
    }

    @Benchmark
    public void testMayContain(Blackhole bh) {
        // Test the read-only operation
        bh.consume(bloomFilter.mayContain(1L));
    }

    @Benchmark
    public void testMayContainNegative(Blackhole bh) {
        // Test a key that is unlikely to be present
        bh.consume(bloomFilter.mayContain(9999999999999999L));
    }

    @Benchmark
    public void testAdd(Blackhole bh) {
        // Test the mutating operation (add)
        bloomFilter.add(11L);
        bh.consume(null); // Void method consumes nothing
    }

    @Benchmark
    public void testGetBitCount(Blackhole bh) {
        // Test a read-only property
        bh.consume(bloomFilter.getBitCount());
    }

    @Benchmark
    public void testConstruct(Blackhole bh) {
        // Test the static constructor
        try {
            org.fastfilter.bloom.BlockedBloom.construct(new long[]{1L, 2L}, 11);
        } catch (Exception e) {
            // Ignore exceptions for benchmark stability
        }
        bh.consume(null);
    }
}
