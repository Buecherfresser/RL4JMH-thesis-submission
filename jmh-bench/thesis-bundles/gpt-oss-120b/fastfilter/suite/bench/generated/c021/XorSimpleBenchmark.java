package bench.generated.c021;

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
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.xor.XorSimple;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorSimpleBenchmark {

    private static final int KEY_COUNT = 1024;

    private long[] keys;
    private XorSimple filter;
    private long hitKey;
    private long missKey;

    @Setup
    public void setUp() {
        Random rnd = new Random(12345L);
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = rnd.nextLong();
        }
        // Build filter once for lookup benchmarks
        filter = XorSimple.construct(keys);
        hitKey = keys[0];
        // Find a key that is not in the set
        missKey = Long.MAX_VALUE;
        boolean found;
        do {
            found = false;
            for (long k : keys) {
                if (k == missKey) {
                    found = true;
                    missKey = rnd.nextLong();
                    break;
                }
            }
        } while (found);
    }

    @Benchmark
    public XorSimple benchmarkConstruction() {
        return XorSimple.construct(keys);
    }

    @Benchmark
    public boolean benchmarkMayContainHit() {
        return filter.mayContain(hitKey);
    }

    @Benchmark
    public boolean benchmarkMayContainMiss() {
        return filter.mayContain(missKey);
    }

    @Benchmark
    public long benchmarkGetBitCount() {
        return filter.getBitCount();
    }
}
