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
     * Benchmarks the execution time of the static method that checks if the
     * current environment is an OSGi bundle.
     */
    @Benchmark
    public void checkOsgiEnvironment(Blackhole bh) {
        boolean result = OsgiUtils.isRunningInOsgiEnvironment();
        bh.consume(result);
    }
}
