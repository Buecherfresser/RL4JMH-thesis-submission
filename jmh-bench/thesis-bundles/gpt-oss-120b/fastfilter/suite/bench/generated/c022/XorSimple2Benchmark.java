package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.xor.XorSimple2;
import org.fastfilter.xor.XorSimple;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorSimple2Benchmark {

    private static final int KEY_COUNT = 1 << 14; // 16384

    private long[] keys;
    private XorSimple filter;
    private long presentKey;
    private long absentKey;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(12345L);
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = rnd.nextLong();
        }
        presentKey = keys[0];
        long candidate;
        do {
            candidate = rnd.nextLong();
        } while (contains(keys, candidate));
        absentKey = candidate;
        filter = constructFilter(keys);
    }

    private static boolean contains(long[] arr, long v) {
        for (long l : arr) {
            if (l == v) {
                return true;
            }
        }
        return false;
    }

    private static XorSimple constructFilter(long[] ks) {
        while (true) {
            try {
                return XorSimple2.construct(ks);
            } catch (ArrayIndexOutOfBoundsException e) {
                // retry with a new internal seed
            }
        }
    }

    @Benchmark
    public XorSimple benchmarkConstruction() {
        return constructFilter(keys);
    }

    @Benchmark
    public boolean benchmarkMayContainPresent() {
        return filter.mayContain(presentKey);
    }

    @Benchmark
    public boolean benchmarkMayContainAbsent() {
        return filter.mayContain(absentKey);
    }

    @Benchmark
    public long benchmarkGetBitCount() {
        return filter.getBitCount();
    }
}
