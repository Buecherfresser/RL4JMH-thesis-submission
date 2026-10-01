package bench.generated.c008;

import org.fastfilter.cuckoo.Cuckoo16;
import org.fastfilter.Filter;
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
public class Cuckoo16Benchmark {

    // State fields for the benchmark
    private Cuckoo16 cuckooFilter;

    @Setup
    public void setup() {
        // Initialize a filter instance. Since Cuckoo16 is mutable,
        // we create a new one here to ensure a clean state for each benchmark run
        // if we were testing mutation, although for static methods, this is less critical.
        try {
            // Constructing a filter is expensive, so we only do it once per trial setup
            this.cuckooFilter = Cuckoo16.construct(new long[]{1L, 2L, 3L, 4L, 5L});
        } catch (Exception e) {
            // Handle potential construction failure if necessary, though unlikely for this simple case
        }
    }

    @Benchmark
    public void benchmarkMayContain(Blackhole bh) {
        // Test a read-only operation on the pre-initialized filter
        bh.consume(this.cuckooFilter.mayContain(10000000000L));
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        // Test another read-only operation
        bh.consume(this.cuckooFilter.getBitCount());
    }

    @Benchmark
    public void benchmarkInsert(Blackhole bh) {
        // Test a mutating operation. Since we are using a state field
        // initialized in @Setup, this tests the cost of insertion into an existing structure.
        try {
            this.cuckooFilter.insert(99999999999L);
        } catch (IllegalStateException e) {
            // Ignore table full exceptions for benchmarking purposes
        }
        bh.consume(null); // Void method consumes nothing
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Test the static construction method. This is a heavy operation.
        try {
            Cuckoo16.construct(new long[]{1L, 2L, 3L, 4L, 5L});
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }
}
