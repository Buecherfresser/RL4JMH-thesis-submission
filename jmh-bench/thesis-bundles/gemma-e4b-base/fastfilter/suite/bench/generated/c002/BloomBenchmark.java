package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.bloom.Bloom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BloomBenchmark {

    private long[] inputKeys;
    private final double bitsPerKey = 10.0;
    private final int keyCount = 10000;

    // State for lookup/add tests
    private Bloom bloomInstance;
    private long keyToTest;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate input keys
        inputKeys = new long[keyCount];
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < keyCount; i++) {
            inputKeys[i] = random.nextLong();
        }

        // 2. Initialize a base bloom instance for lookup tests
        // We construct it fully here, as lookup is read-only.
        bloomInstance = Bloom.construct(inputKeys, bitsPerKey);

        // 3. Define a key to test
        keyToTest = random.nextLong();
    }

    /**
     * Benchmarks the construction time of the Bloom filter.
     * Since construction is expensive and involves iteration over all keys,
     * we measure the time taken to build the filter from scratch.
     */
    @Benchmark
    public Bloom constructBloom() {
        // We must call the static method here to measure construction time.
        // We consume the result to prevent dead code elimination.
        Bloom result = Bloom.construct(inputKeys, bitsPerKey);
        return result;
    }

    /**
     * Benchmarks the time taken to check if a key may be contained (lookup).
     * Uses the pre-constructed bloomInstance.
     */
    @Benchmark
    public void mayContainKey(Blackhole bh) {
        boolean result = bloomInstance.mayContain(keyToTest);
        // Consume the result using the Blackhole instance
        bh.consume(result);
    }

    /**
     * Benchmarks the time taken to add a single key to the Bloom filter.
     * Since 'add' mutates the state, we must ensure the state is clean for each invocation.
     * We achieve this by creating a fresh instance for each invocation.
     */
    @Benchmark
    public void addKey(Blackhole bh) {
        // Create a fresh, empty bloom instance for this invocation
        // We use a minimal key set (just the key being added) to ensure the filter is initialized correctly.
        Bloom freshBloom = Bloom.construct(new long[]{keyToTest}, bitsPerKey);
        
        // Now, call the add method on the fresh instance
        freshBloom.add(keyToTest);
        
        // Consume the object to prevent dead code elimination
        bh.consume(freshBloom);
    }
    
    /**
     * Benchmarks the time taken to retrieve the total bit count of the filter.
     * This is a simple getter operation.
     */
    @Benchmark
    public void getBitCount(Blackhole bh) {
        long count = bloomInstance.getBitCount();
        // Consume the result using the Blackhole instance
        bh.consume(count);
    }
}
