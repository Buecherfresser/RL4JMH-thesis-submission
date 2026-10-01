package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.SplittableRandom;
import org.fastfilter.bloom.count.SuccinctCountingBloomRanked;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBloomRankedBenchmark {

    private static final int KEY_COUNT = 10_000;
    private static final int POOL_SIZE = 16;
    private static final int BITS_PER_KEY = 10;

    private long[] keys;
    private long[] extraKeys;
    private SuccinctCountingBloomRanked readOnlyFilter;
    private SuccinctCountingBloomRanked[] addFilters;
    private SuccinctCountingBloomRanked[] removeFilters;
    private int addIdx;
    private int removeIdx;

    @Setup(Level.Trial)
    public void setUp() {
        SplittableRandom rnd = new SplittableRandom(0x12345678L);
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = rnd.nextLong();
        }
        // extra keys for add benchmark (ensure they are not in the original set)
        extraKeys = new long[POOL_SIZE];
        for (int i = 0; i < POOL_SIZE; i++) {
            long v;
            do {
                v = rnd.nextLong();
            } while (contains(keys, v));
            extraKeys[i] = v;
        }

        readOnlyFilter = SuccinctCountingBloomRanked.construct(keys, BITS_PER_KEY);

        addFilters = new SuccinctCountingBloomRanked[POOL_SIZE];
        removeFilters = new SuccinctCountingBloomRanked[POOL_SIZE];
        for (int i = 0; i < POOL_SIZE; i++) {
            addFilters[i] = SuccinctCountingBloomRanked.construct(keys, BITS_PER_KEY);
            removeFilters[i] = SuccinctCountingBloomRanked.construct(keys, BITS_PER_KEY);
        }
        addIdx = 0;
        removeIdx = 0;
    }

    private boolean contains(long[] arr, long value) {
        for (long v : arr) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }

    @Benchmark
    public boolean benchmarkMayContain() {
        // test with a key that is present
        long key = keys[addIdx % KEY_COUNT];
        return readOnlyFilter.mayContain(key);
    }

    @Benchmark
    public long benchmarkCardinality() {
        return readOnlyFilter.cardinality();
    }

    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        SuccinctCountingBloomRanked filter = addFilters[addIdx];
        long key = extraKeys[addIdx];
        filter.add(key);
        bh.consume(filter);
        addIdx = (addIdx + 1) % POOL_SIZE;
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        SuccinctCountingBloomRanked filter = removeFilters[removeIdx];
        long key = keys[removeIdx];
        filter.remove(key);
        bh.consume(filter);
        removeIdx = (removeIdx + 1) % POOL_SIZE;
    }
}
