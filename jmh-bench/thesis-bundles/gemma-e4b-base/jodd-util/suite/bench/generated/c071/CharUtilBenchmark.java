package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharUtilBenchmark {

    // Inputs for benchmarking
    private char[] sampleCharArray;
    private CharSequence sampleCharSequence;
    private byte[] sampleByteArray;
    private char[] matchChars;
    private Charset utf8Charset;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Sample Char Array (Length 100)
        sampleCharArray = new char[100];
        for (int i = 0; i < 100; i++) {
            sampleCharArray[i] = (char) ('a' + (i % 26));
        }

        // 2. Sample Char Sequence (Length 100)
        sampleCharSequence = new String(sampleCharArray);

        // 3. Sample Byte Array (Length 100)
        sampleByteArray = new byte[100];
        for (int i = 0; i < 100; i++) {
            sampleByteArray[i] = (byte) (i % 256);
        }

        // 4. Match Characters for finding operations
        matchChars = new char[]{'a', 'z', '0'};

        // 5. Charset
        utf8Charset = StandardCharsets.UTF_8;
    }

    // --- Simple Conversions ---

    @Benchmark
    public byte[] testToSimpleByteArrayFromCharArray(Blackhole bh) {
        byte[] result = jodd.util.CharUtil.toSimpleByteArray(sampleCharArray);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public byte[] testToSimpleByteArrayFromCharSequence(Blackhole bh) {
        byte[] result = jodd.util.CharUtil.toSimpleByteArray(sampleCharSequence);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char[] testToSimpleCharArrayFromByteArray(Blackhole bh) {
        char[] result = jodd.util.CharUtil.toSimpleCharArray(sampleByteArray);
        bh.consume(result);
        return result;
    }

    // --- Raw Array Conversions ---

    @Benchmark
    public byte[] testToRawByteArrayFromCharArray(Blackhole bh) {
        byte[] result = jodd.util.CharUtil.toRawByteArray(sampleCharArray);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char[] testToRawCharArrayFromByteArray(Blackhole bh) {
        char[] result = jodd.util.CharUtil.toRawCharArray(sampleByteArray);
        bh.consume(result);
        return result;
    }

    // --- Encoding Conversions ---

    @Benchmark
    public byte[] testToByteArrayDefaultEncoding(Blackhole bh) {
        byte[] result = jodd.util.CharUtil.toByteArray(sampleCharArray);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public byte[] testToByteArraySpecificEncoding(Blackhole bh) {
        byte[] result = jodd.util.CharUtil.toByteArray(sampleCharArray, utf8Charset);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char[] testToCharArrayDefaultDecoding(Blackhole bh) {
        char[] result = jodd.util.CharUtil.toCharArray(sampleByteArray);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char[] testToCharArraySpecificDecoding(Blackhole bh) {
        char[] result = jodd.util.CharUtil.toCharArray(sampleByteArray, utf8Charset);
        bh.consume(result);
        return result;
    }

    // --- Find Operations ---

    @Benchmark
    public int testFindFirstEqualCharMatchArray(Blackhole bh) {
        // Start search from index 0
        int result = jodd.util.CharUtil.findFirstEqual(sampleCharArray, 0, matchChars);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int testFindFirstEqualCharMatchSingle(Blackhole bh) {
        // Search for 'a'
        int result = jodd.util.CharUtil.findFirstEqual(sampleCharArray, 0, 'a');
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int testFindFirstDiffCharMatchArray(Blackhole bh) {
        // Search for a character not in matchChars
        char nonMatch = '!';
        int result = jodd.util.CharUtil.findFirstDiff(sampleCharArray, 0, matchChars);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int testFindFirstDiffCharMatchSingle(Blackhole bh) {
        // Search for 'Z' (assuming sampleCharArray only contains lowercase)
        char nonMatch = 'Z';
        int result = jodd.util.CharUtil.findFirstDiff(sampleCharArray, 0, nonMatch);
        bh.consume(result);
        return result;
    }

    // --- Is Checks (Single Character) ---

    @Benchmark
    public boolean testIsWhitespace(Blackhole bh) {
        boolean result = jodd.util.CharUtil.isWhitespace(' ');
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean testIsAlpha(Blackhole bh) {
        boolean result = jodd.util.CharUtil.isAlpha('A');
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean testIsDigit(Blackhole bh) {
        boolean result = jodd.util.CharUtil.isDigit('5');
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean testIsUnreserved(Blackhole bh) {
        boolean result = jodd.util.CharUtil.isUnreserved('-');
        bh.consume(result);
        return result;
    }

    // --- RFC Checks (Integer Input) ---

    @Benchmark
    public boolean testIsGenericDelimiter(Blackhole bh) {
        // Test '/' (ASCII 47)
        boolean result = jodd.util.CharUtil.isGenericDelimiter('/');
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean testIsSubDelimiter(Blackhole bh) {
        // Test '+' (ASCII 43)
        boolean result = jodd.util.CharUtil.isSubDelimiter('+');
        bh.consume(result);
        return result;
    }

    // --- Conversions (Case/Hex) ---

    @Benchmark
    public char testToUpperAscii(Blackhole bh) {
        char result = jodd.util.CharUtil.toUpperAscii('a');
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char testToLowerAscii(Blackhole bh) {
        char result = jodd.util.CharUtil.toLowerAscii('Z');
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int testHex2Int(Blackhole bh) {
        // Test 'F'
        int result = jodd.util.CharUtil.hex2int('F');
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char testInt2Hex(Blackhole bh) {
        // Test 10 (A)
        char result = jodd.util.CharUtil.int2hex(10);
        bh.consume(result);
        return result;
    }
}
