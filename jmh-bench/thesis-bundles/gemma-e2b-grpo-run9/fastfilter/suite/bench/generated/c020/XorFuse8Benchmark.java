package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.fastfilter.xor.XorFuse8;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XorFuse8Benchmark {

    // State field to hold the filter instance. Since XorFuse8 is immutable,
    // we can reuse this instance across benchmark iterations.
    private XorFuse8 xorFuse8;

    @Setup(Level.Trial)
    public void setup() {
        // No complex setup needed as we instantiate inside the benchmark
        // to ensure isolation and avoid static state issues, adhering to anti-patterns.
    }

    @Benchmark
    public void mayContain_Lookup(Blackhole bh) {
        try {
            // Construct a new instance for each benchmark run to ensure isolation
            xorFuse8 = XorFuse8.construct(new long[0]);
        } catch (Exception e) {
            // Ignore exceptions during construction
        }

        // Test lookup with a fixed, non-final key.
        long testKey = 123456789012345L;

        // Call the public method and consume the result via Blackhole
        boolean result = xorFuse8.mayContain(testKey);
        bh.consume(result);
    }

    @Benchmark
    public void mayContain_Lookup_Negative(Blackhole bh) {
        try {
            xorFuse8 = XorFuse8.construct(new long[0]);
        } catch (Exception e) {
            // Ignore
        }

        // Test lookup with a key unlikely to be present
        long testKey = 987654321098765L;

        boolean result = xorFuse8.mayContain(testKey);
        bh.consume(result);
    }
}
