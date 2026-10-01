package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import jodd.net.URLDecoder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class URLDecoderBenchmark {

    private String standardInput;
    private String encodedInput;
    private String queryInput;
    private Charset isoCharset = Charset.forName("ISO-8859-1");

    @Setup
    public void setup() {
        // Input 1: Standard string with percent encoding (%20 space, %41 A)
        this.standardInput = "Hello%20World%21";

        // Input 2: String requiring decoding with a different charset (e.g., ISO-8859-1)
        // Using characters that might be ambiguous in UTF-8 but clear in ISO-8859-1
        this.encodedInput = "Caf\u00E9"; // 'é' in ISO-8859-1
        
        // Input 3: Query string requiring '+' decoding
        this.queryInput = "name=John+Doe&city=New+York";
    }

    @Benchmark
    public void decode_Standard(Blackhole bh) {
        String result = URLDecoder.decode(standardInput);
        bh.consume(result);
    }

    @Benchmark
    public void decode_CustomCharset(Blackhole bh) {
        String result = URLDecoder.decode(encodedInput, isoCharset);
        bh.consume(result);
    }

    @Benchmark
    public void decodeQuery_Standard(Blackhole bh) {
        String result = URLDecoder.decodeQuery(queryInput);
        bh.consume(result);
    }

    @Benchmark
    public void decodeQuery_CustomCharset(Blackhole bh) {
        String result = URLDecoder.decodeQuery(queryInput, isoCharset);
        bh.consume(result);
    }
}
