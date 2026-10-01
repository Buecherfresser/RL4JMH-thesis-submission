package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.cuckoo.CuckooPlus8;
import org.fastfilter.utils.Hash;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CuckooPlus8Benchmark {

    private CuckooPlus8 filter;
    private long[] keysToInsert;
    private long keyToLookup;
    private long keyToInsert;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate input keys
        int keyCount = 10000;
        keysToInsert = new long[keyCount];
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < keyCount; i++) {
            keysToInsert[i] = random.nextLong();
        }

        // 2. Build the filter (Construction is a heavy setup operation)
        // We use the static construct method which handles capacity and insertion retries.
        filter = CuckooPlus8.construct(keysToInsert);

        // 3. Select keys for specific benchmarks
        keyToLookup = keysToInsert[0]; // A key known to be in the filter
        keyToInsert = keysToInsert[keyCount / 2]; // A key known to be in the filter
    }

    @Benchmark
    public void lookupExistingKey(Blackhole bh) {
        // Test lookup of a key known to be present
        boolean result = filter.mayContain(keyToLookup);
        bh.consume(result);
    }

    @Benchmark
    public void lookupNonExistingKey(Blackhole bh) {
        // Test lookup of a key known not to be present (using a random key outside the set)
        long nonExistingKey = Hash.randomSeed() + 1; // Ensure it's likely not in the set
        boolean result = filter.mayContain(nonExistingKey);
        bh.consume(result);
    }

    @Benchmark
    public void insertKey(Blackhole bh) {
        // Test insertion of a single key into the pre-built filter
        // Note: Since the filter is mutable, this measures the cost of insertion
        // into an already populated structure.
        filter.insert(keyToInsert);
        bh.consume(true);
    }

    @Benchmark
    public long getBitCount() {
        // Test metadata retrieval
        return filter.getBitCount();
    }
}
