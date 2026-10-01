package bench.generated.c093;

import org.apache.commons.compress.utils.OsgiUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class OsgiUtilsBenchmark {

    /**
     * Benchmarks the static method isRunningInOsgiEnvironment().
     * Since this method relies on the ClassLoader at initialization,
     * the result should be consistent across benchmarks.
     *
     * @param bh Blackhole to consume the result.
     */
    @Benchmark
    public void checkOsgiEnvironment(Blackhole bh) {
        // Call the static method. The result is consumed by Blackhole.
        boolean result = OsgiUtils.isRunningInOsgiEnvironment();
        bh.consume(result);
    }
}
