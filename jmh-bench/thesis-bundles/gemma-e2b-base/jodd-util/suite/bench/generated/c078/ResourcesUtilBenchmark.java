package bench.generated.c078;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import jodd.util.ResourcesUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ResourcesUtilBenchmark {

    private String resourceName;

    @Setup(Level.Trial)
    public void setup() {
        // Build inputs in @Setup
        this.resourceName = "test_resource.txt";
    }

    @Benchmark
    public void getResourceAsString(Blackhole bh) throws IOException {
        // Call the method that reads the stream and converts it to String
        String result = ResourcesUtil.getResourceAsString(resourceName);
        bh.consume(result);
    }

    @Benchmark
    public void getResourceAsStream(Blackhole bh) throws IOException {
        // Call the method that opens the stream directly
        InputStream result = ResourcesUtil.getResourceAsStream(resourceName);
        bh.consume(result);
    }

    @Benchmark
    public void getResourceAsStreamWithCache(Blackhole bh) throws IOException {
        // Test the version with ClassLoader and cache=true
        // We use the current class loader as the calling class for this test.
        InputStream result = ResourcesUtil.getResourceAsStream(resourceName, ResourcesUtilBenchmark.class.getClassLoader(), true);
        bh.consume(result);
    }

    @Benchmark
    public void getResourceUrl(Blackhole bh) {
        // Test the simple URL retrieval (which calls the null ClassLoader overload)
        URL result = ResourcesUtil.getResourceUrl(resourceName);
        bh.consume(result);
    }

    // Optional: Benchmark a path that might fail (returning null)
    @Benchmark
    public void getResourceUrlNotFound(Blackhole bh) {
        // Test a resource name that is highly unlikely to exist
        URL result = ResourcesUtil.getResourceUrl("non_existent_resource_xyz123");
        bh.consume(result);
    }
}
