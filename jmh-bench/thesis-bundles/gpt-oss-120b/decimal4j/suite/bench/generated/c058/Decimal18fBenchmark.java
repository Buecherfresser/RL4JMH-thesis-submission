package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal18f;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.factory.Factory18f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal18fBenchmark {

    private Decimal18f decOne;
    private Decimal18f decTwo;
    private Decimal18f decLarge;
    private Decimal18f decFromString;
    private Decimal18f decFromBigDecimal;
    private Decimal18f decFromBigInteger;
    private MutableDecimal18f mutable;

    @Setup(Level.Trial)
    public void setUp() {
        decOne = Decimal18f.ONE;
        decTwo = Decimal18f.TWO;
        decLarge = Decimal18f.valueOf(123456789L);
        decFromString = new Decimal18f("12345.678901234567890");
        decFromBigDecimal = Decimal18f.valueOf(new BigDecimal("98765.432109876543210"));
        decFromBigInteger = Decimal18f.valueOf(BigInteger.valueOf(1234567890123456789L));
        mutable = new MutableDecimal18f(decOne);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfLong() {
        return Decimal18f.valueOf(12345L);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfLongNegative() {
        return Decimal18f.valueOf(-12345L);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfFloat() {
        return Decimal18f.valueOf(12345.67f);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfFloatRounding() {
        return Decimal18f.valueOf(12345.67f, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfDouble() {
        return Decimal18f.valueOf(12345.678901234567);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfDoubleRounding() {
        return Decimal18f.valueOf(12345.678901234567, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfBigInteger() {
        return Decimal18f.valueOf(BigInteger.valueOf(1234567890123456789L));
    }

    @Benchmark
    public Decimal18f benchmarkValueOfBigDecimal() {
        return Decimal18f.valueOf(new BigDecimal("12345.678901234567890"));
    }

    @Benchmark
    public Decimal18f benchmarkValueOfString() {
        return Decimal18f.valueOf("12345.678901234567890");
    }

    @Benchmark
    public Decimal18f benchmarkValueOfStringRounding() {
        return Decimal18f.valueOf("1.1234567890123456789", RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfUnscaled() {
        return Decimal18f.valueOfUnscaled(123456789L);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfUnscaledWithScale() {
        return Decimal18f.valueOfUnscaled(123456789L, 10);
    }

    @Benchmark
    public Decimal18f benchmarkAdd() {
        return decOne.add(decTwo);
    }

    @Benchmark
    public Decimal18f benchmarkSubtract() {
        return decTwo.subtract(decOne);
    }

    @Benchmark
    public Decimal18f benchmarkMultiply() {
        return decOne.multiply(decTwo);
    }

    @Benchmark
    public Decimal18f benchmarkDivide() {
        return decTwo.divide(decOne);
    }

    @Benchmark
    public Scale18f benchmarkGetScaleMetrics() {
        return decOne.getScaleMetrics();
    }

    @Benchmark
    public Factory18f benchmarkGetFactory() {
        return decOne.getFactory();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return decOne.getScale();
    }

    @Benchmark
    public MutableDecimal18f benchmarkToMutableDecimal() {
        return decOne.toMutableDecimal();
    }

    @Benchmark
    public Decimal18f benchmarkToImmutableDecimal() {
        return mutable.toImmutableDecimal();
    }
}
