package bench.generated.c078;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import jodd.util.ResourcesUtil;
import jodd.io.IOUtil;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ResourcesUtilBenchmark {

    private String resourceName;
    private ClassLoader classLoader;

    @Setup
    public void setup() {
        this.resourceName = "jodd/util/ResourcesUtil.class";
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    @Benchmark
    public URL getResourceUrl() {
        return ResourcesUtil.getResourceUrl(resourceName);
    }

    @Benchmark
    public URL getResourceUrlWithClassLoader() {
        return ResourcesUtil.getResourceUrl(resourceName, classLoader);
    }

    @Benchmark
    public String getResourceAsString() throws IOException {
        return ResourcesUtil.getResourceAsString(resourceName);
    }

    @Benchmark
    public byte[] getResourceAsStreamBytes() throws IOException {
        InputStream is = ResourcesUtil.getResourceAsStream(resourceName);
        try {
            return IOUtil.readBytes(is);
        } finally {
            IOUtil.close(is);
        }
    }

    @Benchmark
    public byte[] getResourceAsStreamWithCacheBytes() throws IOException {
        InputStream is = ResourcesUtil.getResourceAsStream(resourceName, classLoader, true);
        try {
            return IOUtil.readBytes(is);
        } finally {
            IOUtil.close(is);
        }
    }

    @Benchmark
    public byte[] getResourceAsStreamNoCacheBytes() throws IOException {
        InputStream is = ResourcesUtil.getResourceAsStream(resourceName, classLoader, false);
        try {
            return IOUtil.readBytes(is);
        } finally {
            IOUtil.close(is);
        }
    }
}
