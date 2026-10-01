package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.io.File;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.URLConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URLConverterBenchmark {

    private URLConverter urlConverter;

    @Setup
    public void setup() {
        // Initialize the subject under test. Since URLConverter is stateless,
        // this setup is sufficient.
        this.urlConverter = new URLConverter();
    }

    @Benchmark
    public void convertNull(Blackhole bh) {
        // Test case 1: null input
        try {
            urlConverter.convert(null);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected paths
        }
        bh.consume(null);
    }

    @Benchmark
    public URL convertExistingUrl(Blackhole bh) {
        // Test case 2: existing URL input (identity check)
        try {
            URL existingUrl = new URL("http://example.com");
            URL result = urlConverter.convert(existingUrl);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null; // Return value consumed by Blackhole
    }

    @Benchmark
    public void convertString(Blackhole bh) {
        // Test case 5: General Object conversion via toString() and new URL()
        try {
            // Use a simple string that should convert successfully
            URL result = urlConverter.convert("http://test.com/path");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void convertUri(Blackhole bh) {
        // Test case 4: URI input
        try {
            URI uri = new URI("http://test.com/path");
            URL result = urlConverter.convert(uri);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    // Note: Benchmarking File or complex objects requires mocking or actual file system access,
    // which violates the "in-memory only" rule or requires external dependencies.
    // We omit them to keep the benchmark self-contained and compliant with input constraints.
}
