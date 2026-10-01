package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.bloom.count.SuccinctCountingBlockedBloomRanked;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBlockedBloomRankedBenchmark {

    @Param({"10000"})
    private int numKeys;

    @Param({"8"})
    private int bitsPerKey;

    private SuccinctCountingBlockedBloomRanked filter;
    private long[] keys;
    private long[] absentKeys;
    private int keyIndex = 0;
    private int absentKeyIndex = 0;

    @Setup(Level.Trial)
    public void setup() {
        Random rand = new Random(12345);
        keys = new long[numKeys];
        for (int i = 0; i < numKeys; i++) {
            keys[i] = rand.nextLong();
        }
        filter = SuccinctCountingBlockedBloomRanked.construct(keys, bitsPerKey);
        // generate absent keys with a different seed
        Random randAbs = new Random(54321);
        absentKeys = new long[numKeys];
        for (int i = 0; i < numKeys; i++) {
            absentKeys[i] = randAbs.nextLong();
        }
    }

    @Benchmark
    public boolean mayContainPresent() {
        int i = keyIndex++;
        if (keyIndex >= numKeys) keyIndex = 0;
        return filter.mayContain(keys[i]);
    }

    @Benchmark
    public boolean mayContainAbsent() {
        int i = absentKeyIndex++;
        if (absentKeyIndex >= numKeys) absentKeyIndex = 0;
        return filter.mayContain(absentKeys[i]);
    }

    @Benchmark
    public SuccinctCountingBlockedBloomRanked construct() {
        return SuccinctCountingBlockedBloomRanked.construct(keys, bitsPerKey);
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
