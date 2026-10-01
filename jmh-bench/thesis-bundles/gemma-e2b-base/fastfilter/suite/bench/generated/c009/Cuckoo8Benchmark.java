package bench.generated.c009;

import org.fastfilter.Filter;
import org.fastfilter.utils.Hash;
import org.fastfilter.cuckoo.Cuckoo8;
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
public class Cuckoo8Benchmark {

    private Cuckoo8 filter;
    private List<Long> keys;
    private List<Long> presentKeys;
    private List<Long> absentKeys;

    // Constants for setup
    private static final int KEY_COUNT = 10000;
    private static final int CAPACITY = 10000; // Capacity for the Cuckoo filter

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate a large set of unique keys
        keys = new ArrayList<>(KEY_COUNT);
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < KEY_COUNT; i++) {
            keys.add(random.nextLong());
        }

        // 2. Create a subset of keys that will be present
        presentKeys = new ArrayList<>(KEY_COUNT / 10);
        for (int i = 0; i < KEY_COUNT / 10; i++) {
            presentKeys.add(keys.get(i));
        }

        // 3. Create a subset of keys that will be absent (ensure they are not in the present set)
        absentKeys = new ArrayList<>(KEY_COUNT - (KEY_COUNT / 10));
        for (int i = KEY_COUNT / 10; i < KEY_COUNT; i++) {
            absentKeys.add(keys.get(i));
        }

        // 4. Construct the Cuckoo8 filter once per trial
        try {
            this.filter = Cuckoo8.construct(keys.stream().mapToLong(Long::longValue).toArray());
        } catch (IllegalStateException e) {
            // Should not happen with sufficient capacity, but handle defensively
            System.err.println("Cuckoo8 construction failed: " + e.getMessage());
            this.filter = null;
        }
    }

    @Benchmark
    public void mayContain_Hit(Blackhole bh) {
        if (filter == null) return;
        long key = presentKeys.get((int) (Math.random() * presentKeys.size()));
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void mayContain_Miss(Blackhole bh) {
        if (filter == null) return;
        long key = absentKeys.get((int) (Math.random() * absentKeys.size()));
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void insert_RandomKey(Blackhole bh) {
        if (filter == null) return;
        long key = keys.get((int) (Math.random() * KEY_COUNT));
        filter.insert(key);
        // insert is void, consume nothing
    }

    @Benchmark
    public void getBitCount(Blackhole bh) {
        if (filter == null) return;
        long count = filter.getBitCount();
        bh.consume(count);
    }
}
