package bench.generated.c022;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.net.HtmlDecoder;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class HtmlDecoderBenchmark {

    // Inputs for decode benchmarks
    private String plainText;
    private String namedEntitiesText;
    private String decimalNumericText;
    private String hexNumericText;

    // Inputs for detectName benchmarks
    private char[] existingEntityChars;
    private char[] nonExistingEntityChars;
    private int existingEntityIndex;
    private int nonExistingEntityIndex;

    // Inputs for lookup benchmarks
    private String existingEntityName;
    private String nonExistingEntityName;

    @Setup(Level.Trial)
    public void setUp() {
        // Simple text without any HTML entities
        plainText = "The quick brown fox jumps over the lazy dog.";

        // Text containing several named HTML entities
        namedEntitiesText = "Fish &amp; chips &lt;3 &quot;quotes&quot; &apos;single&apos;";

        // Text containing decimal numeric character reference
        decimalNumericText = "Letter A: &#65;";

        // Text containing hexadecimal numeric character reference
        hexNumericText = "Letter A hex: &#x41;";

        // Char array for existing entity detection ("amp")
        existingEntityChars = new char[] { 'a', 'm', 'p', ';' };
        existingEntityIndex = 0; // start at 'a'

        // Char array for non‑existing entity detection ("zzz")
        nonExistingEntityChars = new char[] { 'z', 'z', 'z', ';' };
        nonExistingEntityIndex = 0;

        // Names for lookup
        existingEntityName = "amp";
        nonExistingEntityName = "nonexistententity";
    }

    // -------------------------------------------------------------------------
    // decode benchmarks
    // -------------------------------------------------------------------------

    @Benchmark
    public String decodePlainText() {
        return HtmlDecoder.decode(plainText);
    }

    @Benchmark
    public String decodeNamedEntities() {
        return HtmlDecoder.decode(namedEntitiesText);
    }

    @Benchmark
    public String decodeDecimalNumeric() {
        return HtmlDecoder.decode(decimalNumericText);
    }

    @Benchmark
    public String decodeHexNumeric() {
        return HtmlDecoder.decode(hexNumericText);
    }

    // -------------------------------------------------------------------------
    // detectName benchmarks
    // -------------------------------------------------------------------------

    @Benchmark
    public String detectExistingName() {
        return HtmlDecoder.detectName(existingEntityChars, existingEntityIndex);
    }

    @Benchmark
    public String detectNonExistingName() {
        return HtmlDecoder.detectName(nonExistingEntityChars, nonExistingEntityIndex);
    }

    // -------------------------------------------------------------------------
    // lookup benchmarks
    // -------------------------------------------------------------------------

    @Benchmark
    public char[] lookupExisting() {
        return HtmlDecoder.lookup(existingEntityName);
    }

    @Benchmark
    public char[] lookupNonExisting() {
        return HtmlDecoder.lookup(nonExistingEntityName);
    }

    // -------------------------------------------------------------------------
    // void benchmark example (consumes result via Blackhole)
    // -------------------------------------------------------------------------

    @Benchmark
    public void decodeAndConsume(Blackhole bh) {
        bh.consume(HtmlDecoder.decode(namedEntitiesText));
    }
}
