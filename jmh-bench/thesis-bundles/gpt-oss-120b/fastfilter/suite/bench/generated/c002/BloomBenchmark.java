package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.bloom.Bloom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BloomBenchmark {

    private static final double BITS_PER_KEY = 10.0;
    private static final int ADD_POOL_SIZE = 16;

    private long[] keys;
    private Bloom queryFilter;
    private Bloom[] addPool;
    private int addIndex;
    private int queryIndex;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(12345L);
        int keyCount = 1 << 14; // 16384 keys
        keys = new long[keyCount];
        for (int i = 0; i < keyCount; i++) {
            keys[i] = rnd.nextLong();
        }

        // Filter pre‑populated with all keys for mayContain benchmark
        queryFilter = Bloom.construct(keys, BITS_PER_KEY);

        // Pool of empty Bloom instances for add benchmark
        addPool = new Bloom[ADD_POOL_SIZE];
        for (int i = 0; i < ADD_POOL_SIZE; i++) {
            addPool[i] = Bloom.construct(new long[0], BITS_PER_KEY);
        }

        addIndex = 0;
        queryIndex = 0;
    }

    @Benchmark
    public Bloom benchmarkConstruct() {
        // Construct a new Bloom filter from the pre‑generated key set
        return Bloom.construct(keys, BITS_PER_KEY);
    }

    @Benchmark
    public boolean benchmarkMayContain() {
        Bloom b = queryFilter;
        long key = keys[queryIndex];
        boolean result = b.mayContain(key);
        queryIndex = (queryIndex + 1) % keys.length;
        return result;
    }

    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        Bloom b = addPool[addIndex];
        long key = keys[addIndex];
        b.add(key);
        // Consume something derived from the filter to prevent dead‑code elimination
        bh.consume(b.getBitCount());
        addIndex = (addIndex + 1) % addPool.length;
    }

    @Benchmark
    public boolean benchmarkSupportsAdd() {
        return queryFilter.supportsAdd();
    }

    @Benchmark
    public long benchmarkGetBitCount() {
        return queryFilter.getBitCount();
    }
}
