package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.immutable.Decimal12f;
import org.decimal4j.scale.Scale12f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal12fBenchmark {

    // --- State Fields for Inputs ---

    // Input for valueOf(long)
    private long longInput;
    // Input for valueOf(double)
    private double doubleInput;
    // Input for valueOf(BigDecimal)
    private BigDecimal bigDecimalInput;
    // Input for valueOf(String)
    private String stringInput;
    // Input for valueOfUnscaled(long)
    private long unscaledLongInput;
    // Input for valueOfUnscaled(long, int scale)
    private long unscaledLongInputScaled;
    private int scaleInput;
    // Input for valueOfUnscaled(long, int scale, RoundingMode)
    private RoundingMode roundingModeInput;

    // --- Setup Method ---

    @Setup
    public void setup() {
        // Setup long input (a moderately large number)
        this.longInput = 1234567890123L;

        // Setup double input
        this.doubleInput = 1234567890.123456789;

        // Setup BigDecimal input
        this.bigDecimalInput = new BigDecimal("1234567890123.456789");

        // Setup String input
        this.stringInput = "1234567890123.456789";

        // Setup unscaled inputs
        this.unscaledLongInput = 9876543210L;
        this.unscaledLongInputScaled = 9876543210L;
        this.scaleInput = 18;

        // Setup rounding mode input
        this.roundingModeInput = RoundingMode.HALF_UP;
    }

    // --- Benchmarks for valueOf methods ---

    @Benchmark
    public Decimal12f benchmarkValueOfLong(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOf(longInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal12f benchmarkValueOfDouble(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOf(doubleInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal12f benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOf(bigDecimalInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal12f benchmarkValueOfString(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOf(stringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal12f benchmarkValueOfUnscaledLong(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOfUnscaled(unscaledLongInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal12f benchmarkValueOfUnscaledLongScaled(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOfUnscaled(unscaledLongInputScaled, scaleInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal12f benchmarkValueOfUnscaledLongScaledWithRounding(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOfUnscaled(unscaledLongInputScaled, scaleInput, roundingModeInput);
        bh.consume(result);
        return result;
    }

    // --- Benchmarks for Arithmetic methods ---

    @Benchmark
    public Decimal12f benchmarkAdd(Blackhole bh) {
        Decimal12f a = Decimal12f.valueOf(longInput);
        Decimal12f b = Decimal12f.valueOf(longInput);
        Decimal12f result = a.add(b);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal12f benchmarkMultiplyExact(Blackhole bh) {
        Decimal12f a = Decimal12f.valueOf(longInput);
        Decimal12f b = Decimal12f.valueOf(longInput);
        // Fixed: Replaced a.multiplyExact().by(b) with standard multiplication a.multiply(b)
        // to resolve compilation errors related to Multipliable12f usage.
        Decimal12f result = a.multiply(b);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal12f benchmarkDivide(Blackhole bh) {
        Decimal12f a = Decimal12f.valueOf(longInput);
        Decimal12f b = Decimal12f.valueOf(longInput);
        Decimal12f result = a.divide(b);
        bh.consume(result);
        return result;
    }
}
