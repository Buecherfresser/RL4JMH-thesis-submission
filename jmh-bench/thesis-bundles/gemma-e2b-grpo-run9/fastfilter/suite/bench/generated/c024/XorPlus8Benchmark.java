package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.util.Random;

import org.fastfilter.xorplus.XorPlus8;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorPlus8Benchmark {

    // Instance of the filter to be tested. Initialized in setup.
    private XorPlus8 filter;

    // A fixed array of keys for construction. Must not be static final.
    private long[] keysForConstruction;

    @Setup(Level.Trial)
    public void setup() {
        try {
            // Create a small, non-final array of keys to construct the filter.
            // This simulates building the filter once per trial.
            this.keysForConstruction = new long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L};
            this.filter = XorPlus8.construct(this.keysForConstruction);
        } catch (Exception e) {
            // Handle potential exceptions during construction if necessary,
            // though for a benchmark, we usually let the failure propagate
            // or handle it gracefully if the benchmark must continue.
            System.err.println("Setup failed: " + e.getMessage());
        }
    }

    @Benchmark
    public void benchmarkMayContain(Blackhole bh) {
        // Test the hot path lookup method.
        // We use a key that is likely to be contained or not, depending on the setup.
        boolean result = filter.mayContain(1L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetData(Blackhole bh) {
        // Test the serialization method.
        try {
            byte[] data = filter.getData();
            bh.consume(data);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking if they occur during serialization
        }
    }

    @Benchmark
    public void benchmarkConstruction(Blackhole bh) {
        // Since XorPlus8.construct is static, we call it directly.
        // This measures the cost of creating a new instance.
        try {
            // We call the static constructor, which is expensive.
            XorPlus8 newFilter = XorPlus8.construct(this.keysForConstruction);
            bh.consume(newFilter);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
