package bench.generated.c019;

import org.fastfilter.xor.Xor8;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Xor8Benchmark {

    // State field to hold the filter instance. Initialized in setup.
    private Xor8 xor8Filter;

    // A fixed, non-final array of keys for construction purposes.
    private long[] setupKeys;

    @Setup
    public void setup() {
        try {
            // 1. Prepare input data for construction (must not be static final literals)
            // Create a mutable array of keys.
            long[] keys = new long[100];
            Random random = new Random(42); // Fixed seed for reproducibility
            for (int i = 0; i < keys.length; i++) {
                keys[i] = random.nextLong();
            }
            this.setupKeys = keys;

            // 2. Construct the Xor8 instance. This is the expensive operation.
            this.xor8Filter = Xor8.construct(this.setupKeys);

        } catch (Exception e) {
            // In a real scenario, handle this error properly. For benchmarking,
            // we might just let the benchmark fail if setup fails.
            System.err.println("Setup failed: " + e.getMessage());
            this.xor8Filter = null;
        }
    }

    @Benchmark
    public void mayContain(Blackhole bh) {
        if (xor8Filter == null) {
            return;
        }
        // Call the core method. The result is consumed by Blackhole.
        boolean result = xor8Filter.mayContain(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void getData(Blackhole bh) {
        if (xor8Filter == null) {
            return;
        }
        try {
            // Call the method that involves I/O operations.
            byte[] data = xor8Filter.getData();
            bh.consume(data);
        } catch (Exception e) {
            // Ignore exceptions during benchmark execution if they occur
        }
    }
}
