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

    // Since OsgiUtils is purely static and has no state, no @State fields are required.

    /**
     * Benchmarks the execution time of the static method that checks if the
     * current environment is an OSGi bundle.
     *
     * This measures the overhead of the static method call and internal static checks.
     *
     * @param bh Blackhole to consume the result of the operation.
     */
    @Benchmark
    public void checkOsgiEnvironment(Blackhole bh) {
        boolean result = OsgiUtils.isRunningInOsgiEnvironment();
        bh.consume(result);
    }
}
