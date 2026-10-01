package bench.generated.c005;

import org.decimal4j.arithmetic.UncheckedScale0fTruncatingArithmetic;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScale0fTruncatingArithmeticBenchmark {

    private UncheckedScale0fTruncatingArithmetic arithmetic;

    // --- State for Arithmetic Operations ---
    private long uDecimalA;
    private long uDecimalB;
    private long unscaledA;
    private long unscaledB;
    private int scale;

    // --- State for Conversions ---
    private long longInput;
    private double doubleInput;
    private float floatInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;

    @Setup
    public void setup() {
        arithmetic = UncheckedScale0fTruncatingArithmetic.INSTANCE;

        // Setup inputs for arithmetic operations (using large, non-trivial longs)
        uDecimalA = 123456789012345L;
        uDecimalB = 987654321098765L;
        unscaledA = 1000000000000000L;
        unscaledB = 500000000000000L;
        scale = 10;

        // Setup inputs for conversion operations
        longInput = 123456789012345L;
        doubleInput = 123456789012345.6789;
        floatInput = 123456789012345.123f;
        bigDecimalInput = new BigDecimal("123456789012345.6789");
        stringInput = "123456789012345";
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public void benchmarkAddUnscaled(Blackhole bh) {
        long result = arithmetic.addUnscaled(uDecimalA, unscaledA, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSubtractUnscaled(Blackhole bh) {
        long result = arithmetic.subtractUnscaled(uDecimalA, unscaledA, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyByUnscaled(Blackhole bh) {
        long result = arithmetic.multiplyByUnscaled(uDecimalA, unscaledA, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivide(Blackhole bh) {
        long result = arithmetic.divide(uDecimalA, uDecimalB);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivideByLong(Blackhole bh) {
        long result = arithmetic.divideByLong(uDecimalA, 1000L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivideByUnscaled(Blackhole bh) {
        long result = arithmetic.divideByUnscaled(uDecimalA, unscaledA, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyByPowerOf10(Blackhole bh) {
        int positions = 5;
        long result = arithmetic.multiplyByPowerOf10(uDecimalA, positions);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivideByPowerOf10(Blackhole bh) {
        int positions = 3;
        long result = arithmetic.divideByPowerOf10(uDecimalA, positions);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkInvert(Blackhole bh) {
        long result = arithmetic.invert(uDecimalA);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSqrt(Blackhole bh) {
        long result = arithmetic.sqrt(uDecimalA);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPow(Blackhole bh) {
        int exponent = 3;
        long result = arithmetic.pow(uDecimalA, exponent);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkShiftLeft(Blackhole bh) {
        int positions = 4;
        long result = arithmetic.shiftLeft(uDecimalA, positions);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkShiftRight(Blackhole bh) {
        int positions = 2;
        long result = arithmetic.shiftRight(uDecimalA, positions);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkAvg(Blackhole bh) {
        long result = arithmetic.avg(uDecimalA, uDecimalB);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkRound(Blackhole bh) {
        int precision = 5;
        long result = arithmetic.round(uDecimalA, precision);
        bh.consume(result);
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public void benchmarkToDouble(Blackhole bh) {
        double result = arithmetic.toDouble(uDecimalA);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToFloat(Blackhole bh) {
        float result = arithmetic.toFloat(uDecimalA);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromDouble(Blackhole bh) {
        long result = arithmetic.fromDouble(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromFloat(Blackhole bh) {
        long result = arithmetic.fromFloat(floatInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(bigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        long result = arithmetic.parse(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkParseCharSequence(Blackhole bh) {
        // Benchmarking a substring parse operation
        String sub = stringInput.substring(5, 15);
        long result = arithmetic.parse(sub);
        bh.consume(result);
    }
}
