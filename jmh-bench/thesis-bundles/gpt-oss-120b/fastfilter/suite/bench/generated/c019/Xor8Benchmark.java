package bench.generated.c019;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.io.ByteArrayInputStream;
import org.fastfilter.xor.Xor8;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Xor8Benchmark {

    private int keyCount;
    private long[] keys;
    private Xor8 filter;
    private byte[] serializedData;
    private long lookupKey;

    @Setup
    public void setUp() {
        keyCount = 10_000;
        Random rnd = new Random(12345L);
        keys = new long[keyCount];
        for (int i = 0; i < keyCount; i++) {
            keys[i] = rnd.nextLong();
        }
        filter = Xor8.construct(keys);
        serializedData = filter.getData();
        lookupKey = keys[0];
    }

    @Benchmark
    public Xor8 constructFilter() {
        return Xor8.construct(keys);
    }

    @Benchmark
    public boolean lookup() {
        return filter.mayContain(lookupKey);
    }

    @Benchmark
    public long bitCount() {
        return filter.getBitCount();
    }

    @Benchmark
    public byte[] getData() {
        return filter.getData();
    }

    @Benchmark
    public Xor8 roundtrip() {
        return new Xor8(new ByteArrayInputStream(serializedData));
    }
}
