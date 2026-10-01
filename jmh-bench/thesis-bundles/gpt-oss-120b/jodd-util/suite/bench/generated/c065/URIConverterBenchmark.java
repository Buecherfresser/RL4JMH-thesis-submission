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

    private Object nullInput;
    private URI uriInput;
    private File fileInput;
    private URL urlInput;
    private String stringInput;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        converter = new URIConverter();

        nullInput = null;

        uriInput = new URI("http://example.com/path?query=1");

        fileInput = new File("test.txt");

        urlInput = new URL("http://example.com/resource");

        stringInput = "http://example.com/another/path";
    }

    @Benchmark
    public URI convertNull() {
        return converter.convert(nullInput);
    }

    @Benchmark
    public URI convertFromURI() {
        return converter.convert(uriInput);
    }

    @Benchmark
    public URI convertFromFile() {
        return converter.convert(fileInput);
    }

    @Benchmark
    public URI convertFromURL() {
        return converter.convert(urlInput);
    }

    @Benchmark
    public URI convertFromString() {
        return converter.convert(stringInput);
    }
}
