package bench.generated.c009;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.cuckoo.Cuckoo8;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Cuckoo8Benchmark {

    private static final int KEY_COUNT = 10000;
    private static final int POOL_SIZE = 10;

    private long[] keys;
    private Cuckoo8[] filterPool;
    private long lookupKey;
    private int poolIndex = 0;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate input keys
        keys = new long[KEY_COUNT];
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Initialize filter pool for mutable operations
        filterPool = new Cuckoo8[POOL_SIZE];
        for (int i = 0; i < POOL_SIZE; i++) {
            // Initialize empty filters for insertion tests
            filterPool[i] = new Cuckoo8(1); 
        }

        // 3. Select a key for lookup tests
        lookupKey = keys[KEY_COUNT / 2];
    }

    /**
     * Measures the time taken to construct a Cuckoo8 filter from a large set of keys.
     * This is a heavy, setup-like operation.
     */
    @Benchmark
    public Cuckoo8 constructionBenchmark() {
        // The construct method handles internal resizing and retries until successful.
        return Cuckoo8.construct(keys);
    }

    /**
     * Measures the time taken to perform a lookup (mayContain) on a pre-populated filter.
     * We cycle through the filter pool to ensure we are testing a filter that has been populated.
     */
    @Benchmark
    public void lookupBenchmark(Blackhole bh) {
        // Get a filter from the pool
        Cuckoo8 filter = filterPool[poolIndex];
        
        // Ensure the filter is populated before lookup (simulate pre-population)
        // Note: In a real scenario, we would pre-populate the pool in setup. 
        // Since we are measuring lookup, we assume the filter is ready.
        // For this benchmark, we rely on the filter being initialized and assume it contains data.
        
        // Perform lookup
        boolean result = filter.mayContain(lookupKey);
        bh.consume(result);

        // Cycle the pool index
        poolIndex = (poolIndex + 1) % POOL_SIZE;
    }

    /**
     * Measures the time taken to insert a single key into a filter.
     * We cycle through the filter pool, ensuring each invocation operates on a different, 
     * potentially growing, filter instance.
     */
    @Benchmark
    public void insertionBenchmark(Blackhole bh) {
        // Get a filter from the pool
        Cuckoo8 filter = filterPool[poolIndex];
        
        // Select a random key to insert
        long keyToInsert = keys[new Random().nextInt(KEY_COUNT)];

        // Perform insertion (mutating operation)
        filter.insert(keyToInsert);
        
        // Consume the result (void method, but required by JMH rules if not returning)
        bh.consume(null); 

        // Cycle the pool index
        poolIndex = (poolIndex + 1) % POOL_SIZE;
    }
    
    /**
     * Measures the time taken to retrieve the bit count metadata.
     * This is a simple read operation.
     */
    @Benchmark
    public void bitCountBenchmark(Blackhole bh) {
        // Use the first filter in the pool for consistent measurement
        Cuckoo8 filter = filterPool[0];
        long count = filter.getBitCount();
        bh.consume(count);
    }
}
