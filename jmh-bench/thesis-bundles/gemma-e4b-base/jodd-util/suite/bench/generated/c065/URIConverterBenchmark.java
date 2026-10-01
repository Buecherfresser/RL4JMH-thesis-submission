package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.URIConverter;
import java.io.File;
import java.net.URI;
import java.net.URL;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URIConverterBenchmark {

    private URIConverter converter;
    private String inputString;
    private File inputFile;
    private URL inputUrl;
    private URI inputUri;
    private Object nullInput;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        converter = new URIConverter();

        // 1. String input
        inputString = "http://example.com/path?query=test&param=value";

        // 2. File input
        // Create a dummy file object for testing File conversion path
        inputFile = new File("temp_test_file.txt");
        // Ensure the file exists or is at least valid for toURI() call
        if (!inputFile.exists()) {
            inputFile.createNewFile();
        }

        // 3. URL input
        inputUrl = new URL("http://example.com/path");

        // 4. URI input (identity check)
        inputUri = new URI("http://example.com/path");

        // 5. Null input
        nullInput = null;
    }

    @Benchmark
    public URI benchmarkStringConversion(Blackhole bh) {
        URI result = converter.convert(inputString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public URI benchmarkFileConversion(Blackhole bh) {
        URI result = converter.convert(inputFile);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public URI benchmarkUrlConversion(Blackhole bh) {
        URI result = converter.convert(inputUrl);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public URI benchmarkUriIdentityConversion(Blackhole bh) {
        URI result = converter.convert(inputUri);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public URI benchmarkNullConversion(Blackhole bh) {
        URI result = converter.convert(nullInput);
        bh.consume(result);
        return result;
    }
}
