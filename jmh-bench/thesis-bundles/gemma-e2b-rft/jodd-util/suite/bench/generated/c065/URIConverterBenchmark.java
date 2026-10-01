package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.impl.URIConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URIConverterBenchmark {

    private URIConverter converter;
    private URI uriInput;
    private File fileInput;
    private URL urlInput;
    private String genericStringInput;
    private Object nullInput;

    @Setup
    public void setup() throws Exception {
        converter = new URIConverter();

        // 1. Setup URI input
        this.uriInput = new URI("http://example.com/path?q=test");

        // 2. Setup File input
        // Create a temporary file for testing File conversion
        File tempFile = File.createTempFile("test_uri_file", ".tmp");
        tempFile.deleteOnExit();
        this.fileInput = tempFile;

        // 3. Setup URL input
        this.urlInput = new URL("http://another.example.com/resource");

        // 4. Setup Generic String input (for new URI(String) path)
        this.genericStringInput = "https://www.example.com/data/item_123";

        // 5. Setup Null input
        this.nullInput = null;
    }

    @Benchmark
    public void testConvert_Null(Blackhole bh) {
        URI result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void testConvert_URI(Blackhole bh) {
        URI result = converter.convert(uriInput);
        bh.consume(result);
    }

    @Benchmark
    public void testConvert_File(Blackhole bh) {
        URI result = converter.convert(fileInput);
        bh.consume(result);
    }

    @Benchmark
    public void testConvert_URL(Blackhole bh) {
        URI result = converter.convert(urlInput);
        bh.consume(result);
    }

    @Benchmark
    public void testConvert_GenericString(Blackhole bh) {
        URI result = converter.convert(genericStringInput);
        bh.consume(result);
    }
}
