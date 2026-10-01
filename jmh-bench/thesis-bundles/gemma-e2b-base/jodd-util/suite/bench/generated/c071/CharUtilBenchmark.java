package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import jodd.util.CharUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CharUtilBenchmark {

    // --- Setup State ---
    private char[] testChars;
    private byte[] testBytes;
    private char[] matchChars;
    private int searchIndex;
    private Charset utf8Charset = StandardCharsets.UTF_8;
    private Charset iso88591Charset = Charset.forName("ISO-8859-1");

    @Setup(Level.Trial)
    public void setup() {
        // Create a moderately sized input string for testing
        String inputString = "This is a test string for CharUtil benchmarking. It contains mixed case, numbers, symbols: @#$%.";
        testChars = inputString.toCharArray();
        testBytes = CharUtil.toByteArray(testChars, utf8Charset);

        // Setup match arrays for search operations
        matchChars = new char[testChars.length];
        java.util.Arrays.fill(matchChars, ' '); // Initialize match array

        // Setup search index
        searchIndex = testChars.length / 2;
    }

    // --- Simple Conversions Benchmarks ---

    @Benchmark
    public void testToChar(Blackhole bh) {
        char result = CharUtil.toChar(testBytes[0]);
        bh.consume(result);
    }

    @Benchmark
    public void testToSimpleByteArrayFromCharArray(Blackhole bh) {
        byte[] result = CharUtil.toSimpleByteArray(testChars);
        bh.consume(result);
    }

    @Benchmark
    public void testToSimpleByteArrayFromCharSequence(Blackhole bh) {
        byte[] result = CharUtil.toSimpleByteArray(new String(testChars));
        bh.consume(result);
    }

    @Benchmark
    public void testToSimpleCharArrayFromByteArray(Blackhole bh) {
        char[] result = CharUtil.toSimpleCharArray(testBytes);
        bh.consume(result);
    }

    // --- ASCII Conversions Benchmarks ---

    @Benchmark
    public void testToAsciiByteArrayFromCharArray(Blackhole bh) {
        byte[] result = CharUtil.toAsciiByteArray(testChars);
        bh.consume(result);
    }

    @Benchmark
    public void testToAsciiByteArrayFromCharSequence(Blackhole bh) {
        byte[] result = CharUtil.toAsciiByteArray(new String(testChars));
        bh.consume(result);
    }

    // --- Raw Array Conversions Benchmarks ---

    @Benchmark
    public void testToRawByteArrayFromCharArray(Blackhole bh) {
        byte[] result = CharUtil.toRawByteArray(testChars);
        bh.consume(result);
    }

    @Benchmark
    public void testToRawCharArrayFromByteArray(Blackhole bh) {
        char[] result = CharUtil.toRawCharArray(testBytes);
        bh.consume(result);
    }

    // --- Encoding Benchmarks ---

    @Benchmark
    public void testToByteArrayDefaultEncoding(Blackhole bh) {
        byte[] result = CharUtil.toByteArray(testChars);
        bh.consume(result);
    }

    @Benchmark
    public void testToByteArrayCustomEncoding(Blackhole bh) {
        byte[] result = CharUtil.toByteArray(testChars, iso88591Charset);
        bh.consume(result);
    }

    @Benchmark
    public void testToCharArrayDefaultEncoding(Blackhole bh) {
        char[] result = CharUtil.toCharArray(testBytes);
        bh.consume(result);
    }

    @Benchmark
    public void testToCharArrayCustomEncoding(Blackhole bh) {
        char[] result = CharUtil.toCharArray(testBytes, iso88591Charset);
        bh.consume(result);
    }

    // --- Find/Search Benchmarks ---

    @Benchmark
    public void testFindFirstEqualCharArray(Blackhole bh) {
        int index = CharUtil.findFirstEqual(testChars, searchIndex, matchChars);
        bh.consume(index);
    }

    @Benchmark
    public void testFindFirstEqualChar(Blackhole bh) {
        int index = CharUtil.findFirstEqual(testChars, searchIndex, ' ');
        bh.consume(index);
    }

    @Benchmark
    public void testFindFirstDiffCharArray(Blackhole bh) {
        int index = CharUtil.findFirstDiff(testChars, searchIndex, matchChars);
        bh.consume(index);
    }

    @Benchmark
    public void testFindFirstDiffChar(Blackhole bh) {
        int index = CharUtil.findFirstDiff(testChars, searchIndex, ' ');
        bh.consume(index);
    }

    // --- Classification Benchmarks ---

    @Benchmark
    public void testIsWhitespace(Blackhole bh) {
        boolean result = CharUtil.isWhitespace(testChars[0]);
        bh.consume(result);
    }

    @Benchmark
    public void testIsLowercaseAlpha(Blackhole bh) {
        boolean result = CharUtil.isLowercaseAlpha(testChars[10]);
        bh.consume(result);
    }

    @Benchmark
    public void testIsUppercaseAlpha(Blackhole bh) {
        boolean result = CharUtil.isUppercaseAlpha(testChars[20]);
        bh.consume(result);
    }

    @Benchmark
    public void testIsAlphaOrDigit(Blackhole bh) {
        boolean result = CharUtil.isAlphaOrDigit(testChars[30]);
        bh.consume(result);
    }

    @Benchmark
    public void testIsWordChar(Blackhole bh) {
        boolean result = CharUtil.isWordChar(testChars[40]);
        bh.consume(result);
    }

    @Benchmark
    public void testIsPropertyNameChar(Blackhole bh) {
        boolean result = CharUtil.isPropertyNameChar(testChars[50]);
        bh.consume(result);
    }

    @Benchmark
    public void testIsAlpha(Blackhole bh) {
        boolean result = CharUtil.isAlpha(testChars[60]);
        bh.consume(result);
    }

    @Benchmark
    public void testIsDigit(Blackhole bh) {
        boolean result = CharUtil.isDigit(testChars[70]);
        bh.consume(result);
    }

    @Benchmark
    public void testIsHexDigit(Blackhole bh) {
        boolean result = CharUtil.isHexDigit(testChars[80]);
        bh.consume(result);
    }

    @Benchmark
    public void testIsGenericDelimiter(Blackhole bh) {
        boolean result = CharUtil.isGenericDelimiter('?');
        bh.consume(result);
    }

    @Benchmark
    public void testIsSubDelimiter(Blackhole bh) {
        boolean result = CharUtil.isSubDelimiter(';');
        bh.consume(result);
    }

    @Benchmark
    public void testIsReserved(Blackhole bh) {
        boolean result = CharUtil.isReserved('!');
        bh.consume(result);
    }

    @Benchmark
    public void testIsUnreserved(Blackhole bh) {
        boolean result = CharUtil.isUnreserved('-');
        bh.consume(result);
    }

    @Benchmark
    public void testIsPchar(Blackhole bh) {
        boolean result = CharUtil.isPchar('@');
        bh.consume(result);
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public void testToUpperAscii(Blackhole bh) {
        char result = CharUtil.toUpperAscii('a');
        bh.consume(result);
    }

    @Benchmark
    public void testToLowerAscii(Blackhole bh) {
        char result = CharUtil.toLowerAscii('Z');
        bh.consume(result);
    }

    @Benchmark
    public void testHex2Int(Blackhole bh) {
        int result = CharUtil.hex2int('A');
        bh.consume(result);
    }

    @Benchmark
    public void testInt2Hex(Blackhole bh) {
        char result = CharUtil.int2hex(10);
        bh.consume(result);
    }
}
