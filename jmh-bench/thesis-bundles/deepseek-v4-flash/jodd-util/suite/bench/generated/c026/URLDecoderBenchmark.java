package bench.generated.c026;

import jodd.net.URLDecoder;
import org.openjdk.jmh.annotations.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URLDecoderBenchmark {

    private String plain;
    private String encoded;
    private String queryPlus;
    private String queryEncoded;
    private String longEncoded;
    private String mixed;

    private Charset iso8859;

    @Setup(Level.Trial)
    public void setup() {
        plain = "hello world";
        encoded = "hello%20world%21";
        queryPlus = "name=John+Doe&age=25";
        queryEncoded = "name=John%20Doe&age=25";
        longEncoded = "a%20b%20c%20d%20e%20f%20g%20h%20i%20j%20k%20l%20m%20n%20o%20p%20q%20r%20s%20t%20u%20v%20w%20x%20y%20z";
        mixed = "path%2Fto%2Ffile?query=value+with%20spaces";
        iso8859 = StandardCharsets.ISO_8859_1;
    }

    @Benchmark
    public String decodePlain() {
        return URLDecoder.decode(plain);
    }

    @Benchmark
    public String decodeEncoded() {
        return URLDecoder.decode(encoded);
    }

    @Benchmark
    public String decodeLongEncoded() {
        return URLDecoder.decode(longEncoded);
    }

    @Benchmark
    public String decodeMixed() {
        return URLDecoder.decode(mixed);
    }

    @Benchmark
    public String decodeWithCharset() {
        return URLDecoder.decode(encoded, iso8859);
    }

    @Benchmark
    public String decodeQueryPlus() {
        return URLDecoder.decodeQuery(queryPlus);
    }

    @Benchmark
    public String decodeQueryEncoded() {
        return URLDecoder.decodeQuery(queryEncoded);
    }

    @Benchmark
    public String decodeQueryWithCharset() {
        return URLDecoder.decodeQuery(queryPlus, iso8859);
    }
}
