package bench.generated.c082;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable5f;
import org.decimal4j.factory.Factory5f;
import org.decimal4j.scale.Scale5f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal5fBenchmark {

    // Constants used across benchmarks
    private static final long UNSCALED_SAMPLE = 123456L;
    private static final double DOUBLE_SAMPLE = 12345.6789;
    private static final String STRING_SAMPLE = "12345.67890";

    @Benchmark
    public MutableDecimal5f benchmarkZeroFactory() {
        return MutableDecimal5f.zero();
    }

    @Benchmark
    public MutableDecimal5f benchmarkOneFactory() {
        return MutableDecimal5f.one();
    }

    @Benchmark
    public MutableDecimal5f benchmarkUnscaledFactory() {
        return MutableDecimal5f.unscaled(UNSCALED_SAMPLE);
    }

    @Benchmark
    public MutableDecimal5f benchmarkConstructorLong() {
        return new MutableDecimal5f(UNSCALED_SAMPLE);
    }

    @Benchmark
    public MutableDecimal5f benchmarkConstructorDouble() {
        return new MutableDecimal5f(DOUBLE_SAMPLE);
    }

    @Benchmark
    public MutableDecimal5f benchmarkConstructorString() {
        return new MutableDecimal5f(STRING_SAMPLE);
    }

    @Benchmark
    public MutableDecimal5f benchmarkAdd() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.ONE);
        MutableDecimal5f b = new MutableDecimal5f(Decimal5f.TWO);
        return a.add(b);
    }

    @Benchmark
    public MutableDecimal5f benchmarkSubtract() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.FIVE);
        MutableDecimal5f b = new MutableDecimal5f(Decimal5f.TWO);
        return a.subtract(b);
    }

    @Benchmark
    public MutableDecimal5f benchmarkMultiply() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.THREE);
        MutableDecimal5f b = new MutableDecimal5f(Decimal5f.FOUR);
        return a.multiply(b);
    }

    @Benchmark
    public MutableDecimal5f benchmarkDivide() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.EIGHT);
        MutableDecimal5f b = new MutableDecimal5f(Decimal5f.TWO);
        return a.divide(b);
    }

    @Benchmark
    public MutableDecimal5f benchmarkNegate() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.ONE);
        return a.negate();
    }

    @Benchmark
    public MutableDecimal5f benchmarkAbs() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.MINUS_ONE);
        return a.abs();
    }

    @Benchmark
    public MutableDecimal5f benchmarkSquare() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.THREE);
        return a.square();
    }

    @Benchmark
    public MutableDecimal5f benchmarkSqrt() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.FOUR);
        return a.sqrt();
    }

    @Benchmark
    public MutableDecimal5f benchmarkPow() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.TWO);
        return a.pow(3);
    }

    @Benchmark
    public Multipliable5f benchmarkMultiplyExact() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.TWO);
        return a.multiplyExact();
    }

    @Benchmark
    public Decimal5f benchmarkToImmutable() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.FIVE);
        return a.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal5f benchmarkClone() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.SIX);
        return a.clone();
    }

    @Benchmark
    public MutableDecimal5f benchmarkSetLong() {
        MutableDecimal5f a = new MutableDecimal5f();
        a.set(UNSCALED_SAMPLE);
        return a;
    }

    @Benchmark
    public MutableDecimal5f benchmarkSetDouble() {
        MutableDecimal5f a = new MutableDecimal5f();
        a.set(DOUBLE_SAMPLE);
        return a;
    }

    @Benchmark
    public MutableDecimal5f benchmarkSetString() {
        MutableDecimal5f a = new MutableDecimal5f();
        a.set(STRING_SAMPLE);
        return a;
    }

    @Benchmark
    public int benchmarkGetScale() {
        MutableDecimal5f a = new MutableDecimal5f();
        return a.getScale();
    }

    @Benchmark
    public Scale5f benchmarkGetScaleMetrics() {
        MutableDecimal5f a = new MutableDecimal5f();
        return a.getScaleMetrics();
    }

    @Benchmark
    public Factory5f benchmarkGetFactory() {
        MutableDecimal5f a = new MutableDecimal5f();
        return a.getFactory();
    }

    @Benchmark
    public String benchmarkToString() {
        MutableDecimal5f a = new MutableDecimal5f(Decimal5f.TEN);
        return a.toString();
    }
}
