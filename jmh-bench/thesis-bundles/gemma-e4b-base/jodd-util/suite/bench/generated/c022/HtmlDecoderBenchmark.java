package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.net.HtmlDecoder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class HtmlDecoderBenchmark {

    private String complexHtmlInput;
    private char[] detectNameInput;
    private String lookupNameInput;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Input for decode(String html)
        // Contains named entities, decimal entities, and hex entities.
        complexHtmlInput = "This is a test string with &amp; ampersands, &lt; less than, and &gt; greater than signs. " +
                             "It also has decimal entities like &#38; (ampersand) and hex entities like &#x26; (ampersand). " +
                             "Another entity is &copy; (copyright).";

        // 2. Input for detectName(char[] input, int ndx)
        // A char array starting with a known entity name.
        String entityName = "amp";
        detectNameInput = entityName.toCharArray();

        // 3. Input for lookup(String name)
        lookupNameInput = "amp";
    }

    @Benchmark
    public String decode_ComplexString(Blackhole bh) {
        String result = HtmlDecoder.decode(complexHtmlInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String decode_SimpleString(Blackhole bh) {
        // Test case with no entities
        String simpleInput = "No entities here.";
        String result = HtmlDecoder.decode(simpleInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String decode_EmptyString(Blackhole bh) {
        String emptyInput = "";
        String result = HtmlDecoder.decode(emptyInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String decode_OnlyAmpersand(Blackhole bh) {
        // Test case with only an ampersand, no entity
        String input = "&";
        String result = HtmlDecoder.decode(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String decode_MalformedEntity(Blackhole bh) {
        // Test case with an incomplete entity
        String input = "Test &lt";
        String result = HtmlDecoder.decode(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String detectName_ValidName(Blackhole bh) {
        // Detects the name starting at index 0
        String result = HtmlDecoder.detectName(detectNameInput, 0);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String detectName_NoMatch(Blackhole bh) {
        // Input that does not start with a known entity name
        char[] input = "abc".toCharArray();
        String result = HtmlDecoder.detectName(input, 0);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String detectName_MidString(Blackhole bh) {
        // Test detection starting mid-string
        String fullString = "prefix&amp;suffix";
        char[] input = fullString.toCharArray();
        // Start detection at the '&' of &amp;
        String result = HtmlDecoder.detectName(input, 6);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char[] lookup_ValidName(Blackhole bh) {
        // Looks up a known entity name
        char[] result = HtmlDecoder.lookup(lookupNameInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char[] lookup_InvalidName(Blackhole bh) {
        // Looks up an unknown entity name
        String invalidName = "unknownentity";
        char[] result = HtmlDecoder.lookup(invalidName);
        bh.consume(result);
        return result;
    }
}
