package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.URLConverter;
import java.io.File;
import java.net.URI;
import java.net.URL;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URLConverterBenchmark {

    private URLConverter converter;
    private String testString;
    private File testFile;
    private URI testUri;
    private URL testUrl;

    @Setup(Level.Trial)
    public void setup() {
        converter = new URLConverter();
        
        // 1. String input
        testString = "http://www.example.com/path?query=test&param=value";

        // 2. File input (using a dummy file object for testing the path logic)
        // Note: FileUtil.toURL might require a real file, but for benchmarking the conversion logic path, 
        // a constructed File object is sufficient if the underlying implementation handles non-existent files gracefully 
        // or if we assume a valid path structure.
        testFile = new File("/tmp/test_file.txt");

        // 3. URI input
        testUri = URI.create("https://api.service.org/v1/data");

        // 4. URL input (identity check)
        try {
            testUrl = new URL("http://localhost:8080");
        } catch (java.net.MalformedURLException e) {
            throw new RuntimeException("Failed to create test URL", e);
        }
    }

    @Benchmark
    public URL convertString(Blackhole bh) {
        URL result = converter.convert(testString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public URL convertFile(Blackhole bh) {
        URL result = converter.convert(testFile);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public URL convertURI(Blackhole bh) {
        URL result = converter.convert(testUri);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public URL convertURL(Blackhole bh) {
        URL result = converter.convert(testUrl);
        bh.consume(result);
        return result;
    }
}
