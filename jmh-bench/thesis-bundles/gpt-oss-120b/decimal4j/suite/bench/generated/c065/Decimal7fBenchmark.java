package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal7f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.exact.Multipliable7f;
import org.decimal4j.factory.Factory7f;
import org.decimal4j.scale.Scale7f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal7fBenchmark {

    // Primitive and object inputs
    private long longVal;
    private long longValSmall;
    private float floatVal;
    private double doubleVal;
    private BigInteger bigIntVal;
    private BigDecimal bigDecVal;
    private String stringVal;
    private long unscaledVal;
    private int unscaledScale;

    // Instances for instance method benchmarks
    private Decimal7f decimalInstance;
    private Decimal7f otherDecimal;

    @Setup(Level.Trial)
    public void setUp() {
        longVal = 123456789L;
        longValSmall = 5L;
        floatVal = 12345.678f;
        doubleVal = 1234567.8901234;
        bigIntVal = new BigInteger("12345678901234567890");
        bigDecVal = new BigDecimal("1234567890.1234567");
        stringVal = "1234567.8901234";
        unscaledVal = 987654321L;
        unscaledScale = 5;

        decimalInstance = Decimal7f.valueOf(12345L);
        otherDecimal = Decimal7f.valueOf(6789L);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfLong() {
        return Decimal7f.valueOf(longVal);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfLongSmall() {
        return Decimal7f.valueOf(longValSmall);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfFloat() {
        return Decimal7f.valueOf(floatVal);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfFloatRounding() {
        return Decimal7f.valueOf(floatVal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfDouble() {
        return Decimal7f.valueOf(doubleVal);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfDoubleRounding() {
        return Decimal7f.valueOf(doubleVal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfBigInteger() {
        return Decimal7f.valueOf(bigIntVal);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfBigDecimal() {
        return Decimal7f.valueOf(bigDecVal);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfBigDecimalRounding() {
        return Decimal7f.valueOf(bigDecVal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfString() {
        return Decimal7f.valueOf(stringVal);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfStringRounding() {
        return Decimal7f.valueOf(stringVal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfUnscaled() {
        return Decimal7f.valueOfUnscaled(unscaledVal);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfUnscaledWithScale() {
        return Decimal7f.valueOfUnscaled(unscaledVal, unscaledScale);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfUnscaledWithRounding() {
        return Decimal7f.valueOfUnscaled(unscaledVal, unscaledScale, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Multipliable7f benchmarkMultiplyExact() {
        return decimalInstance.multiplyExact();
    }

    @Benchmark
    public MutableDecimal7f benchmarkToMutableDecimal() {
        return decimalInstance.toMutableDecimal();
    }

    @Benchmark
    public Decimal7f benchmarkToImmutableDecimal() {
        return decimalInstance.toImmutableDecimal();
    }

    @Benchmark
    public Scale7f benchmarkGetScaleMetrics() {
        return decimalInstance.getScaleMetrics();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return decimalInstance.getScale();
    }

    @Benchmark
    public Factory7f benchmarkGetFactory() {
        return decimalInstance.getFactory();
    }

    @Benchmark
    public Decimal7f benchmarkAdd() {
        return decimalInstance.add(otherDecimal);
    }

    @Benchmark
    public Decimal7f benchmarkSubtract() {
        return decimalInstance.subtract(otherDecimal);
    }

    @Benchmark
    public Decimal7f benchmarkMultiply() {
        return decimalInstance.multiply(otherDecimal);
    }

    @Benchmark
    public Decimal7f benchmarkDivide() {
        return decimalInstance.divide(otherDecimal);
    }

    @Benchmark
    public Decimal7f benchmarkRemainder() {
        return decimalInstance.remainder(otherDecimal);
    }

    @Benchmark
    public Decimal7f benchmarkNegate() {
        return decimalInstance.negate();
    }

    @Benchmark
    public Decimal7f benchmarkAbs() {
        return decimalInstance.abs();
    }

    @Benchmark
    public Decimal7f benchmarkInvert() {
        return decimalInstance.invert();
    }

    @Benchmark
    public Decimal7f benchmarkSquare() {
        return decimalInstance.square();
    }

    @Benchmark
    public Decimal7f benchmarkSqrt() {
        return decimalInstance.sqrt();
    }

    @Benchmark
    public Decimal7f benchmarkPow() {
        return decimalInstance.pow(3);
    }

    @Benchmark
    public Decimal7f benchmarkAvg() {
        return decimalInstance.avg(otherDecimal);
    }

    @Benchmark
    public Decimal7f benchmarkShiftLeft() {
        return decimalInstance.shiftLeft(2);
    }

    @Benchmark
    public Decimal7f benchmarkShiftRight() {
        return decimalInstance.shiftRight(2);
    }

    @Benchmark
    public Decimal7f benchmarkRound() {
        return decimalInstance.round(2);
    }

    @Benchmark
    public Decimal7f benchmarkRoundWithRoundingMode() {
        return decimalInstance.round(2, RoundingMode.HALF_UP);
    }
}
