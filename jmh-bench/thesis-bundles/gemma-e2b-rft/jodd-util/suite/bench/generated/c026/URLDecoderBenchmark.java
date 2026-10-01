package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import jodd.net.URLDecoder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class URLDecoderBenchmark {

    // Input data prepared once in @Setup
    private String standardInput;
    private String percentEncodedInput;
    private String plusEncodedInput;
    private String mixedInput;
    private Charset utf8Charset = StandardCharsets.UTF_8;
    private Charset isoCharset = Charset.forName("ISO-8859-1");

    @Setup
    public void setup() {
        // Simple string
        this.standardInput = "Hello World";

        // String with percent encoding (%20 = space)
        this.percentEncodedInput = "Hello%20World";

        // String with plus encoding (+ = space, for query decoding)
        this.plusEncodedInput = "Hello+World";

        // Mixed encoding
        this.mixedInput = "Test%20String+with+plus";
    }

    @Benchmark
    public void decode_StandardUtf8(Blackhole bh) {
        String result = URLDecoder.decode(standardInput);
        bh.consume(result);
    }

    @Benchmark
    public void decode_CustomCharsetNoPlus(Blackhole bh) {
        String result = URLDecoder.decode(standardInput, isoCharset);
        bh.consume(result);
    }

    @Benchmark
    public void decodeQuery_StandardUtf8(Blackhole bh) {
        String result = URLDecoder.decodeQuery(plusEncodedInput);
        bh.consume(result);
    }

    @Benchmark
    public void decodeQuery_CustomCharsetWithPlus(Blackhole bh) {
        String result = URLDecoder.decodeQuery(plusEncodedInput, isoCharset);
        bh.consume(result);
    }
}
