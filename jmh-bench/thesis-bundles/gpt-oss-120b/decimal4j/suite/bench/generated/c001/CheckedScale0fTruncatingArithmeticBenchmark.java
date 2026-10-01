package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.arithmetic.CheckedScale0fTruncatingArithmetic;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.truncate.CheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScale0fTruncatingArithmeticBenchmark {

    private CheckedScale0fTruncatingArithmetic arith;

    // Operands for binary operations
    private long uDecimal;
    private long unscaled;
    private int scale;

    private long dividend;
    private long divisor;

    private long a;
    private long b;

    private long base;
    private int exponent;

    private int power;
    private int shiftPositions;
    private int roundPrecision;

    private float floatVal;
    private double doubleVal;
    private BigDecimal bigDecimalVal;

    private String parseString;

    @Setup(Level.Trial)
    public void setup() {
        arith = CheckedScale0fTruncatingArithmetic.INSTANCE;

        uDecimal = 123456789L;
        unscaled = 987654321L;
        scale = 0;

        dividend = 987654321L;
        divisor = 12345L;

        a = 111111111L;
        b = 222222222L;

        base = 7L;
        exponent = 5;

        power = 3;
        shiftPositions = 2;
        roundPrecision = 0;

        floatVal = 12345.67f;
        doubleVal = 12345678.9;
        bigDecimalVal = new BigDecimal("123456789");

        parseString = "987654321";
    }

    @Benchmark
    public RoundingMode benchGetRoundingMode() {
        return arith.getRoundingMode();
    }

    @Benchmark
    public CheckedRounding benchGetTruncationPolicy() {
        return arith.getTruncationPolicy();
    }

    @Benchmark
    public long benchAddUnscaled() {
        return arith.addUnscaled(uDecimal, unscaled, scale);
    }

    @Benchmark
    public long benchSubtractUnscaled() {
        return arith.subtractUnscaled(uDecimal, unscaled, scale);
    }

    @Benchmark
    public long benchMultiplyByUnscaled() {
        return arith.multiplyByUnscaled(uDecimal, unscaled, scale);
    }

    @Benchmark
    public long benchDivideByUnscaled() {
        return arith.divideByUnscaled(uDecimal, unscaled, scale);
    }

    @Benchmark
    public long benchDivide() {
        return arith.divide(dividend, divisor);
    }

    @Benchmark
    public long benchDivideByLong() {
        return arith.divideByLong(dividend, divisor);
    }

    @Benchmark
    public long benchAvg() {
        return arith.avg(a, b);
    }

    @Benchmark
    public long benchInvert() {
        return arith.invert(uDecimal);
    }

    @Benchmark
    public long benchPow() {
        return arith.pow(base, exponent);
    }

    @Benchmark
    public long benchSqrt() {
        return arith.sqrt(uDecimal);
    }

    @Benchmark
    public long benchDivideByPowerOf10() {
        return arith.divideByPowerOf10(uDecimal, power);
    }

    @Benchmark
    public long benchMultiplyByPowerOf10() {
        return arith.multiplyByPowerOf10(uDecimal, power);
    }

    @Benchmark
    public long benchShiftLeft() {
        return arith.shiftLeft(uDecimal, shiftPositions);
    }

    @Benchmark
    public long benchShiftRight() {
        return arith.shiftRight(uDecimal, shiftPositions);
    }

    @Benchmark
    public long benchRound() {
        return arith.round(uDecimal, roundPrecision);
    }

    @Benchmark
    public float benchToFloat() {
        return arith.toFloat(uDecimal);
    }

    @Benchmark
    public double benchToDouble() {
        return arith.toDouble(uDecimal);
    }

    @Benchmark
    public long benchToUnscaled() {
        return arith.toUnscaled(uDecimal, scale);
    }

    @Benchmark
    public long benchFromFloat() {
        return arith.fromFloat(floatVal);
    }

    @Benchmark
    public long benchFromDouble() {
        return arith.fromDouble(doubleVal);
    }

    @Benchmark
    public long benchFromUnscaled() {
        return arith.fromUnscaled(unscaled, scale);
    }

    @Benchmark
    public long benchFromBigDecimal() {
        return arith.fromBigDecimal(bigDecimalVal);
    }

    @Benchmark
    public long benchParseString() {
        return arith.parse(parseString);
    }

    @Benchmark
    public long benchParseCharSequence() {
        return arith.parse((CharSequence) parseString, 0, parseString.length());
    }
}
