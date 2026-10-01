package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.decimal4j.immutable.Decimal1f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
public class Decimal1fBenchmark {

    // --- State Fields for Inputs ---
    private long longInput;
    private double doubleInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private BigInteger bigIntegerInput;
    private String stringInputWithFraction;

    private RoundingMode roundingModeHalfUp;
    private RoundingMode roundingModeDown;
    private RoundingMode roundingModeUnnecessary;

    private Decimal1f resultLong;
    private Decimal1f resultDouble;
    private Decimal1f resultBigDecimal;
    private Decimal1f resultString;
    private Decimal1f resultBigInteger;
    private Decimal1f resultStringWithFraction;

    @Setup
    public void setup() {
        // Setup Long inputs
        longInput = 123456789L;

        // Setup Double inputs
        doubleInput = 123.456789;

        // Setup BigDecimal inputs
        bigDecimalInput = new BigDecimal("123456789.123");

        // Setup String inputs
        stringInput = "123456789";
        stringInputWithFraction = "123456789.12345";

        // Setup BigInteger inputs
        bigIntegerInput = new BigInteger("9876543210");

        // Setup Rounding Modes
        roundingModeHalfUp = RoundingMode.HALF_UP;
        roundingModeDown = RoundingMode.DOWN;
        roundingModeUnnecessary = RoundingMode.UNNECESSARY;
    }

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        Decimal1f result = Decimal1f.valueOf(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        Decimal1f result = Decimal1f.valueOf(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        Decimal1f result = Decimal1f.valueOf(doubleInput, roundingModeHalfUp);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal1f result = Decimal1f.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        Decimal1f result = Decimal1f.valueOf(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfStringWithRounding(Blackhole bh) {
        Decimal1f result = Decimal1f.valueOf(stringInputWithFraction, roundingModeHalfUp);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        Decimal1f result = Decimal1f.valueOf(bigIntegerInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaled(Blackhole bh) {
        Decimal1f result = Decimal1f.valueOfUnscaled(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledWithScale(Blackhole bh) {
        int scale = 3;
        Decimal1f result = Decimal1f.valueOfUnscaled(longInput, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledWithScaleAndRounding(Blackhole bh) {
        int scale = 2;
        Decimal1f result = Decimal1f.valueOfUnscaled(longInput, scale, roundingModeHalfUp);
        bh.consume(result);
    }
}
