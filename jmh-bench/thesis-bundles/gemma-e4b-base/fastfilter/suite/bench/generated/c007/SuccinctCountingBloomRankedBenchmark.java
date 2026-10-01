package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.bloom.count.SuccinctCountingBloomRanked;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBloomRankedBenchmark {

    private static final int KEY_COUNT = 1000;
    private static final int BITS_PER_KEY = 8;
    private static final int POOL_SIZE = 10;

    private long[] keys;
    private SuccinctCountingBloomRanked[] filterPool;
    private AtomicInteger poolIndex;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate keys
        keys = new long[KEY_COUNT];
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Initialize filter pool
        filterPool = new SuccinctCountingBloomRanked[POOL_SIZE];
        poolIndex = new AtomicInteger(0);

        // Initialize filters. We construct them fully populated for testing lookups,
        // and we will use them sequentially for mutating operations.
        for (int i = 0; i < POOL_SIZE; i++) {
            // Constructing a fully populated filter
            filterPool[i] = SuccinctCountingBloomRanked.construct(keys, BITS_PER_KEY);
        }
    }

    /**
     * Benchmarks the lookup operation (mayContain) on a pre-populated filter.
     */
    @Benchmark
    public boolean testMayContainLookup(Blackhole bh) {
        // Use a random key from the input set
        long key = keys[new Random().nextInt(KEY_COUNT)];
        
        // Use a random filter from the pool
        SuccinctCountingBloomRanked filter = filterPool[poolIndex.getAndIncrement() % POOL_SIZE];
        
        boolean result = filter.mayContain(key);
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks the addition operation (add) on a filter instance.
     * Since 'add' mutates the state, we cycle through the pool.
     * Note: For accurate single-operation timing, the filter should ideally be empty,
     * but cycling through the pool ensures we test the operation on a representative instance.
     */
    @Benchmark
    public void testAddOperation(Blackhole bh) {
        // Use a random key
        long key = keys[new Random().nextInt(KEY_COUNT)];
        
        // Get a filter instance
        SuccinctCountingBloomRanked filter = filterPool[poolIndex.getAndIncrement() % POOL_SIZE];
        
        // Perform the operation
        filter.add(key);
        
        // Consume the result (void method, so we consume the side effect implicitly, 
        // but we must consume something to prevent dead code elimination if the compiler is aggressive)
        bh.consume(filter); 
    }

    /**
     * Benchmarks the removal operation (remove) on a filter instance.
     * We use a key that is likely present in the pre-populated filter.
     */
    @Benchmark
    public void testRemoveOperation(Blackhole bh) {
        // Use a random key that was used in construction
        long key = keys[new Random().nextInt(KEY_COUNT)];
        
        // Get a filter instance
        SuccinctCountingBloomRanked filter = filterPool[poolIndex.getAndIncrement() % POOL_SIZE];
        
        // Perform the operation
        filter.remove(key);
        
        bh.consume(filter);
    }

    /**
     * Benchmarks the cardinality calculation.
     */
    @Benchmark
    public long testCardinality(Blackhole bh) {
        // Use a random filter from the pool
        SuccinctCountingBloomRanked filter = filterPool[new Random().nextInt(POOL_SIZE)];
        
        long cardinality = filter.cardinality();
        bh.consume(cardinality);
        return cardinality;
    }

    /**
     * Benchmarks the space usage calculation.
     */
    @Benchmark
    public long testBitCount(Blackhole bh) {
        // Use a random filter from the pool
        SuccinctCountingBloomRanked filter = filterPool[new Random().nextInt(POOL_SIZE)];
        
        long bitCount = filter.getBitCount();
        bh.consume(bitCount);
        return bitCount;
    }
}
