package bench.generated.c018;

import org.fastfilter.xor.Xor16;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Xor16Benchmark {

    @Benchmark
    public void benchmarkConstruct(Blackhole bh) {
        try {
            // Create a small, non-static array of keys.
            // This ensures the input is not a compile-time constant (Rule 8).
            long[] keys = {1L, 2L, 3L, 4L, 5L};
            Xor16 filter = Xor16.construct(keys);
            bh.consume(filter);
        } catch (Exception e) {
            // Suppress exceptions for benchmark stability if possible, or handle them minimally.
        }
    }

    @Benchmark
    public void benchmarkMayContain(Blackhole bh) {
        try {
            // Construct a filter instance for the lookup test.
            long[] keys = {100L, 200L, 300L};
            Xor16 filter = Xor16.construct(keys);

            // Test containment for a key that might or might not be present
            long keyToCheck = 100L;
            boolean result = filter.mayContain(keyToCheck);
            bh.consume(result);

        } catch (Exception e) {
            // Suppress exceptions
        }
    }
}
