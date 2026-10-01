package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.cuckoo.CuckooPlus16;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CuckooPlus16Benchmark {

    private long[] keys;
    private CuckooPlus16 filter;
    private long testKey;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate input keys
        Random random = new Random(42); // Fixed seed for reproducibility
        int keyCount = 1000;
        keys = new long[keyCount];
        for (int i = 0; i < keyCount; i++) {
            keys[i] = random.nextLong();
        }
        
        // Select a key to test containment later
        testKey = keys[0];

        // 2. Construct the filter once for lookup benchmarks
        // Note: This construction is expensive and is done only once per trial.
        filter = CuckooPlus16.construct(keys);
    }

    /**
     * Benchmarks the construction phase of the CuckooPlus16 filter.
     * This measures the time taken to insert all keys from the input array.
     */
    @Benchmark
    public CuckooPlus16 benchmarkConstruction(Blackhole bh) {
        // We must call the static construct method every time to measure the full build time.
        CuckooPlus16 newFilter = CuckooPlus16.construct(keys);
        bh.consume(newFilter);
        return newFilter;
    }

    /**
     * Benchmarks the lookup operation (mayContain) on a pre-constructed filter.
     */
    @Benchmark
    public boolean benchmarkMayContain(Blackhole bh) {
        // Use the filter instance prepared in @Setup
        boolean result = filter.mayContain(testKey);
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks the retrieval of the bit count (space usage).
     */
    @Benchmark
    public long benchmarkGetBitCount(Blackhole bh) {
        // Use the filter instance prepared in @Setup
        long result = filter.getBitCount();
        bh.consume(result);
        return result;
    }
}
