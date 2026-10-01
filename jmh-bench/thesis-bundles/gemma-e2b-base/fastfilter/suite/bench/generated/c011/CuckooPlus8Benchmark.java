package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.fastfilter.Filter;
import org.fastfilter.utils.Hash;
import org.fastfilter.cuckoo.CuckooPlus8;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CuckooPlus8Benchmark {

    private CuckooPlus8 filter;
    private long[] keysForConstruction;
    private List<Long> keysForInsertion;

    // Constants for setup
    private static final int CONSTRUCTION_CAPACITY = 1024;
    private static final int NUM_KEYS_FOR_SETUP = 500;

    @Setup(Level.Trial)
    public void setupTrial() {
        // 1. Generate keys for construction
        keysForConstruction = new long[NUM_KEYS_FOR_SETUP];
        Random random = new Random(42); // Fixed seed for reproducible key generation
        for (int i = 0; i < NUM_KEYS_FOR_SETUP; i++) {
            keysForConstruction[i] = random.nextLong();
        }

        // 2. Construct the filter instance
        // CuckooPlus8.construct handles the internal retry logic
        this.filter = CuckooPlus8.construct(keysForConstruction);

        // 3. Generate keys for insertion tests (distinct keys)
        keysForInsertion = new ArrayList<>(NUM_KEYS_FOR_SETUP);
        for (long k : keysForConstruction) {
            keysForInsertion.add(k);
        }
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Re-run construction for a fresh filter instance in each benchmark run
        // This tests the cost of building the structure from scratch.
        CuckooPlus8 freshFilter = CuckooPlus8.construct(keysForConstruction);
        bh.consume(freshFilter);
    }

    @Benchmark
    public void benchmarkInsertion(Blackhole bh) {
        // Test insertion of a key that is likely already present (to test the 'already inserted' path)
        long keyToInsert = keysForInsertion.get(0);
        filter.insert(keyToInsert);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkContainmentPresent(Blackhole bh) {
        // Test containment for a key known to be in the set
        long keyPresent = keysForInsertion.get(0);
        boolean result = filter.mayContain(keyPresent);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainmentAbsent(Blackhole bh) {
        // Test containment for a key guaranteed not to be in the set
        long keyAbsent = 9999999999999999L;
        boolean result = filter.mayContain(keyAbsent);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        // Test the metric calculation method
        long count = filter.getBitCount();
        bh.consume(count);
    }
}
