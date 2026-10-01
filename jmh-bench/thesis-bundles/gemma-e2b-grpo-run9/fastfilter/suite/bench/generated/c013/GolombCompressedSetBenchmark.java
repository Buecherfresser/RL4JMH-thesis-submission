package bench.generated.c013;

import org.fastfilter.gcs.GolombCompressedSet;
import org.fastfilter.utils.Hash;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GolombCompressedSetBenchmark {

    // We use a static field to hold the constructed set if we want to measure
    // repeated lookups on the same object instance across benchmarks.
    private GolombCompressedSet gcsInstance;

    private final int SETTING = 10;

    @Setup
    public void setup() {
        try {
            // Construct the set once. This measures construction time.
            // We use a small, fixed set of keys for reproducibility.
            this.gcsInstance = GolombCompressedSet.construct(new long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L}, SETTING);
        } catch (Exception e) {
            // Ignore failure for benchmark purposes
        }
    }

    @Benchmark
    public void benchmarkMayContain(Blackhole bh) {
        if (gcsInstance == null) {
            // If setup failed, try to construct it here (though this is bad practice for JMH)
            try {
                gcsInstance = GolombCompressedSet.construct(new long[]{1L, 2L, 3L}, SETTING);
            } catch (Exception e) {
                // Ignore failure
            }
        }
        // Test containment for a key that is likely present (e.g., 1L)
        boolean result = gcsInstance.mayContain(1L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        if (gcsInstance == null) {
            // Ensure instance exists if setup failed
            try {
                gcsInstance = GolombCompressedSet.construct(new long[]{1L, 2L, 3L}, SETTING);
            } catch (Exception e) {
                // Ignore failure
            }
        }
        long count = gcsInstance.getBitCount();
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Measure the cost of building a new set instance every time.
        try {
            GolombCompressedSet.construct(new long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L}, SETTING);
        } catch (Exception e) {
            // Ignore failure
        }
    }
}
