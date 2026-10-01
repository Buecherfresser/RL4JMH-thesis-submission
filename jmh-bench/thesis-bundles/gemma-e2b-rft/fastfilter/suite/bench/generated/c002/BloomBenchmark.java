package bench.generated.c002;

import org.fastfilter.bloom.Bloom;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class BloomBenchmark {

    // --- State Fields ---
    private Bloom bloomFilter;
    private long[] keysForConstruction;
    private long[] keysForAdd;
    private long[] keysForContainmentTrue;
    private long[] keysForContainmentFalse;
    private final int KEY_COUNT = 100000;
    private final double BITS_PER_KEY = 10.0;

    // Random generator for reproducible keys
    private final Random random = new Random(42);

    @Setup
    public void setup() {
        // 1. Setup keys for construction
        keysForConstruction = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keysForConstruction[i] = random.nextLong();
        }

        // 2. Construct the Bloom filter (This simulates the initial setup cost)
        System.out.println("Setting up Bloom filter...");
        bloomFilter = Bloom.construct(keysForConstruction, BITS_PER_KEY);
        System.out.println("Bloom filter constructed.");

        // 3. Setup keys for Add operations (a subset of construction keys)
        keysForAdd = new long[KEY_COUNT / 2];
        for (int i = 0; i < keysForAdd.length; i++) {
            keysForAdd[i] = keysForConstruction[i];
        }

        // 4. Setup keys for True Containment (keys that were added)
        keysForContainmentTrue = new long[KEY_COUNT / 4];
        for (int i = 0; i < keysForContainmentTrue.length; i++) {
            keysForContainmentTrue[i] = keysForConstruction[i];
        }

        // 5. Setup keys for False Containment (keys that were NOT added)
        keysForContainmentFalse = new long[KEY_COUNT / 4];
        for (int i = 0; i < keysForContainmentFalse.length; i++) {
            // Generate completely new, distinct keys
            keysForContainmentFalse[i] = random.nextLong();
        }
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Re-run construction to measure the static factory method cost
        Bloom constructedFilter = Bloom.construct(keysForConstruction, BITS_PER_KEY);
        bh.consume(constructedFilter);
    }

    @Benchmark
    public void benchmarkAddOperation(Blackhole bh) {
        // Test the add method on a single key
        long key = keysForAdd[0];
        bloomFilter.add(key);
        bh.consume(key);
    }

    @Benchmark
    public void benchmarkMayContainTrue(Blackhole bh) {
        // Test containment for a single key known to be present
        long key = keysForContainmentTrue[0];
        boolean result = bloomFilter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMayContainFalse(Blackhole bh) {
        // Test containment for a single key known NOT to be present
        long key = keysForContainmentFalse[0];
        boolean result = bloomFilter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        // Test the property retrieval method
        long count = bloomFilter.getBitCount();
        bh.consume(count);
    }
}
