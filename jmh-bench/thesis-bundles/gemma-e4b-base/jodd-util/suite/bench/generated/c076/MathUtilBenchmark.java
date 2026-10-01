package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.MathUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MathUtilBenchmark {

    // --- Inputs for parseDigit ---
    private char digitNumeric = '5';
    private char digitLowercase = 'a';
    private char digitUppercase = 'A';
    private char digitOther = '$';

    // --- Inputs for randomLong ---
    private long randomLongMin = 1L;
    private long randomLongMax = 1000L;

    // --- Inputs for randomInt ---
    private int randomIntMin = 0;
    private int randomIntMax = 100;

    // --- Inputs for isEven/isOdd ---
    private int evenNumber = 42;
    private int oddNumber = 13;

    // --- Inputs for humanReadableByteCount ---
    private long largeBytes = 1234567890L;
    private long smallBytes = 500L;
    private boolean useSi = true;

    @Setup(Level.Trial)
    public void setup() {
        // Inputs are simple primitives, so setup is minimal.
    }

    // =========================================================================
    // Benchmarks for parseDigit(final char digit)
    // =========================================================================

    @Benchmark
    public void parseDigitNumericTest(Blackhole bh) {
        int result = MathUtil.parseDigit(digitNumeric);
        bh.consume(result);
    }

    @Benchmark
    public void parseDigitLowercaseTest(Blackhole bh) {
        int result = MathUtil.parseDigit(digitLowercase);
        bh.consume(result);
    }

    @Benchmark
    public void parseDigitUppercaseTest(Blackhole bh) {
        int result = MathUtil.parseDigit(digitUppercase);
        bh.consume(result);
    }

    @Benchmark
    public void parseDigitOtherTest(Blackhole bh) {
        // Should return 10 + digit - 'A' if it fails the first two checks
        int result = MathUtil.parseDigit(digitOther);
        bh.consume(result);
    }

    // =========================================================================
    // Benchmarks for randomLong(final long min, final long max)
    // =========================================================================

    @Benchmark
    public void randomLongTest(Blackhole bh) {
        long result = MathUtil.randomLong(randomLongMin, randomLongMax);
        bh.consume(result);
    }

    // =========================================================================
    // Benchmarks for randomInt(final int min, final int max)
    // =========================================================================

    @Benchmark
    public void randomIntTest(Blackhole bh) {
        int result = MathUtil.randomInt(randomIntMin, randomIntMax);
        bh.consume(result);
    }

    // =========================================================================
    // Benchmarks for isEven(final int x) and isOdd(final int x)
    // =========================================================================

    @Benchmark
    public void isEvenTest(Blackhole bh) {
        boolean result = MathUtil.isEven(evenNumber);
        bh.consume(result);
    }

    @Benchmark
    public void isOddTest(Blackhole bh) {
        boolean result = MathUtil.isOdd(oddNumber);
        bh.consume(result);
    }

    // =========================================================================
    // Benchmarks for humanReadableByteCount(final long bytes, final boolean useSi)
    // =========================================================================

    @Benchmark
    public void humanReadableByteCountLargeSiTest(Blackhole bh) {
        String result = MathUtil.humanReadableByteCount(largeBytes, useSi);
        bh.consume(result);
    }

    @Benchmark
    public void humanReadableByteCountLargeNonSiTest(Blackhole bh) {
        String result = MathUtil.humanReadableByteCount(largeBytes, !useSi);
        bh.consume(result);
    }

    @Benchmark
    public void humanReadableByteCountSmallSiTest(Blackhole bh) {
        String result = MathUtil.humanReadableByteCount(smallBytes, useSi);
        bh.consume(result);
    }
}
