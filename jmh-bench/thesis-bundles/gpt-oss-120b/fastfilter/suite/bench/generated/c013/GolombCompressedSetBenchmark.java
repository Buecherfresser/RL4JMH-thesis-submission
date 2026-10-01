package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.gcs.GolombCompressedSet;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GolombCompressedSetBenchmark {

    private static final int KEY_COUNT = 100_000;
    private static final int FINGERPRINT_BITS = 8; // valid range 4..50

    private long[] keys;
    private GolombCompressedSet filter;
    private long presentKey;
    private long absentKey;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(12345L);
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = rnd.nextLong();
        }
        // ensure at least one known present key
        presentKey = keys[0];
        // generate an absent key that is unlikely to be in the set
        absentKey = presentKey ^ Long.MAX_VALUE;

        filter = GolombCompressedSet.construct(keys, FINGERPRINT_BITS);
    }

    @Benchmark
    public GolombCompressedSet constructFilter() {
        // benchmark construction of a new filter from the same key set
        return GolombCompressedSet.construct(keys, FINGERPRINT_BITS);
    }

    @Benchmark
    public boolean lookupPresent() {
        return filter.mayContain(presentKey);
    }

    @Benchmark
    public boolean lookupAbsent() {
        return filter.mayContain(absentKey);
    }

    @Benchmark
    public long getBitCount() {
        return filter.getBitCount();
    }
}
