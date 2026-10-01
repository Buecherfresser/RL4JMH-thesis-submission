package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.bloom.count.SuccinctCountingBlockedBloom;
import java.util.SplittableRandom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBlockedBloomBenchmark {

    private long[] keys;
    private int bitsPerKey;
    private SuccinctCountingBlockedBloom filter;
    private long extraKey;

    @Setup(Level.Trial)
    public void setup() {
        SplittableRandom rnd = new SplittableRandom(12345L);
        int size = 1024;
        keys = new long[size];
        for (int i = 0; i < size; i++) {
            keys[i] = rnd.nextLong();
        }
        bitsPerKey = 10;
        filter = SuccinctCountingBlockedBloom.construct(keys, bitsPerKey);
        extraKey = rnd.nextLong();
    }

    @Benchmark
    public SuccinctCountingBlockedBloom constructBenchmark() {
        return SuccinctCountingBlockedBloom.construct(keys, bitsPerKey);
    }

    @Benchmark
    public boolean mayContainBenchmark() {
        return filter.mayContain(keys[0]);
    }

    @Benchmark
    public long cardinalityBenchmark() {
        return filter.cardinality();
    }

    @Benchmark
    public SuccinctCountingBlockedBloom addBenchmark() {
        SuccinctCountingBlockedBloom f = SuccinctCountingBlockedBloom.construct(keys, bitsPerKey);
        f.add(extraKey);
        return f;
    }

    @Benchmark
    public SuccinctCountingBlockedBloom removeBenchmark() {
        SuccinctCountingBlockedBloom f = SuccinctCountingBlockedBloom.construct(keys, bitsPerKey);
        f.remove(keys[0]);
        return f;
    }
}
