package bench.generated.c020;

import org.fastfilter.xor.XorFuse8;
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
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorFuse8Benchmark {

    private XorFuse8 filter;
    private long[] keys;
    private long positiveKey;
    private long negativeKey;

    // Constants for input generation
    private static final int KEY_SET_SIZE = 50000;
    private static final Random RANDOM = new Random(42);

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate a large set of keys for construction
        keys = new long[KEY_SET_SIZE];
        List<Long> keyList = new ArrayList<>(KEY_SET_SIZE);

        for (int i = 0; i < KEY_SET_SIZE; i++) {
            // Generate unique long keys
            long key;
            do {
                key = RANDOM.nextLong();
            } while (keyList.contains(key));
            keyList.add(key);
            keys[i] = key;
        }

        // 2. Construct the XorFuse8 filter
        try {
            this.filter = XorFuse8.construct(keys);
            
            // 3. Select a positive key (one that is definitely in the set)
            this.positiveKey = keys[RANDOM.nextInt(KEY_SET_SIZE)];

            // 4. Select a negative key (one that is definitely NOT in the set)
            long tempNegativeKey;
            do {
                tempNegativeKey = RANDOM.nextLong();
            } while (java.util.Arrays.binarySearch(keys, tempNegativeKey) >= 0);
            this.negativeKey = tempNegativeKey;

        } catch (Exception e) {
            throw new RuntimeException("Failed to construct XorFuse8 filter during setup", e);
        }
    }

    @Benchmark
    public void testConstruction(Blackhole bh) {
        // Re-construct the filter in every iteration to measure construction time
        XorFuse8 tempFilter = XorFuse8.construct(keys);
        bh.consume(tempFilter);
    }

    @Benchmark
    public void testMayContainPositive(Blackhole bh) {
        // Test lookup for a key known to be present
        boolean result = filter.mayContain(positiveKey);
        bh.consume(result);
    }

    @Benchmark
    public void testMayContainNegative(Blackhole bh) {
        // Test lookup for a key known to be absent
        boolean result = filter.mayContain(negativeKey);
        bh.consume(result);
    }
}
