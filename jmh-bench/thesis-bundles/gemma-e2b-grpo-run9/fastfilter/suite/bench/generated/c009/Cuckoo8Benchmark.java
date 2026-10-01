package bench.generated.c009;

import org.fastfilter.cuckoo.Cuckoo8;
import org.fastfilter.utils.Hash;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Cuckoo8Benchmark {

    // State fields for reusable objects or inputs
    private Cuckoo8 cuckooFilter;

    @Setup
    public void setup() {
        // Initialize a small, fixed Cuckoo8 instance for lookup/insert tests
        try {
            // Construct a filter with a small capacity. This is expensive, so we do it once.
            this.cuckooFilter = new Cuckoo8(100);
        } catch (Exception e) {
            // Ignore exceptions during setup if the filter construction fails for some reason
        }
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Test the static construction method.
        // We use a small set of keys to keep construction time reasonable.
        try {
            Cuckoo8.construct(new long[]{1L, 2L, 3L, 4L, 5L});
        } catch (Exception e) {
            // Ignore exceptions during construction
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkMayContain(Blackhole bh) {
        // Test a read-only operation on the pre-initialized filter.
        // We use a key that is unlikely to be present, or one that might be.
        try {
            cuckooFilter.mayContain(1234567890123L);
        } catch (Exception e) {
            // Ignore exceptions during lookup
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkInsert(Blackhole bh) {
        // Test a mutating operation. Since Cuckoo8 is mutable, this modifies the state.
        // We rely on JMH isolation, but this test measures the cost of insertion.
        try {
            cuckooFilter.insert(9876543210L);
        } catch (IllegalStateException e) {
            // Expected if the filter fills up quickly
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        // Test a read-only method that returns a value.
        try {
            long count = cuckooFilter.getBitCount();
            bh.consume(count);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
