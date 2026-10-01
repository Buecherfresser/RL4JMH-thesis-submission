package bench.generated.c070;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.util.CharSequenceUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharSequenceUtilBenchmark {

    // Input data for benchmarks (must not be static final literals)
    private String testString;
    private char[] testCharArray;

    @Setup
    public void setup() {
        // Prepare fixed payloads once in @Setup
        try {
            // Test string: mixed case, special chars, length ~100
            this.testString = "Hello World! This is a test string for CharSequenceUtil benchmarking.";
            this.testCharArray = this.testString.toCharArray();
        } catch (Exception e) {
            throw new RuntimeException("Setup failed", e);
        }
    }

    // --- Benchmarks for equals() methods ---

    @Benchmark
    public void benchmarkEquals(Blackhole bh) {
        // Test equality with identical strings
        boolean result = CharSequenceUtil.equals(this.testString, this.testString);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkEqualsDifferentLength(Blackhole bh) {
        // Test equality with different lengths
        boolean result = CharSequenceUtil.equals(this.testString, "shorter");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkEqualsIgnoreCase(Blackhole bh) {
        // Test case-insensitive equality
        boolean result = CharSequenceUtil.equalsIgnoreCase(this.testString, "hello world! this is a test string for charsequenceutil benchmarking.");
        bh.consume(result);
    }

    // --- Benchmarks for findFirstEqual() methods (CharSequence) ---

    @Benchmark
    public void benchmarkFindFirstEqual_Found(Blackhole bh) {
        // Test case where a match exists near the start
        int result = CharSequenceUtil.findFirstEqual(this.testString, 0, "H");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFindFirstEqual_NotFound(Blackhole bh) {
        // Test case where no match exists
        int result = CharSequenceUtil.findFirstEqual(this.testString, 0, "Z");
        bh.consume(result);
    }

    // --- Benchmarks for findFirstEqual() methods (char[]) ---

    @Benchmark
    public void benchmarkFindFirstEqual_Array_Found(Blackhole bh) {
        // Test array search where a match exists (space character is present)
        int result = CharSequenceUtil.findFirstEqual(this.testCharArray, 5, ' ');
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFindFirstEqual_Array_NotFound(Blackhole bh) {
        // Test array search where no match exists
        int result = CharSequenceUtil.findFirstEqual(this.testCharArray, 0, '!');
        bh.consume(result);
    }

    // --- Benchmarks for findFirstDiff() methods (CharSequence) ---

    @Benchmark
    public void benchmarkFindFirstDiff_Found(Blackhole bh) {
        // Test case where a difference is found early (comparing to a string starting with 'X')
        int result = CharSequenceUtil.findFirstDiff(this.testString, 0, "X");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFindFirstDiff_NotFound(Blackhole bh) {
        // Test case where no difference is found (strings are equal)
        int result = CharSequenceUtil.findFirstDiff(this.testString, 0, "H");
        bh.consume(result);
    }

    // --- Benchmarks for findFirstDiff() methods (char[]) ---

    @Benchmark
    public void benchmarkFindFirstDiff_Array_Found(Blackhole bh) {
        // Test array search where a difference is found (searching for 'Z' which is not in the array)
        int result = CharSequenceUtil.findFirstDiff(this.testCharArray, 0, 'Z');
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFindFirstDiff_Array_NotFound(Blackhole bh) {
        // Test array search where no difference is found (array matches the char)
        int result = CharSequenceUtil.findFirstDiff(this.testCharArray, 0, 'H');
        bh.consume(result);
    }

    // --- Benchmarks for equalsToLowercase() ---

    @Benchmark
    public void benchmarkEqualsToLowercase(Blackhole bh) {
        // Test case where it should return false (different case)
        boolean result = CharSequenceUtil.equalsToLowercase(this.testString, "HELLO WORLD!");
        bh.consume(result);
    }
}
