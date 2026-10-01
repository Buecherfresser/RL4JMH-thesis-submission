package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.bloom.BlockedBloom;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockedBloomBenchmark {

    private long[] keys;
    private BlockedBloom filter;
    private int bitsPerKey;
    private int index;
    private Random random;

    @Setup(Level.Trial)
    public void setUp() {
        bitsPerKey = 11;
        int keyCount = 1 << 14; // 16384 keys
        keys = new long[keyCount];
        random = new Random(12345L);
        for (int i = 0; i < keyCount; i++) {
            keys[i] = random.nextLong();
        }
        filter = BlockedBloom.construct(keys, bitsPerKey);
        index = 0;
    }

    @Benchmark
    public BlockedBloom constructBenchmark() {
        return BlockedBloom.construct(keys, bitsPerKey);
    }

    @Benchmark
    public void mayContainBenchmark(Blackhole bh) {
        long key = keys[(index++) & (keys.length - 1)];
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void addBenchmark(Blackhole bh) {
        long key = keys[(index++) & (keys.length - 1)];
        filter.add(key);
        bh.consume(key);
    }

    @Benchmark
    public long bitCountBenchmark() {
        return filter.getBitCount();
    }
}
