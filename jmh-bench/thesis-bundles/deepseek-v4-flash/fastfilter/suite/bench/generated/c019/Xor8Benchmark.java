package bench.generated.c019;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.fastfilter.xor.Xor8;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Xor8Benchmark {

    private long[] keys;
    private long[] absentKeys;
    private Xor8 filter;
    private byte[] serialized;
    private int keyIndex;
    private int absentIndex;

    @Setup(Level.Trial)
    public void setUp() {
        int numKeys = 100_000;
        Random random = new Random(42L);
        keys = new long[numKeys];
        Set<Long> keySet = new HashSet<>(numKeys * 2);
        for (int i = 0; i < numKeys; i++) {
            long key = random.nextLong();
            keys[i] = key;
            keySet.add(key);
        }
        absentKeys = new long[numKeys];
        for (int i = 0; i < numKeys; i++) {
            long candidate;
            do {
                candidate = random.nextLong();
            } while (keySet.contains(candidate));
            absentKeys[i] = candidate;
        }
        filter = Xor8.construct(keys);
        serialized = filter.getData();
        keyIndex = 0;
        absentIndex = 0;
    }

    @Benchmark
    public Xor8 construct() {
        return Xor8.construct(keys);
    }

    @Benchmark
    public Xor8 constructViaConstructor() {
        return new Xor8(keys);
    }

    @Benchmark
    public boolean mayContainPresent() {
        long key = keys[keyIndex];
        keyIndex++;
        if (keyIndex >= keys.length) {
            keyIndex = 0;
        }
        return filter.mayContain(key);
    }

    @Benchmark
    public boolean mayContainAbsent() {
        long key = absentKeys[absentIndex];
        absentIndex++;
        if (absentIndex >= absentKeys.length) {
            absentIndex = 0;
        }
        return filter.mayContain(key);
    }

    @Benchmark
    public byte[] getData() {
        return filter.getData();
    }

    @Benchmark
    public Xor8 readFromStream() {
        return new Xor8(new ByteArrayInputStream(serialized));
    }

    @Benchmark
    public long getBitCount() {
        return filter.getBitCount();
    }
}
