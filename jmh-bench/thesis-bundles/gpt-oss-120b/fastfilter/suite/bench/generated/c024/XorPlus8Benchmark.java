package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.xorplus.XorPlus8;
import java.util.Random;
import java.io.ByteArrayInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorPlus8Benchmark {

    private static final int KEY_COUNT = 10_000;

    private long[] keys;
    private XorPlus8 filter;
    private byte[] serializedData;
    private int lookupIndex;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(12345L);
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = rnd.nextLong();
        }
        filter = XorPlus8.construct(keys);
        serializedData = filter.getData();
        lookupIndex = 0;
    }

    @Benchmark
    public XorPlus8 constructFilter() {
        return XorPlus8.construct(keys);
    }

    @Benchmark
    public boolean lookupMayContain() {
        long key = keys[lookupIndex];
        lookupIndex = (lookupIndex + 1) % keys.length;
        return filter.mayContain(key);
    }

    @Benchmark
    public byte[] serializeFilter() {
        return filter.getData();
    }

    @Benchmark
    public XorPlus8 deserializeFilter() {
        return new XorPlus8(new ByteArrayInputStream(serializedData));
    }

    @Benchmark
    public void lookupMayContainBlackhole(Blackhole bh) {
        long key = keys[lookupIndex];
        lookupIndex = (lookupIndex + 1) % keys.length;
        bh.consume(filter.mayContain(key));
    }
}
