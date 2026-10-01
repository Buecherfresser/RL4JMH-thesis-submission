package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.URLConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URLConverterBenchmark {

    private URLConverter converter;

    // Inputs for testing different conversion paths
    private URL urlInput;
    private URI uriInput;
    private File fileInput;
    private String stringInput;
    private Object nullInput;

    @Setup
    public void setup() throws Exception {
        converter = new URLConverter();

        // 1. URL Input
        this.urlInput = new URL("http://example.com/path");

        // 2. URI Input
        this.uriInput = new URI("http://example.com/path?query=test");

        // 3. File Input (Requires creating a temporary file)
        File tempFile = File.createTempFile("url_test", ".tmp");
        tempFile.deleteOnExit();
        this.fileInput = tempFile;

        // 4. String Input (Standard URL construction)
        this.stringInput = "https://another.example.com/resource";

        // 5. Null Input
        this.nullInput = null;
    }

    @Benchmark
    public void convert_Null(Blackhole bh) {
        URL result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_ExistingURL(Blackhole bh) {
        URL result = converter.convert(urlInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_ExistingURI(Blackhole bh) {
        URL result = converter.convert(uriInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_File(Blackhole bh) {
        URL result = converter.convert(fileInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_String(Blackhole bh) {
        URL result = converter.convert(stringInput);
        bh.consume(result);
    }
}
