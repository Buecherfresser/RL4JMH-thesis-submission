package bench.generated.c019;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.xor.Xor8;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Xor8Benchmark {

    private long[] keys;
    private Xor8 filter;
    private byte[] serializedData;
    private long testKey;

    private static final int KEY_COUNT = 10000;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Generate input keys
        keys = new long[KEY_COUNT];
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the filter (This is the expensive setup step)
        try {
            filter = Xor8.construct(keys);
        } catch (Exception e) {
            throw new RuntimeException("Xor8 construction failed during setup.", e);
        }

        // 3. Generate a test key for lookups
        testKey = random.nextLong();

        // 4. Serialize the filter
        try {
            serializedData = filter.getData();
        } catch (Exception e) {
            throw new RuntimeException("Xor8 serialization failed during setup.", e);
        }
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Re-run construction using the same keys to measure the cost of the algorithm
        Xor8 newFilter = Xor8.construct(keys);
        bh.consume(newFilter);
    }

    @Benchmark
    public void benchmarkLookup(Blackhole bh) {
        // Measure the hot path: mayContain
        boolean result = filter.mayContain(testKey);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSerialization(Blackhole bh) {
        // Measure the cost of generating the byte array representation
        byte[] data = filter.getData();
        bh.consume(data);
    }

    @Benchmark
    public void benchmarkDeserialization(Blackhole bh) {
        // Measure the cost of reconstructing the filter from the stream.
        // We must create a fresh stream for each invocation to ensure correct state.
        try (ByteArrayInputStream in = new ByteArrayInputStream(serializedData)) {
            // Xor8 constructor wraps IOException in RuntimeException
            Xor8 newFilter = new Xor8(in);
            bh.consume(newFilter);
        } catch (RuntimeException e) {
            // Catch the wrapper exception thrown by Xor8 constructor
        } catch (IOException e) {
            // Catch IOException thrown by ByteArrayInputStream.close()
        }
    }
}
