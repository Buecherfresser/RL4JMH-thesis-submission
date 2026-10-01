package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.URIConverter;
import jodd.typeconverter.TypeConversionException;
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
    private URI uriInput;
    private File fileInput;
    private URL urlInput;
    private String stringInput;
    private String invalidUriString;
    private StringBuilder stringBuilderInput;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        converter = new URIConverter();
        uriInput = new URI("https://example.com/path?query=1#fragment");
        fileInput = new File("/tmp/example file.txt");
        urlInput = new URL("https://example.com/path?query=1#fragment");
        stringInput = "https://example.com/path?query=1#fragment";
        invalidUriString = "http://[::1";
        stringBuilderInput = new StringBuilder("https://example.com/from-object");
    }

    @Benchmark
    public void convertNull(Blackhole bh) {
        bh.consume(converter.convert(null));
    }

    @Benchmark
    public URI convertUri() {
        return converter.convert(uriInput);
    }

    @Benchmark
    public URI convertFile() {
        return converter.convert(fileInput);
    }

    @Benchmark
    public URI convertUrl() {
        return converter.convert(urlInput);
    }

    @Benchmark
    public URI convertString() {
        return converter.convert(stringInput);
    }

    @Benchmark
    public URI convertStringBuilder() {
        return converter.convert(stringBuilderInput);
    }

    @Benchmark
    public void convertInvalidString(Blackhole bh) {
        try {
            converter.convert(invalidUriString);
        } catch (TypeConversionException ex) {
            bh.consume(ex);
        }
    }
}
