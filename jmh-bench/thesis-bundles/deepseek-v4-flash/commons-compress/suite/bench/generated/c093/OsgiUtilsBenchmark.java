package bench.generated.c093;

import org.openjdk.jmh.annotations.*;
import org.apache.commons.compress.utils.OsgiUtils;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class OsgiUtilsBenchmark {

    @Benchmark
    public boolean isRunningInOsgiEnvironment() {
        return OsgiUtils.isRunningInOsgiEnvironment();
    }
}
