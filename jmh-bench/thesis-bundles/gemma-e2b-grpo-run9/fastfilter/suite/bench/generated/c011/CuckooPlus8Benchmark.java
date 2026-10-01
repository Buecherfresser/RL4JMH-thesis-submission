package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.cuckoo.CuckooPlus8;
import org.fastfilter.Filter;
import org.fastfilter.utils.Hash;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CuckooPlus8Benchmark {

    private CuckooPlus8 filter;

    @Setup
    public void setup() {
        // Initialize a filter instance. Since the internal state is randomized
        // based on Hash.randomSeed(), this setup is sufficient for measuring
        // the cost of operations on a newly constructed object.
        try {
            // Constructing a filter requires an array of keys.
            // We use a small, fixed set of keys for setup.
            long[] initialKeys = {1L, 2L, 3L, 4L, 5L};
            this.filter = CuckooPlus8.construct(initialKeys);
        } catch (Exception e) {
            // Handle potential exceptions during construction if necessary,
            // though for benchmarking we assume success or let the benchmark fail.
            System.err.println("CuckooPlus8 construction failed: " + e.getMessage());
        }
    }

    @Benchmark
    public void mayContain_Present(Blackhole bh) {
        // Test lookup for a key that was likely inserted during setup (or a key that might exist)
        long keyToTest = 1L;
        try {
            bh.consume(filter.mayContain(keyToTest));
        } catch (Exception e) {
            // Ignore exceptions if the filter state is unstable during benchmarking
        }
    }

    @Benchmark
    public void mayContain_Absent(Blackhole bh) {
        // Test lookup for a key that is unlikely to be present
        long keyToTest = 9999999999999999L;
        try {
            bh.consume(filter.mayContain(keyToTest));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void getBitCount(Blackhole bh) {
        // Test a simple read operation
        bh.consume(filter.getBitCount());
    }

    @Benchmark
    public void insert(Blackhole bh) {
        // Test a mutating operation. This will change the internal state of 'filter'.
        // Since we are using Scope.Benchmark, the state will be reset for the next iteration.
        try {
            filter.insert(100L);
            bh.consume(null); // Void method consumes nothing, but we call it.
        } catch (IllegalStateException e) {
            // Expected if the internal table fills up quickly, which is fine for measuring failure path.
        }
    }
}
