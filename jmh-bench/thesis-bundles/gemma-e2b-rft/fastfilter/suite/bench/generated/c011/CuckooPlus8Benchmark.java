package bench.generated.c011;

import org.fastfilter.Filter;
import org.fastfilter.cuckoo.CuckooPlus8;
import org.fastfilter.utils.Hash;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CuckooPlus8Benchmark {

    private CuckooPlus8 filter;
    private List<Long> keysToInsert;
    private List<Long> keysToLookup;
    private final Random random = new Random(42); // Fixed seed for reproducibility

    // Constants for setup
    private static final int KEY_COUNT = 10000;

    @Setup
    public void setup() {
        // 1. Generate a large set of unique keys for construction and insertion
        keysToInsert = new ArrayList<>(KEY_COUNT);
        for (int i = 0; i < KEY_COUNT; i++) {
            keysToInsert.add(random.nextLong());
        }

        // 2. Generate a subset of keys for lookup tests (some present, some absent)
        keysToLookup = new ArrayList<>(KEY_COUNT);
        for (int i = 0; i < KEY_COUNT; i++) {
            // Use the first half for guaranteed hits, second half for misses
            if (i < KEY_COUNT / 2) {
                keysToLookup.add(keysToInsert.get(i));
            } else {
                // Generate a random key that is highly unlikely to be in the set
                keysToLookup.add(random.nextLong());
            }
        }

        // 3. Construct the CuckooPlus8 filter instance
        // We use the static construct method which handles internal rebuilding if necessary.
        this.filter = CuckooPlus8.construct(keysToInsert.stream().mapToLong(Long::longValue).toArray());
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Re-run construction to measure the cost of building the structure from scratch
        CuckooPlus8 tempFilter = CuckooPlus8.construct(keysToInsert.stream().mapToLong(Long::longValue).toArray());
        bh.consume(tempFilter);
    }

    @Benchmark
    public void benchmarkInsertion(Blackhole bh) {
        // Test insertion of a key that is known to be in the set
        long keyToInsert = keysToInsert.get(0);
        filter.insert(keyToInsert);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkInsertionMiss(Blackhole bh) {
        // Test insertion of a key that is highly unlikely to be in the set
        long randomKey = random.nextLong();
        filter.insert(randomKey);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkMayContainHit(Blackhole bh) {
        // Test lookup for a key known to be present
        long keyToFind = keysToInsert.get(500);
        boolean result = filter.mayContain(keyToFind);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMayContainMiss(Blackhole bh) {
        // Test lookup for a key known to be absent
        long randomKey = random.nextLong();
        boolean result = filter.mayContain(randomKey);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        // Test the metric calculation method
        long count = filter.getBitCount();
        bh.consume(count);
    }
}
