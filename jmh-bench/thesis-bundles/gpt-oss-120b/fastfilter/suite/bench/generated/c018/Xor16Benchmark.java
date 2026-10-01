package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.xor.Xor16;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Xor16Benchmark {

    private long[] keys;
    private Xor16 filter;
    private int queryIndex;

    @Setup(Level.Trial)
    public void setUp() {
        int size = 100_000; // number of keys
        keys = new long[size];
        Random rnd = new Random(123456L);
        for (int i = 0; i < size; i++) {
            keys[i] = rnd.nextLong();
        }
        filter = Xor16.construct(keys);
        queryIndex = 0;
    }

    @Benchmark
    public Xor16 constructFilter() {
        return Xor16.construct(keys);
    }

    @Benchmark
    public boolean mayContainLookup() {
        long key = keys[queryIndex];
        queryIndex = (queryIndex + 1) % keys.length;
        return filter.mayContain(key);
    }

    @Benchmark
    public long getBitCount() {
        return filter.getBitCount();
    }
}
