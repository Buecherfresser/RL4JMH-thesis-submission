package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.cuckoo.Cuckoo16;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Cuckoo16Benchmark {

    private static final int KEY_COUNT = 100_000;

    private long[] keys;
    private Cuckoo16 filter;
    private Random queryRandom;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(12345L);
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = rnd.nextLong();
        }
        // Build a filter once for read‑only benchmarks
        filter = Cuckoo16.construct(keys);
        queryRandom = new Random(54321L);
    }

    @Benchmark
    public long benchmarkConstruct() {
        Cuckoo16 f = Cuckoo16.construct(keys);
        return f.getBitCount();
    }

    @Benchmark
    public boolean benchmarkMayContain() {
        long key = keys[queryRandom.nextInt(keys.length)];
        return filter.mayContain(key);
    }

    @Benchmark
    public long benchmarkGetBitCount() {
        return filter.getBitCount();
    }
}
