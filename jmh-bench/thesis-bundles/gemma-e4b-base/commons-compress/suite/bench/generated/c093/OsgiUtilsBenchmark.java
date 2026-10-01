package bench.generated.c093;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.OsgiUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class OsgiUtilsBenchmark {

    // Since the method is static and relies on static initialization,
    // no complex state setup is required.

    @Benchmark
    public void checkOsgiEnvironment(Blackhole bh) {
        boolean result = OsgiUtils.isRunningInOsgiEnvironment();
        bh.consume(result);
    }
}
