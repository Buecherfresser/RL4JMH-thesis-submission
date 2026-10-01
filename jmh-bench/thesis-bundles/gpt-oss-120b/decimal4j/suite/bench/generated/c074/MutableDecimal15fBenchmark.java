package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.mutable.MutableDecimal15f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.factory.Factory15f;
import org.decimal4j.scale.Scale15f;
import org.decimal4j.exact.Multipliable15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal15fBenchmark {

    private MutableDecimal15f mutable;
    private long longValue;
    private long unscaledValue;
    private String stringValue;
    private BigDecimal bigDecimal;
    private BigInteger bigInteger;

    @Setup(Level.Trial)
    public void setUp() {
        mutable = new MutableDecimal15f(Decimal15f.ONE);
        longValue = 123456789L;
        unscaledValue = 123456789012345L;
        stringValue = "12345.678901234567";
        bigDecimal = new BigDecimal("12345.678901234567");
        bigInteger = new BigInteger("12345678901234567890");
    }

    @Benchmark
    public MutableDecimal15f benchmarkConstructorLong() {
        return new MutableDecimal15f(longValue);
    }

    @Benchmark
    public MutableDecimal15f benchmarkConstructorString() {
        return new MutableDecimal15f(stringValue);
    }

    @Benchmark
    public MutableDecimal15f benchmarkConstructorBigDecimal() {
        return new MutableDecimal15f(bigDecimal);
    }

    @Benchmark
    public MutableDecimal15f benchmarkConstructorBigInteger() {
        return new MutableDecimal15f(bigInteger);
    }

    @Benchmark
    public MutableDecimal15f benchmarkSetLong() {
        mutable.set(longValue);
        return mutable;
    }

    @Benchmark
    public MutableDecimal15f benchmarkClone() {
        return mutable.clone();
    }

    @Benchmark
    public Decimal15f benchmarkToImmutable() {
        return mutable.toImmutableDecimal();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return mutable.getScale();
    }

    @Benchmark
    public Factory15f benchmarkGetFactory() {
        return mutable.getFactory();
    }

    @Benchmark
    public Scale15f benchmarkGetScaleMetrics() {
        return mutable.getScaleMetrics();
    }

    @Benchmark
    public MutableDecimal15f benchmarkStaticZero() {
        return MutableDecimal15f.zero();
    }

    @Benchmark
    public MutableDecimal15f benchmarkStaticOne() {
        return MutableDecimal15f.one();
    }

    @Benchmark
    public MutableDecimal15f benchmarkStaticFive() {
        return MutableDecimal15f.five();
    }

    @Benchmark
    public MutableDecimal15f benchmarkUnscaled() {
        return MutableDecimal15f.unscaled(unscaledValue);
    }

    @Benchmark
    public Multipliable15f benchmarkMultiplyExact() {
        return mutable.multiplyExact();
    }

    @Benchmark
    public void benchmarkConsumeToString(Blackhole bh) {
        bh.consume(mutable.toString());
    }
}
