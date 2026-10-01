package bench.generated.c021;

import org.fastfilter.xor.XorSimple;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorSimpleBenchmark {

    private XorSimple xorSimpleInstance;

    @Setup
    public void setup() {
        try {
            // Build a representative instance. This involves internal randomness and computation.
            // We use a small, fixed set of keys for setup.
            long[] keys = {1L, 2L, 3L, 4L, 5L};
            this.xorSimpleInstance = XorSimple.construct(keys);
        } catch (Exception e) {
            // Handle potential exceptions during construction if necessary, though unlikely for this simple case.
            System.err.println("Error during XorSimple setup: " + e.getMessage());
        }
    }

    @Benchmark
    public void mayContain(Blackhole bh) {
        // Test the core lookup functionality.
        // We use a key that might or might not be contained based on the setup.
        boolean result = xorSimpleInstance.mayContain(100L);
        bh.consume(result);
    }

    @Benchmark
    public void mayContain_FalsePositive(Blackhole bh) {
        // Test another key.
        boolean result = xorSimpleInstance.mayContain(999L);
        bh.consume(result);
    }
}
