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

    private String standardEncodedString;
    private String queryEncodedString;
    private Charset utf8Charset;

    @Setup
    public void setup() {
        // Standard URL encoding (no '+' treated as space)
        // Example: "Hello World!" -> "Hello%20World%21"
        this.standardEncodedString = "Test%20String%2Fwith%26symbols%21";

        // Query string encoding (where '+' is treated as space)
        // Example: "key=value with space" -> "key%3Dvalue+with+space"
        this.queryEncodedString = "search+term%20and+more%2Fpath";

        this.utf8Charset = StandardCharsets.UTF_8;
    }

    /**
     * Benchmarks URLDecoder.decode(String url) using default UTF-8 and no '+' decoding.
     */
    @Benchmark
    public String decode_StandardUrl_UTF8_NoPlus(Blackhole bh) {
        String result = URLDecoder.decode(standardEncodedString);
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks URLDecoder.decode(String source, Charset encoding) using custom UTF-8 and no '+' decoding.
     */
    @Benchmark
    public String decode_StandardUrl_CustomCharset_NoPlus(Blackhole bh) {
        String result = URLDecoder.decode(standardEncodedString, utf8Charset);
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks URLDecoder.decodeQuery(String source) using default UTF-8 and '+' decoding.
     */
    @Benchmark
    public String decodeQuery_UTF8_WithPlus(Blackhole bh) {
        String result = URLDecoder.decodeQuery(queryEncodedString);
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks URLDecoder.decodeQuery(String source, Charset encoding) using custom UTF-8 and '+' decoding.
     */
    @Benchmark
    public String decodeQuery_CustomCharset_WithPlus(Blackhole bh) {
        String result = URLDecoder.decodeQuery(queryEncodedString, utf8Charset);
        bh.consume(result);
        return result;
    }
}
