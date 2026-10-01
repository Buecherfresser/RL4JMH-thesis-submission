package bench.generated.c006;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.bloom.count.SuccinctCountingBloom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBloomBenchmark {

    private static final int KEY_COUNT = 1024;
    private static final double BITS_PER_KEY = 10.0;

    private long[] keys;
    private SuccinctCountingBloom filter;
    private long presentKey;
    private long absentKey;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(12345L);
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = rnd.nextLong();
        }
        // ensure distinct keys
        java.util.HashSet<Long> set = new java.util.HashSet<>();
        for (int i = 0; i < KEY_COUNT; i++) {
            while (!set.add(keys[i])) {
                keys[i] = rnd.nextLong();
            }
        }
        filter = SuccinctCountingBloom.construct(keys, BITS_PER_KEY);
        presentKey = keys[0];
        // pick an absent key that is not in the set
        long candidate = Long.MAX_VALUE;
        while (set.contains(candidate)) {
            candidate--;
        }
        absentKey = candidate;
    }

    @Benchmark
    public SuccinctCountingBloom constructFilter() {
        return SuccinctCountingBloom.construct(keys, BITS_PER_KEY);
    }

    @Benchmark
    public boolean mayContainPresent() {
        return filter.mayContain(presentKey);
    }

    @Benchmark
    public boolean mayContainAbsent() {
        return filter.mayContain(absentKey);
    }

    @Benchmark
    public long cardinality() {
        return filter.cardinality();
    }

    @Benchmark
    public long bitCount() {
        return filter.getBitCount();
    }
}
