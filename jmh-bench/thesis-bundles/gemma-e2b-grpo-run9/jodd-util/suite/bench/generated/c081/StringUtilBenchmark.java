package bench.generated.c081;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import jodd.util.StringUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringUtilBenchmark {

    // State fields for read-only or reusable inputs
    private String testString;
    private byte[] testBytes;

    @Setup
    public void setup() {
        // Setup a moderately long string for testing
        this.testString = "This is a test string with various characters, including spaces, punctuation, and mixed case. Test string.";
        this.testBytes = this.testString.getBytes();
    }

    // --- replace benchmarks ---

    @Benchmark
    public void testReplace(Blackhole bh) {
        String result = StringUtil.replace(testString, "test", "REPLACED");
        bh.consume(result);
    }

    @Benchmark
    public void testReplaceChar(Blackhole bh) {
        String result = StringUtil.replaceChar(testString, 't', 'X');
        bh.consume(result);
    }

    @Benchmark
    public void testReplaceChars(Blackhole bh) {
        String result = StringUtil.replaceChars(testString, new char[] {'t', 's'}, new char[] {'X', 'Y'});
        bh.consume(result);
    }

    @Benchmark
    public void testReplaceFirst(Blackhole bh) {
        String result = StringUtil.replaceFirst(testString, "test", "FIRST");
        bh.consume(result);
    }

    @Benchmark
    public void testReplaceLast(Blackhole bh) {
        String result = StringUtil.replaceLast(testString, "test", "LAST");
        bh.consume(result);
    }

    // --- remove benchmarks ---

    @Benchmark
    public void testRemove(Blackhole bh) {
        String result = StringUtil.remove(testString, "test");
        bh.consume(result);
    }

    @Benchmark
    public void testRemoveChars(Blackhole bh) {
        String result = StringUtil.removeChars(testString, "aeiou");
        bh.consume(result);
    }

    @Benchmark
    public void testRemoveCharsCharArray(Blackhole bh) {
        String result = StringUtil.removeChars(testString, new char[] {'t', 's'});
        bh.consume(result);
    }

    @Benchmark
    public void testRemoveSingleChar(Blackhole bh) {
        String result = StringUtil.remove(testString, 's');
        bh.consume(result);
    }

    // --- case change benchmarks ---

    @Benchmark
    public void testToLowercase(Blackhole bh) {
        String result = StringUtil.toLowerCase(testString);
        bh.consume(result);
    }

    @Benchmark
    public void testToUpperCase(Blackhole bh) {
        String result = StringUtil.toUpperCase(testString);
        bh.consume(result);
    }

    @Benchmark
    public void testDecapitalize(Blackhole bh) {
        String result = StringUtil.decapitalize(testString);
        bh.consume(result);
    }

    @Benchmark
    public void testTitle(Blackhole bh) {
        String result = StringUtil.title(testString);
        bh.consume(result);
    }

    // --- substring benchmarks ---

    @Benchmark
    public void testSubstring(Blackhole bh) {
        String result = StringUtil.substring(testString, 5, 10);
        bh.consume(result);
    }

    @Benchmark
    public void testSubstringNegativeIndices(Blackhole bh) {
        String result = StringUtil.substring(testString, -2, -1);
        bh.consume(result);
    }

    // --- split benchmarks ---

    @Benchmark
    public void testSplit(Blackhole bh) {
        String[] result = StringUtil.split(testString, " ");
        bh.consume(result);
    }

    @Benchmark
    public void testSplitChar(Blackhole bh) {
        String[] result = StringUtil.splitc(testString, " ,");
        bh.consume(result);
    }

    // --- indexOf benchmarks ---

    @Benchmark
    public void testIndexOfIgnoreCase(Blackhole bh) {
        int index = StringUtil.indexOfIgnoreCase(testString, "test");
        bh.consume(index);
    }

    @Benchmark
    public void testIndexOfNonWhitespace(Blackhole bh) {
        int index = StringUtil.indexOfNonWhitespace(testString);
        bh.consume(index);
    }

    @Benchmark
    public void testIndexOfChars(Blackhole bh) {
        int index = StringUtil.indexOfChars(testString, "aeiou");
        bh.consume(index);
    }

    // --- join benchmarks ---

    @Benchmark
    public void testJoinObjectArray(Blackhole bh) {
        String result = StringUtil.join(new Object[]{"A", 1, true});
        bh.consume(result);
    }

    @Benchmark
    public void testJoinStringSeparator(Blackhole bh) {
        String result = StringUtil.join(new String[]{"A", "B", "C"}, ",");
        bh.consume(result);
    }

    // --- surround benchmarks ---

    @Benchmark
    public void testSurround(Blackhole bh) {
        String result = StringUtil.surround(testString, "PREFIX", "SUFFIX");
        bh.consume(result);
    }

    // --- hex benchmarks (using byte array) ---

    @Benchmark
    public void testToHexString(Blackhole bh) {
        String result = StringUtil.toHexString(testBytes);
        bh.consume(result);
    }
}
