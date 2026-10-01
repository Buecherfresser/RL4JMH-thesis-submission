package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.arithmetic.UncheckedScale0fRoundingArithmetic;
import java.math.BigDecimal;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScale0fRoundingArithmeticBenchmark {

    private UncheckedScale0fRoundingArithmetic arithmetic;

    // Operands for arithmetic operations
    private long uDecimal;
    private long unscaled;
    private int scale;
    private long divisor;
    private long divisorLong;
    private int positions;
    private int exponent;
    private int precision;
    private BigDecimal bigDecimal;
    private String decimalString;
    private CharSequence decimalCharSeq;

    @Setup(Level.Trial)
    public void setup() {
        // Use a common rounding mode for all operations
        arithmetic = new UncheckedScale0fRoundingArithmetic(RoundingMode.HALF_UP);

        // Sample values – chosen to avoid overflow in the benchmarked operations
        uDecimal = 123_456_789L;
        unscaled = 987_654_321L;
        scale = 2;
        divisor = 12_345L;
        divisorLong = 100L;
        positions = 3;
        exponent = 5;
        precision = 2;
        bigDecimal = new BigDecimal("12345.67");
        decimalString = "12345";
        decimalCharSeq = decimalString;
    }

    @Benchmark
    public long benchmarkAddUnscaled() {
        return arithmetic.addUnscaled(uDecimal, unscaled, scale);
    }

    @Benchmark
    public long benchmarkSubtractUnscaled() {
        return arithmetic.subtractUnscaled(uDecimal, unscaled, scale);
    }

    @Benchmark
    public long benchmarkMultiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(uDecimal, unscaled, scale);
    }

    @Benchmark
    public long benchmarkDivide() {
        return arithmetic.divide(uDecimal, divisor);
    }

    @Benchmark
    public long benchmarkDivideByLong() {
        return arithmetic.divideByLong(uDecimal, divisorLong);
    }

    @Benchmark
    public long benchmarkDivideByUnscaled() {
        return arithmetic.divideByUnscaled(uDecimal, unscaled, scale);
    }

    @Benchmark
    public long benchmarkAvg() {
        return arithmetic.avg(uDecimal, divisor);
    }

    @Benchmark
    public long benchmarkInvert() {
        return arithmetic.invert(uDecimal);
    }

    @Benchmark
    public long benchmarkShiftLeft() {
        return arithmetic.shiftLeft(uDecimal, positions);
    }

    @Benchmark
    public long benchmarkShiftRight() {
        return arithmetic.shiftRight(uDecimal, positions);
    }

    @Benchmark
    public long benchmarkDivideByPowerOf10() {
        return arithmetic.divideByPowerOf10(uDecimal, positions);
    }

    @Benchmark
    public long benchmarkMultiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(uDecimal, positions);
    }

    @Benchmark
    public long benchmarkSqrt() {
        return arithmetic.sqrt(uDecimal);
    }

    @Benchmark
    public long benchmarkPow() {
        return arithmetic.pow(uDecimal, exponent);
    }

    @Benchmark
    public long benchmarkRound() {
        return arithmetic.round(uDecimal, precision);
    }

    @Benchmark
    public long benchmarkToUnscaled() {
        return arithmetic.toUnscaled(uDecimal, scale);
    }

    @Benchmark
    public float benchmarkToFloat() {
        return arithmetic.toFloat(uDecimal);
    }

    @Benchmark
    public double benchmarkToDouble() {
        return arithmetic.toDouble(uDecimal);
    }

    @Benchmark
    public long benchmarkFromUnscaled() {
        return arithmetic.fromUnscaled(unscaled, scale);
    }

    @Benchmark
    public long benchmarkFromFloat() {
        return arithmetic.fromFloat((float) uDecimal);
    }

    @Benchmark
    public long benchmarkFromDouble() {
        return arithmetic.fromDouble((double) uDecimal);
    }

    @Benchmark
    public long benchmarkFromBigDecimal() {
        return arithmetic.fromBigDecimal(bigDecimal);
    }

    @Benchmark
    public long benchmarkParseString() {
        return arithmetic.parse(decimalString);
    }

    @Benchmark
    public long benchmarkParseCharSequence() {
        return arithmetic.parse(decimalCharSeq, 0, decimalCharSeq.length());
    }
}
