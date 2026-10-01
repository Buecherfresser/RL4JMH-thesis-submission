package bench.generated.c013;

import org.fastfilter.Filter;
import org.fastfilter.gcs.BitBuffer;
import org.fastfilter.gcs.GolombCompressedSet;
import org.fastfilter.utils.Hash;
import org.fastfilter.gcs.MonotoneList;
import org.fastfilter.gcs.Sort;

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
public class GolombCompressedSetBenchmark {

    // --- State Fields ---
    private GolombCompressedSet gcs;
    private long[] keys;
    private List<Long> presentKeys;
    private List<Long> absentKeys;
    private int fingerprintBits = 10;
    private static final int NUM_KEYS = 10000;

    // --- Setup ---
    @Setup(Level.Trial)
    public void setupTrial() {
        // 1. Generate fixed input keys
        Random random = new Random(42); // Fixed seed for reproducibility
        keys = new long[NUM_KEYS];
        presentKeys = new ArrayList<>(NUM_KEYS / 2);
        absentKeys = new ArrayList<>(NUM_KEYS / 2);

        for (int i = 0; i < NUM_KEYS; i++) {
            long key = random.nextLong();
            keys[i] = key;

            // Split keys into present and absent sets
            if (i % 2 == 0) {
                presentKeys.add(key);
            } else {
                absentKeys.add(key);
            }
        }

        // 2. Construct the GolombCompressedSet (Expensive operation, done once per trial)
        gcs = GolombCompressedSet.construct(keys, fingerprintBits);
    }

    // --- Benchmarks ---

    /**
     * Benchmark for checking membership of keys known to be present in the set.
     */
    @Benchmark
    public void mayContain_PresentKey(Blackhole bh) {
        // Pick a key known to be in the set (even indices from setup)
        long key = presentKeys.get(0);
        boolean result = gcs.mayContain(key);
        bh.consume(result);
    }

    /**
     * Benchmark for checking membership of keys known to be absent from the set.
     */
    @Benchmark
    public void mayContain_AbsentKey(Blackhole bh) {
        // Pick a key known to be absent (odd indices from setup)
        long key = absentKeys.get(0);
        boolean result = gcs.mayContain(key);
        bh.consume(result);
    }

    /**
     * Benchmark for measuring the space used by the compressed set structure.
     */
    @Benchmark
    public long getBitCount(Blackhole bh) {
        long count = gcs.getBitCount();
        bh.consume(count);
        return count;
    }
}
