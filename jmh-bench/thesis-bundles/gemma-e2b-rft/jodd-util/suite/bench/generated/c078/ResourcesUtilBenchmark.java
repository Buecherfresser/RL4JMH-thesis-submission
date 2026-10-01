package bench.generated.c078;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.TimeUnit;

import jodd.util.ResourcesUtil;
import jodd.io.IOUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ResourcesUtilBenchmark {

    // State for ClassLoader simulation
    private ClassLoader mockClassLoader;
    private ClassLoader currentClassLoader;

    // State for resource names
    private final String resourceName1 = "test_resource_1.txt";

    // State for input streams/data
    private final byte[] dummyData = "This is test data for benchmarking IO operations.".getBytes();
    private final ByteArrayInputStream inputStream = new ByteArrayInputStream(dummyData);

    // State for URL simulation
    private URL dummyUrl;

    @Setup
    public void setup() throws IOException {
        // Initialize ClassLoaders
        this.currentClassLoader = ResourcesUtilBenchmark.class.getClassLoader();
        this.mockClassLoader = new URLClassLoader(new URL[]{new URL("file:///dummy")}, currentClassLoader);

        // Simulate a successful URL resolution for testing stream opening logic
        this.dummyUrl = new URL("http://localhost:8080/resource");
    }

    // --- Benchmarks for getResourceUrl(String resourceName) ---

    @Benchmark
    public void getResourceUrl_NoClassLoader(Blackhole bh) {
        URL url = ResourcesUtil.getResourceUrl(resourceName1);
        bh.consume(url);
    }

    @Benchmark
    public void getResourceUrl_WithClassLoader(Blackhole bh) {
        URL url = ResourcesUtil.getResourceUrl(resourceName1, mockClassLoader);
        bh.consume(url);
    }

    // --- Benchmarks for getResourceAsStream(String resourceName) ---

    @Benchmark
    public void getResourceAsStream_NoClassLoader(Blackhole bh) throws IOException {
        // This tests the path that calls getResourceAsStream(resourceName, null)
        InputStream stream = ResourcesUtil.getResourceAsStream(resourceName1);
        bh.consume(stream);
    }

    // --- Benchmarks for getResourceAsStream(String resourceName, ClassLoader callingClass) ---

    @Benchmark
    public void getResourceAsStream_WithClassLoader(Blackhole bh) throws IOException {
        // This tests the path that uses getResourceUrl(name, classLoader) and then url.openStream()
        InputStream stream = ResourcesUtil.getResourceAsStream(resourceName1, mockClassLoader);
        bh.consume(stream);
    }

    // --- Benchmarks for getResourceAsStream(String resourceName, ClassLoader callingClass, boolean useCache) ---

    @Benchmark
    public void getResourceAsStream_WithCache(Blackhole bh) throws IOException {
        // This tests the path that uses getResourceUrl(name, classLoader) and then opens connection with caching enabled
        InputStream stream = ResourcesUtil.getResourceAsStream(resourceName1, mockClassLoader, true);
        bh.consume(stream);
    }

    // --- Benchmarks for getResourceAsString(String resourceName) ---

    @Benchmark
    public String getResourceAsString(Blackhole bh) throws IOException {
        // Simulates reading a resource stream into a String
        String result = ResourcesUtil.getResourceAsString(resourceName1);
        bh.consume(result);
        return result;
    }
}
