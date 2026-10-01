package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import org.fastfilter.cuckoo.CuckooPlus16;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CuckooPlus16Benchmark {

    // State fields for the CuckooPlus16 instance
    private CuckooPlus16 cuckooFilter;

    // Fixed keys for construction/lookup tests
    private long[] keysForConstruction;

    // Helper method to construct the filter
    private void setupFilter(int capacity) {
        try {
            // Call the static constructor method
            this.cuckooFilter = CuckooPlus16.construct(keysForConstruction);
        } catch (Exception e) {
            // Ignore construction failures for benchmarking purposes
        }
    }

    @Setup
    public void setup() {
        // Build a set of keys for construction.
        this.keysForConstruction = new long[100];
        Random random = new Random(42); // Use a fixed seed for reproducibility
        for (int i = 0; i < 100; i++) {
            this.keysForConstruction[i] = random.nextLong();
        }
        // Initialize the filter instance.
        setupFilter(100);
    }

    @Benchmark
    public void benchmarkMayContain(Blackhole bh) {
        // Test lookup on a key that is likely present or absent
        bh.consume(cuckooFilter.mayContain(keysForConstruction[0]));
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        // Test a method that doesn't mutate state
        bh.consume(cuckooFilter.getBitCount());
    }

    @Benchmark
    public void benchmarkInsert(Blackhole bh) {
        // Create a fresh filter instance for mutation testing
        try {
            // Reconstruct the filter every time to test insertion performance
            CuckooPlus16 mutableFilter = CuckooPlus16.construct(keysForConstruction);
            mutableFilter.insert(keysForConstruction[0]);
        } catch (Exception e) {
            // Ignore exceptions during mutation testing
        }
    }

    @Benchmark
    public void benchmarkMayContainAfterInsert(Blackhole bh) {
        // Test lookup after a single insertion
        try {
            CuckooPlus16 mutableFilter = CuckooPlus16.construct(keysForConstruction);
            mutableFilter.insert(keysForConstruction[0]);
            bh.consume(mutableFilter.mayContain(keysForConstruction[0]));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
