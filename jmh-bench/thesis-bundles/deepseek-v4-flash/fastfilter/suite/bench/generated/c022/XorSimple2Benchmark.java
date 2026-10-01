package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
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

    private long[] keys;
    private long[] absentKeys;
    private XorSimple2 filter;
    private long presentKey;
    private long absentKey;

    @Setup(Level.Trial)
    public void setup() {
        int numKeys = 100_000;
        Random rnd = new Random(12345L);
        keys = new long[numKeys];
        for (int i = 0; i < numKeys; i++) {
            keys[i] = rnd.nextLong();
        }
        absentKeys = new long[numKeys];
        for (int i = 0; i < numKeys; i++) {
            absentKeys[i] = rnd.nextLong();
        }
        filter = createXorSimple2(keys);
        presentKey = keys[0];
        absentKey = 0x123456789abcdef0L;
        while (filter.mayContain(absentKey)) {
            absentKey++;
        }
    }

    private static XorSimple2 createXorSimple2(long[] keys) {
        while (true) {
            try {
                return new XorSimple2(keys);
            } catch (ArrayIndexOutOfBoundsException e) {
                // Retry with a fresh random seed.
            }
        }
    }

    private static XorSimple createXorSimple(long[] keys) {
        while (true) {
            try {
                return XorSimple2.construct(keys);
            } catch (ArrayIndexOutOfBoundsException e) {
                // Retry with a fresh random seed.
            }
        }
    }

    @Benchmark
    public XorSimple2 constructXorSimple2() {
        return createXorSimple2(keys);
    }

    @Benchmark
    public XorSimple constructViaStatic() {
        return createXorSimple(keys);
    }

    @Benchmark
    public boolean mayContainPresent() {
        return filter.mayContain(presentKey);
    }

    @Benchmark
    public boolean mayContainAbsent() {
        return filter.mayContain(absentKey);
    }

    @Benchmark
    public long getBitCount() {
        return filter.getBitCount();
    }

    @Benchmark
    public long cardinality() {
        return filter.cardinality();
    }
}
