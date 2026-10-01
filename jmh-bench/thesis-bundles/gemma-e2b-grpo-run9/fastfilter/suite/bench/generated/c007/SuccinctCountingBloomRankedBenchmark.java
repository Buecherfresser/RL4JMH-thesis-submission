package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.fastfilter.bloom.count.SuccinctCountingBloomRanked;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBloomRankedBenchmark {

    // State field for the filter instance.
    private SuccinctCountingBloomRanked filter;

    @Setup
    public void setup() {
        // Initialize a filter instance. We use a small set of keys.
        try {
            // Using a small number of keys and a reasonable bitsPerKey setting.
            this.filter = SuccinctCountingBloomRanked.construct(new long[]{1L, 2L, 3L, 4L, 5L}, 10);
        } catch (Exception e) {
            // Handle potential exceptions during construction if necessary.
        }
    }

    @Benchmark
    public void testMayContain(Blackhole bh) {
        // Test a lookup operation.
        if (filter != null) {
            bh.consume(filter.mayContain(1L));
        }
    }

    @Benchmark
    public void testMayContainAbsent(Blackhole bh) {
        // Test a lookup for a key unlikely to be present.
        if (filter != null) {
            bh.consume(filter.mayContain(9999999999999999L));
        }
    }

    @Benchmark
    public void testAdd(Blackhole bh) {
        // Test an addition operation. This mutates the internal state.
        if (filter != null) {
            filter.add(100L);
        }
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        // Test a removal operation. This mutates the internal state.
        if (filter != null) {
            filter.remove(1L);
        }
    }

    @Benchmark
    public void testCardinality(Blackhole bh) {
        // Test the cardinality calculation.
        if (filter != null) {
            bh.consume(filter.cardinality());
        }
    }

    // Benchmarking the static constructor path (if we wanted to measure construction time)
    @Benchmark
    public void testConstructStatic(Blackhole bh) {
        try {
            // Reconstruct the filter every time to measure construction time.
            SuccinctCountingBloomRanked.construct(new long[]{1L, 2L, 3L}, 10);
        } catch (Exception e) {
            // Ignore exceptions for this benchmark if they occur.
        }
    }
}
