package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.xorplus.XorPlus8;
import java.util.Random;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorPlus8Benchmark {

    private long[] keysToConstruct;
    private long[] testKeys;
    private XorPlus8 filterInstance;
    private byte[] serializedFilterData;

    private static final int KEY_COUNT = 1000;
    private static final int TEST_KEY_COUNT = 100;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate keys for construction
        Random random = new Random(42); // Fixed seed for reproducibility
        keysToConstruct = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keysToConstruct[i] = random.nextLong();
        }

        // 2. Generate test keys for lookups
        testKeys = new long[TEST_KEY_COUNT];
        for (int i = 0; i < TEST_KEY_COUNT; i++) {
            testKeys[i] = random.nextLong();
        }

        // 3. Pre-construct the filter instance for read-only benchmarks
        filterInstance = XorPlus8.construct(keysToConstruct);

        // 4. Pre-serialize the filter data for deserialization benchmarks
        try {
            serializedFilterData = filterInstance.getData();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Failed to serialize filter", e);
        }
    }

    /**
     * Benchmarks the construction time of the XorPlus8 filter.
     * This is a heavy operation, so running it once per invocation is appropriate.
     */
    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        XorPlus8 filter = XorPlus8.construct(keysToConstruct);
        bh.consume(filter);
    }

    /**
     * Benchmarks the lookup time (mayContain) using a pre-constructed filter.
     * We test a single key per invocation.
     */
    @Benchmark
    public void benchmarkMayContain(Blackhole bh) {
        // Use the pre-constructed instance
        long key = testKeys[0];
        boolean result = filterInstance.mayContain(key);
        bh.consume(result);
    }

    /**
     * Benchmarks the serialization time (getData) of the filter.
     */
    @Benchmark
    public void benchmarkSerialization(Blackhole bh) {
        byte[] data = filterInstance.getData();
        bh.consume(data);
    }

    /**
     * Benchmarks the deserialization time (XorPlus8(InputStream)) of the filter.
     */
    @Benchmark
    public void benchmarkDeserialization(Blackhole bh) {
        // Use the pre-serialized data
        ByteArrayInputStream bis = new ByteArrayInputStream(serializedFilterData);
        XorPlus8 filter = new XorPlus8(bis);
        bh.consume(filter);
    }
}
