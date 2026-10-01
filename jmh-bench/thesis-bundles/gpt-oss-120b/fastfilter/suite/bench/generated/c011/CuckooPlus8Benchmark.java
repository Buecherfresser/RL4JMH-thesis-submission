package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.cuckoo.CuckooPlus8;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CuckooPlus8Benchmark {

    private static final int KEY_COUNT = 1 << 14; // 16384 keys

    private long[] keys;
    private CuckooPlus8 prebuiltFilter;
    private Random queryRandom;
    private int queryIndex;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(12345L);
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = rnd.nextLong();
        }
        prebuiltFilter = CuckooPlus8.construct(keys);
        queryRandom = new Random(98765L);
        queryIndex = 0;
    }

    @Benchmark
    public CuckooPlus8 benchmarkConstruct() {
        return CuckooPlus8.construct(keys);
    }

    @Benchmark
    public boolean benchmarkMayContain() {
        // cycle through keys to avoid cache effects
        long key = keys[queryIndex];
        queryIndex = (queryIndex + 1) & (keys.length - 1);
        return prebuiltFilter.mayContain(key);
    }

    @Benchmark
    public long benchmarkGetBitCount() {
        return prebuiltFilter.getBitCount();
    }

    @Benchmark
    public CuckooPlus8 benchmarkInsert() {
        // create a fresh filter for each invocation
        CuckooPlus8 filter = new CuckooPlus8(1024);
        long key = queryRandom.nextLong();
        filter.insert(key);
        return filter;
    }
}
