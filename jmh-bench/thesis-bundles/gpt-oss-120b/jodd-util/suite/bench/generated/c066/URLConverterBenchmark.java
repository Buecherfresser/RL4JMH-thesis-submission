package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.URLConverter;
import java.net.URL;
import java.io.File;
import java.net.URI;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URLConverterBenchmark {

    private URLConverter converter;
    private URL url;
    private File file;
    private URI uri;
    private String urlString;

    @Setup
    public void setup() {
        converter = new URLConverter();
        try {
            url = new URL("https://example.com/path?query=1");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        file = new File("test.txt");
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        urlString = url.toString();
    }

    @Benchmark
    public URL convertFromUrl() {
        return converter.convert(url);
    }

    @Benchmark
    public URL convertFromFile() {
        return converter.convert(file);
    }

    @Benchmark
    public URL convertFromUri() {
        return converter.convert(uri);
    }

    @Benchmark
    public URL convertFromString() {
        return converter.convert(urlString);
    }

    @Benchmark
    public URL convertFromNull() {
        return converter.convert(null);
    }
}
