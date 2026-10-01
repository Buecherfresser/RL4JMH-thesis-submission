package bench.generated.c024;

import org.fastfilter.Filter;
import org.fastfilter.xorplus.XorPlus8;
import java.io.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorPlus8Benchmark {

    private XorPlus8 filter;
    private long[] keys;
    private long[] lookupKeys;
    private long[] missingKeys;
    private byte[] serializedData;

    private static final int KEY_SET_SIZE = 10000;
    private static final Random RANDOM = new Random(42);

    @Setup
    public void setup() throws IOException {
        // 1. Generate a large set of unique keys for construction
        keys = new long[KEY_SET_SIZE];
        for (int i = 0; i < KEY_SET_SIZE; i++) {
            keys[i] = RANDOM.nextLong();
        }

        // 2. Create lookup sets (subset of keys for testing containment)
        lookupKeys = new long[KEY_SET_SIZE / 10];
        missingKeys = new long[KEY_SET_SIZE - (KEY_SET_SIZE / 10)];

        for (int i = 0; i < KEY_SET_SIZE / 10; i++) {
            lookupKeys[i] = keys[i * 10];
        }
        for (int i = 0; i < KEY_SET_SIZE - (KEY_SET_SIZE / 10); i++) {
            missingKeys[i] = keys[i * 10 + (i % 10)];
        }

        // 3. Construct the filter (Expensive operation, done once per trial)
        System.out.println("Starting XorPlus8 construction...");
        filter = XorPlus8.construct(keys);
        System.out.println("XorPlus8 construction complete.");

        // 4. Prepare I/O resources for getData() benchmark
        serializedData = filter.getData();
    }

    @Benchmark
    public void testMayContainPresent(Blackhole bh) {
        long key = lookupKeys[RANDOM.nextInt(lookupKeys.length)];
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void testMayContainAbsent(Blackhole bh) {
        long key = missingKeys[RANDOM.nextInt(missingKeys.length)];
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void testGetBitCount(Blackhole bh) {
        long bitCount = filter.getBitCount();
        bh.consume(bitCount);
    }

    @Benchmark
    public void testGetDataSerialization(Blackhole bh) {
        byte[] data = filter.getData();
        bh.consume(data);
    }

    @Benchmark
    public void testStreamConstruction(Blackhole bh) throws IOException {
        // Recreate an InputStream from the serialized data to test the constructor
        ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
        XorPlus8 streamFilter = new XorPlus8(bais);
        bh.consume(streamFilter);
    }
}
