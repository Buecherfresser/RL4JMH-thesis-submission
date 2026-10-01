package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.arithmetic.CheckedScaleNfTruncatingArithmetic;
import org.decimal4j.scale.Scale5f;
import java.math.BigDecimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScaleNfTruncatingArithmeticBenchmark {

    private CheckedScaleNfTruncatingArithmetic arithmetic;
    private long uDecimalA;
    private long uDecimalB;
    private long unscaledOperand;
    private int operandScale;
    private int exponent;
    private int shiftPositions;
    private int roundPrecision;
    private float floatValue;
    private double doubleValue;
    private BigDecimal bigDecimalValue;
    private String decimalString;

    @Setup(Level.Trial)
    public void setup() {
        // Use scale 5 (factor 100000) for the arithmetic instance
        arithmetic = new CheckedScaleNfTruncatingArithmetic(Scale5f.INSTANCE);
        // Values chosen to stay well within range of long after scaling
        uDecimalA = arithmetic.fromLong(12345L);      // 12345 * scaleFactor
        uDecimalB = arithmetic.fromLong(6789L);       // 6789 * scaleFactor
        unscaledOperand = 55555L;                     // raw unscaled operand
        operandScale = 3;                             // different scale for operand
        exponent = 3;                                 // small exponent
        shiftPositions = 2;                           // shift by 2 positions
        roundPrecision = 4;                           // round to 4 decimal places
        floatValue = 123.456f;
        doubleValue = 789.0123;
        bigDecimalValue = new BigDecimal("456.78901");
        decimalString = "12345.6789";
    }

    @Benchmark
    public long addUnscaled() {
        return arithmetic.addUnscaled(uDecimalA, unscaledOperand, operandScale);
    }

    @Benchmark
    public long subtractUnscaled() {
        return arithmetic.subtractUnscaled(uDecimalA, unscaledOperand, operandScale);
    }

    @Benchmark
    public long multiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(uDecimalA, unscaledOperand, operandScale);
    }

    @Benchmark
    public long divideByUnscaled() {
        return arithmetic.divideByUnscaled(uDecimalA, unscaledOperand, operandScale);
    }

    @Benchmark
    public long multiply() {
        return arithmetic.multiply(uDecimalA, uDecimalB);
    }

    @Benchmark
    public long square() {
        return arithmetic.square(uDecimalA);
    }

    @Benchmark
    public long divide() {
        return arithmetic.divide(uDecimalA, uDecimalB);
    }

    @Benchmark
    public long pow() {
        return arithmetic.pow(uDecimalA, exponent);
    }

    @Benchmark
    public long avg() {
        return arithmetic.avg(uDecimalA, uDecimalB);
    }

    @Benchmark
    public long sqrt() {
        return arithmetic.sqrt(uDecimalA);
    }

    @Benchmark
    public long divideByLong() {
        return arithmetic.divideByLong(uDecimalA, 7L);
    }

    @Benchmark
    public long divideByPowerOf10() {
        return arithmetic.divideByPowerOf10(uDecimalA, shiftPositions);
    }

    @Benchmark
    public long invert() {
        return arithmetic.invert(uDecimalA);
    }

    @Benchmark
    public long multiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(uDecimalA, shiftPositions);
    }

    @Benchmark
    public long shiftLeft() {
        return arithmetic.shiftLeft(uDecimalA, shiftPositions);
    }

    @Benchmark
    public long shiftRight() {
        return arithmetic.shiftRight(uDecimalA, shiftPositions);
    }

    @Benchmark
    public long round() {
        return arithmetic.round(uDecimalA, roundPrecision);
    }

    @Benchmark
    public long fromLong() {
        return arithmetic.fromLong(98765L);
    }

    @Benchmark
    public long fromFloat() {
        return arithmetic.fromFloat(floatValue);
    }

    @Benchmark
    public long fromDouble() {
        return arithmetic.fromDouble(doubleValue);
    }

    @Benchmark
    public long fromUnscaled() {
        return arithmetic.fromUnscaled(unscaledOperand, operandScale);
    }

    @Benchmark
    public long fromBigDecimal() {
        return arithmetic.fromBigDecimal(bigDecimalValue);
    }

    @Benchmark
    public long toLong() {
        return arithmetic.toLong(uDecimalA);
    }

    @Benchmark
    public float toFloat() {
        return arithmetic.toFloat(uDecimalA);
    }

    @Benchmark
    public double toDouble() {
        return arithmetic.toDouble(uDecimalA);
    }

    @Benchmark
    public long toUnscaled() {
        return arithmetic.toUnscaled(uDecimalA, operandScale);
    }

    @Benchmark
    public long parseString() {
        return arithmetic.parse(decimalString);
    }

    @Benchmark
    public long parseCharSequence(Blackhole bh) {
        long result = arithmetic.parse((CharSequence) decimalString, 0, decimalString.length());
        bh.consume(result);
        return result;
    }
}
