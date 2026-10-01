package bench.generated.c006;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.bloom.count.SuccinctCountingBloom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBloomBenchmark {

    // State field for the Bloom filter instance.
    // Since construction is expensive and creates a new object, we initialize it here.
    private SuccinctCountingBloom bloomFilter;

    // A fixed array of keys for construction. This is safe because we are only
    // benchmarking the performance of the methods, not the mutation of the state
    // across trials.
    private long[] keysForSetup;

    @Setup
    public void setup() {
        try {
            // Construct a filter instance. This is the expensive part we want to measure
            // if we were benchmarking construction, but here we reuse it for lookup tests.
            // We use a small set of keys.
            this.keysForSetup = new long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L};
            this.bloomFilter = SuccinctCountingBloom.construct(keysForSetup, 10.0);
        } catch (Exception e) {
            // Handle potential exceptions during construction if necessary, though unlikely here.
            System.err.println("Setup failed: " + e.getMessage());
        }
    }

    @Benchmark
    public void testMayContain(Blackhole bh) {
        // Test a lookup operation (read-only)
        bh.consume(bloomFilter.mayContain(1L));
    }

    @Benchmark
    public void testGetBitCount(Blackhole bh) {
        // Test a read operation
        bh.consume(bloomFilter.getBitCount());
    }

    @Benchmark
    public void testCardinality(Blackhole bh) {
        // Test a read operation
        bh.consume(bloomFilter.cardinality());
    }

    @Benchmark
    public void testAdd(Blackhole bh) {
        // Test a mutating operation. Since this modifies the state,
        // we rely on the fact that JMH isolates state per benchmark method
        // if we were to create a new instance, but here we test the method call itself.
        // We consume the void return value.
        bloomFilter.add(11L);
        bh.consume(null);
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        // Test a mutating operation
        bloomFilter.remove(1L);
        bh.consume(null);
    }
}
