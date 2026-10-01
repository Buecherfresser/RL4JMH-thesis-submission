package bench.generated.c078;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.lang.ClassLoader;
import java.util.concurrent.TimeUnit;
import jodd.util.ResourcesUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ResourcesUtilBenchmark {

    private String resourceName;
    private ClassLoader dummyClassLoader;

    @Setup(Level.Trial)
    public void setup() {
        // Use a resource name that is likely to exist or at least test the path logic.
        // Assuming "test_resource.txt" is available on the classpath for successful execution paths.
        resourceName = "test_resource.txt";
        
        // Create a dummy ClassLoader for testing the parameterized methods
        dummyClassLoader = ClassLoader.getSystemClassLoader();
    }

    // --- Benchmarks for getResourceUrl ---

    @Benchmark
    public URL testGetResourceUrlSimple(Blackhole bh) {
        URL url = ResourcesUtil.getResourceUrl(resourceName);
        bh.consume(url);
        return url;
    }

    @Benchmark
    public URL testGetResourceUrlWithClassLoader(Blackhole bh) {
        URL url = ResourcesUtil.getResourceUrl(resourceName, dummyClassLoader);
        bh.consume(url);
        return url;
    }

    // --- Benchmarks for getResourceAsString ---

    @Benchmark
    public void testGetResourceAsString(Blackhole bh) throws IOException {
        // This method throws IOException, so we must handle it or let JMH handle the exception flow.
        // Since we are benchmarking the successful path, we assume the resource exists.
        String content = ResourcesUtil.getResourceAsString(resourceName);
        bh.consume(content);
    }

    // --- Benchmarks for getResourceAsStream ---

    @Benchmark
    public InputStream testGetResourceAsStreamSimple(Blackhole bh) throws IOException {
        InputStream is = ResourcesUtil.getResourceAsStream(resourceName);
        bh.consume(is);
        return is;
    }

    @Benchmark
    public InputStream testGetResourceAsStreamWithClassLoader(Blackhole bh) throws IOException {
        InputStream is = ResourcesUtil.getResourceAsStream(resourceName, dummyClassLoader);
        bh.consume(is);
        return is;
    }

    @Benchmark
    public InputStream testGetResourceAsStreamWithCacheControl(Blackhole bh) throws IOException {
        // Test with caching enabled (true)
        InputStream is = ResourcesUtil.getResourceAsStream(resourceName, dummyClassLoader, true);
        bh.consume(is);
        return is;
    }
}
