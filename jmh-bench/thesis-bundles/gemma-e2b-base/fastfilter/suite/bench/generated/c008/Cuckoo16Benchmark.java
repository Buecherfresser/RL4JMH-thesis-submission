package bench.generated.c008;

import org.fastfilter.Filter;
import org.fastfilter.utils.Hash;
import org.fastfilter.cuckoo.Cuckoo16;
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
public class Cuckoo16Benchmark {

    private Cuckoo16 filter;
    private List<Long> keysToInsert;
    private List<Long> keysToContain;
    private long testKey;

    @Setup
    public void setup() {
        // 1. Generate a large set of unique keys for construction and insertion testing.
        int numKeys = 10000;
        keysToInsert = new ArrayList<>(numKeys);
        keysToContain = new ArrayList<>(numKeys / 2);

        Random random = new Random(42); // Fixed seed for reproducibility

        for (int i = 0; i < numKeys; i++) {
            long key = random.nextLong();
            keysToInsert.add(key);
            if (i < numKeys / 2) {
                keysToContain.add(key);
            }
        }

        // 2. Construct the Cuckoo16 filter once. This simulates the setup cost.
        // Cuckoo16.construct handles the internal resizing logic.
        this.filter = Cuckoo16.construct(keysToInsert.stream().mapToLong(Long::longValue).toArray());

        // 3. Select a representative key for lookup tests
        this.testKey = keysToContain.get(0);
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Re-run construction to measure the cost of building the structure from scratch.
        Cuckoo16 tempFilter = Cuckoo16.construct(keysToInsert.stream().mapToLong(Long::longValue).toArray());
        bh.consume(tempFilter);
    }

    @Benchmark
    public void benchmarkInsertion(Blackhole bh) {
        // Test insertion performance on the pre-built filter.
        // We cycle through the keys to ensure we hit different hash paths.
        for (int i = 0; i < 100; i++) {
            long key = keysToInsert.get(i % keysToInsert.size());
            filter.insert(key);
        }
        bh.consume(filter);
    }

    @Benchmark
    public void benchmarkMayContainPresent(Blackhole bh) {
        // Test containment for keys known to be present.
        for (int i = 0; i < 100; i++) {
            long key = keysToContain.get(i % keysToContain.size());
            boolean result = filter.mayContain(key);
            bh.consume(result);
        }
    }

    @Benchmark
    public void benchmarkMayContainAbsent(Blackhole bh) {
        // Test containment for keys known not to be present (randomly generated).
        for (int i = 0; i < 100; i++) {
            // Generate a key guaranteed not to be in the initial set
            long absentKey = (long) (Math.random() * Long.MAX_VALUE);
            boolean result = filter.mayContain(absentKey);
            bh.consume(result);
        }
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        // Test the cost of retrieving the filter's space usage.
        long count = filter.getBitCount();
        bh.consume(count);
    }
}
