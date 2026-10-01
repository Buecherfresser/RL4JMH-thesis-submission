package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.Filter;
import org.fastfilter.xor.Xor16;
import org.fastfilter.utils.Hash;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Xor16Benchmark {

    private Xor16 xor16Filter;
    private long[] keys;
    private static final int KEY_COUNT = 10000;

    @Setup
    public void setup() {
        // Build a fixed set of keys for the Xor16 filter construction
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = (long) i * 0xDEADBEEFCAFE0000L + i;
        }
        // Construct the Xor16 filter once
        xor16Filter = Xor16.construct(keys);
    }

    @Benchmark
    public void mayContain(Blackhole bh) {
        long key = (long) Math.random() * Long.MAX_VALUE;
        bh.consume(xor16Filter.mayContain(key));
    }

    @Benchmark
    public void getBitCount(Blackhole bh) {
        bh.consume(xor16Filter.getBitCount());
    }

    // Since Xor16 is an immutable filter once constructed, we focus on construction time
    // and the core lookup operation.

    @Benchmark
    public void constructionTime(Blackhole bh) {
        // Reconstruct the filter to measure construction time
        Xor16.construct(keys);
    }
}
