package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URLDecoderBenchmark {

    private String simpleEncoded;
    private String complexEncoded;
    private String queryEncoded;
    private String queryEncodedPlus;
    private Charset utf8;
    private Charset iso8859;

    @Setup(Level.Trial)
    public void setup() {
        utf8 = StandardCharsets.UTF_8;
        iso8859 = StandardCharsets.ISO_8859_1;

        simpleEncoded = "hello%20world";
        complexEncoded = "caf%C3%A9%20au%20lait";
        queryEncoded = "name=John+Doe%26age%3D30";
        queryEncodedPlus = "param+with+spaces%20and%20%2Bplus";
    }

    @Benchmark
    public String decodeSimple() {
        return URLDecoder.decode(simpleEncoded);
    }

    @Benchmark
    public String decodeComplexUtf8() {
        return URLDecoder.decode(complexEncoded, utf8);
    }

    @Benchmark
    public String decodeComplexIso() {
        return URLDecoder.decode(complexEncoded, iso8859);
    }

    @Benchmark
    public String decodeQueryDefault() {
        return URLDecoder.decodeQuery(queryEncoded);
    }

    @Benchmark
    public String decodeQueryWithCharset() {
        return URLDecoder.decodeQuery(queryEncodedPlus, utf8);
    }

    @Benchmark
    public String decodeWithPlusFlagFalse() {
        return URLDecoder.decode(queryEncodedPlus, utf8);
    }
}
