package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.cuckoo.Cuckoo16;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Cuckoo16Benchmark {

    private static final int KEY_COUNT = 10000;
    private static final int INSERTION_POOL_SIZE = 10;

    // Input data
    private long[] inputKeys;
    private long[] lookupKeys;

    // State for benchmarks
    private Cuckoo16[] filterPool;
    private int currentFilterIndex = 0;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(42); // Fixed seed for reproducibility

        // 1. Generate input keys for construction and insertion
        inputKeys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            inputKeys[i] = random.nextLong();
        }

        // 2. Generate lookup keys (some present, some absent)
        lookupKeys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            // Mix in some keys that are definitely in the input set
            if (i % 5 == 0) {
                lookupKeys[i] = inputKeys[i % KEY_COUNT];
            } else {
                lookupKeys[i] = random.nextLong();
            }
        }

        // 3. Initialize filter pool for insertion benchmarks
        filterPool = new Cuckoo16[INSERTION_POOL_SIZE];
        for (int i = 0; i < INSERTION_POOL_SIZE; i++) {
            // Construct a fresh filter for each slot in the pool
            filterPool[i] = Cuckoo16.construct(inputKeys);
        }
    }

    /**
     * Benchmarks the construction of the Cuckoo16 filter from a large set of keys.
     */
    @Benchmark
    public Cuckoo16 benchmarkConstruction(Blackhole bh) {
        // Since construct is static and takes the input keys, we measure the entire construction process.
        Cuckoo16 filter = Cuckoo16.construct(inputKeys);
        bh.consume(filter);
        return filter;
    }

    /**
     * Benchmarks the lookup operation (mayContain) on a pre-built filter.
     * We cycle through the filter pool to ensure we are testing against a fresh, fully populated filter.
     */
    @Benchmark
    public void benchmarkLookup(Blackhole bh) {
        // Get a fresh filter from the pool
        Cuckoo16 filter = filterPool[currentFilterIndex];
        currentFilterIndex = (currentFilterIndex + 1) % INSERTION_POOL_SIZE;

        // Pick a random key from the lookup set
        long key = lookupKeys[new Random(1).nextInt(lookupKeys.length)];

        // Perform the lookup
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }

    /**
     * Benchmarks the insertion operation (insert) on a filter instance.
     * We cycle through the filter pool, ensuring each benchmark invocation uses a fresh, fully constructed filter
     * before the insertion happens, preventing unbounded growth and state distortion.
     */
    @Benchmark
    public void benchmarkInsertion(Blackhole bh) {
        // Get a fresh filter from the pool
        Cuckoo16 filter = filterPool[currentFilterIndex];
        currentFilterIndex = (currentFilterIndex + 1) % INSERTION_POOL_SIZE;

        // Pick a random key to insert
        long key = inputKeys[new Random(1).nextInt(inputKeys.length)];

        // Perform the insertion
        filter.insert(key);
        bh.consume(filter); // Consume the filter reference to prevent dead code elimination
    }

    /**
     * Benchmarks the metadata retrieval (getBitCount).
     */
    @Benchmark
    public long benchmarkGetBitCount(Blackhole bh) {
        // Use a filter from the pool
        Cuckoo16 filter = filterPool[new Random(1).nextInt(INSERTION_POOL_SIZE)];
        
        long bits = filter.getBitCount();
        bh.consume(bits);
        return bits;
    }
}
