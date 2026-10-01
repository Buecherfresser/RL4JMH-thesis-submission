package bench.generated.c008;

import org.fastfilter.cuckoo.Cuckoo16;
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
public class Cuckoo16Benchmark {

    private Cuckoo16 filter;
    private long[] keys;
    private long[] testKeys;
    private final Random random = new Random(42);

    // Setup: Prepare the filter instance and the key set once per trial.
    @Setup
    public void setup() {
        // 1. Generate a large set of unique keys for construction
        int keyCount = 10000;
        keys = new long[keyCount];
        for (int i = 0; i < keyCount; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the Cuckoo16 filter
        this.filter = Cuckoo16.construct(keys);

        // 3. Prepare a subset of keys for insertion/containment tests
        this.testKeys = new long[keyCount / 10];
        for (int i = 0; i < keys.length / 10; i++) {
            testKeys[i] = keys[i * 10];
        }
    }

    // Benchmark 1: Construction Time
    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Re-run construction to measure the cost of building the structure from scratch
        Cuckoo16 cuckoo = Cuckoo16.construct(keys);
        bh.consume(cuckoo);
    }

    // Benchmark 2: Containment Check (Positive Case)
    @Benchmark
    public boolean benchmarkMayContainPresent(Blackhole bh) {
        long key = testKeys[0]; // Key known to be in the set
        boolean result = filter.mayContain(key);
        bh.consume(result);
        return result;
    }

    // Benchmark 3: Containment Check (Negative Case)
    @Benchmark
    public boolean benchmarkMayContainAbsent(Blackhole bh) {
        // Generate a key highly unlikely to be in the set
        long absentKey = random.nextLong();
        boolean result = filter.mayContain(absentKey);
        bh.consume(result);
        return result;
    }

    // Benchmark 4: Insertion Time
    @Benchmark
    public void benchmarkInsert(Blackhole bh) {
        long key = testKeys[0];
        filter.insert(key);
    }

    // Benchmark 5: Space Usage Check
    @Benchmark
    public long benchmarkGetBitCount(Blackhole bh) {
        long count = filter.getBitCount();
        bh.consume(count);
        return count;
    }
}
