package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.mutable.MutableDecimal15f;
import org.decimal4j.exact.Multipliable15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal15fBenchmark {

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private long unscaledValue;
    private int unscaledScale;
    private Decimal15f instanceA;
    private Decimal15f instanceB;

    @Setup(Level.Trial)
    public void setup() {
        longValue = 123456789L;
        floatValue = 12345.678f;
        doubleValue = 12345.6789012345;
        bigIntegerValue = new BigInteger("123456789012345678901234567890");
        bigDecimalValue = new BigDecimal("12345.67890123456789012345");
        stringValue = "12345.678901234567890";
        unscaledValue = 123456789012345L;
        unscaledScale = 10;
        instanceA = Decimal15f.valueOf(12345.6789);
        instanceB = Decimal15f.valueOf(9876.54321);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfLong() {
        return Decimal15f.valueOf(longValue);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfFloat() {
        return Decimal15f.valueOf(floatValue);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfFloatRounding() {
        return Decimal15f.valueOf(floatValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfDouble() {
        return Decimal15f.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfDoubleRounding() {
        return Decimal15f.valueOf(doubleValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfBigInteger() {
        return Decimal15f.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfBigDecimal() {
        return Decimal15f.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfBigDecimalRounding() {
        return Decimal15f.valueOf(bigDecimalValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfString() {
        return Decimal15f.valueOf(stringValue);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfStringRounding() {
        return Decimal15f.valueOf(stringValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfUnscaled() {
        return Decimal15f.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfUnscaledWithScale() {
        return Decimal15f.valueOfUnscaled(unscaledValue, unscaledScale);
    }

    @Benchmark
    public Decimal15f benchmarkValueOfUnscaledWithScaleRounding() {
        return Decimal15f.valueOfUnscaled(unscaledValue, unscaledScale, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Multipliable15f benchmarkMultiplyExact() {
        return instanceA.multiplyExact();
    }

    @Benchmark
    public MutableDecimal15f benchmarkToMutableDecimal() {
        return instanceA.toMutableDecimal();
    }

    @Benchmark
    public Decimal15f benchmarkAdd() {
        return instanceA.add(instanceB);
    }

    @Benchmark
    public Decimal15f benchmarkSubtract() {
        return instanceA.subtract(instanceB);
    }

    @Benchmark
    public Decimal15f benchmarkMultiply() {
        return instanceA.multiply(instanceB);
    }

    @Benchmark
    public Decimal15f benchmarkDivide() {
        return instanceA.divide(instanceB);
    }
}
