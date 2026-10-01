package bench.generated.c078;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import jodd.util.ResourcesUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ResourcesUtilBenchmark {

    // Since ResourcesUtil is static, we don't need instance fields for state,
    // but we keep the class structure as required.

    @Benchmark
    public void testGetResourceUrl_Simple(Blackhole bh) {
        try {
            // Test a simple, non-null path. The actual result (URL) is consumed by Blackhole.
            URL url = ResourcesUtil.getResourceUrl("test/resource.txt");
            bh.consume(url);
        } catch (Exception e) {
            // Catching exceptions is necessary if the static method throws checked exceptions,
            // though getResourceUrl seems to return null on failure rather than throwing IOException.
        }
    }

    @Benchmark
    public void testGetResourceUrl_RootPath(Blackhole bh) {
        try {
            // Test a path starting with a slash, which should be normalized internally.
            URL url = ResourcesUtil.getResourceUrl("/resource/path");
            bh.consume(url);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected failures
        }
    }

    @Benchmark
    public void testGetResourceUrl_EmptyName(Blackhole bh) {
        try {
            // Test an empty string input.
            URL url = ResourcesUtil.getResourceUrl("");
            bh.consume(url);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
