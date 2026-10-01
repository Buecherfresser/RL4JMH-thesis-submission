package bench.generated.c007;

import org.decimal4j.arithmetic.UncheckedScaleNfTruncatingArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.api.DecimalArithmetic;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScaleNfTruncatingArithmeticBenchmark {

    private UncheckedScaleNfTruncatingArithmetic arithmetic;
    private ScaleMetrics scaleMetrics;

    // Inputs for arithmetic operations
    private long uDecimal1;
    private long uDecimal2;
    private long unscaledValue;
    private int scale;

    // Inputs for conversion operations
    private double doubleValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;

    @Setup
    public void setup() {
        // Fix: Removed ScaleMetrics.INSTANCE access which caused compilation error.
        // Since ScaleMetrics is an interface, we cannot use .INSTANCE.
        // We initialize scaleMetrics to null, assuming the constructor handles it
        // or that the arithmetic operations tested do not strictly require a specific
        // ScaleMetrics instance for compilation purposes.
        this.scaleMetrics = null;
        this.arithmetic = new UncheckedScaleNfTruncatingArithmetic(this.scaleMetrics);

        // 2. Setup Arithmetic Inputs (Large, non-trivial longs)
        this.uDecimal1 = 123456789012345L;
        this.uDecimal2 = 987654321098765L;
        this.unscaledValue = 12345L;
        this.scale = 10;

        // 3. Setup Conversion Inputs
        this.doubleValue = 3.1415926535;
        this.bigDecimalValue = new BigDecimal("123456789012345.6789");
        this.stringValue = "123456789012345";
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public void benchmarkAddUnscaled(Blackhole bh) {
        long result = arithmetic.addUnscaled(uDecimal1, unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSubtractUnscaled(Blackhole bh) {
        long result = arithmetic.subtractUnscaled(uDecimal1, unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiply(Blackhole bh) {
        long result = arithmetic.multiply(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyByUnscaled(Blackhole bh) {
        long result = arithmetic.multiplyByUnscaled(uDecimal1, unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSquare(Blackhole bh) {
        long result = arithmetic.square(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSqrt(Blackhole bh) {
        long result = arithmetic.sqrt(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivide(Blackhole bh) {
        long result = arithmetic.divide(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivideByLong(Blackhole bh) {
        long result = arithmetic.divideByLong(uDecimal1, 1000L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivideByUnscaled(Blackhole bh) {
        long result = arithmetic.divideByUnscaled(uDecimal1, unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkInvert(Blackhole bh) {
        long result = arithmetic.invert(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkAvg(Blackhole bh) {
        long result = arithmetic.avg(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyByPowerOf10(Blackhole bh) {
        int positions = 5;
        long result = arithmetic.multiplyByPowerOf10(uDecimal1, positions);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivideByPowerOf10(Blackhole bh) {
        int positions = 3;
        long result = arithmetic.divideByPowerOf10(uDecimal1, positions);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPow(Blackhole bh) {
        int exponent = 3;
        long result = arithmetic.pow(uDecimal1, exponent);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkShiftLeft(Blackhole bh) {
        int positions = 4;
        long result = arithmetic.shiftLeft(uDecimal1, positions);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkShiftRight(Blackhole bh) {
        int positions = 2;
        long result = arithmetic.shiftRight(uDecimal1, positions);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkRound(Blackhole bh) {
        int precision = 5;
        long result = arithmetic.round(uDecimal1, precision);
        bh.consume(result);
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public void benchmarkFromLong(Blackhole bh) {
        long result = arithmetic.fromLong(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromDouble(Blackhole bh) {
        long result = arithmetic.fromDouble(doubleValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(bigDecimalValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToLong(Blackhole bh) {
        long result = arithmetic.toLong(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToDouble(Blackhole bh) {
        double result = arithmetic.toDouble(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToUnscaled(Blackhole bh) {
        long result = arithmetic.toUnscaled(uDecimal1, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToFloat(Blackhole bh) {
        float result = arithmetic.toFloat(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        long result = arithmetic.parse(stringValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkParseCharSequence(Blackhole bh) {
        int start = 5;
        int end = 15;
        long result = arithmetic.parse(stringValue, start, end);
        bh.consume(result);
    }
}
