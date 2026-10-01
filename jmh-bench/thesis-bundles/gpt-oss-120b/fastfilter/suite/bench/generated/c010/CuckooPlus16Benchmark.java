package bench.generated.c010;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.cuckoo.CuckooPlus16;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CuckooPlus16Benchmark {

    private static final int KEY_COUNT = 10_000;

    private long[] keys;
    private CuckooPlus16 filter;
    private long existingKey;
    private long nonExistingKey;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(12345L);
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = rnd.nextLong();
        }
        filter = CuckooPlus16.construct(keys);
        existingKey = keys[0];
        nonExistingKey = Long.MAX_VALUE;
        for (long k : keys) {
            if (k == nonExistingKey) {
                nonExistingKey = Long.MIN_VALUE;
                break;
            }
        }
    }

    @Benchmark
    public CuckooPlus16 constructBenchmark() {
        return CuckooPlus16.construct(keys);
    }

    @Benchmark
    public boolean mayContainExistingBenchmark() {
        return filter.mayContain(existingKey);
    }

    @Benchmark
    public boolean mayContainNonExistingBenchmark() {
        return filter.mayContain(nonExistingKey);
    }

    @Benchmark
    public long getBitCountBenchmark() {
        return filter.getBitCount();
    }
}
