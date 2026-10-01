package bench.generated.c072;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal13f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.factory.Factory13f;
import org.decimal4j.exact.Multipliable13f;
import org.decimal4j.scale.Scale13f;
import java.math.BigDecimal;
import java.math.BigInteger;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal13fBenchmark {

    private long longValue;
    private double doubleValue;
    private BigDecimal bigDecimalValue;
    private BigInteger bigIntegerValue;
    private String stringValue;
    private Decimal13f decimal13fValue;
    private MutableDecimal13f mutableInstance;

    @Setup(Level.Trial)
    public void setUp() {
        longValue = 1234567890123L;
        doubleValue = 12345.67890123456;
        bigDecimalValue = new BigDecimal("1234567890.1234567890123");
        bigIntegerValue = new BigInteger("12345678901234567890");
        stringValue = "1234567.8901234567890";
        decimal13fValue = Decimal13f.valueOf(987654321L);
        mutableInstance = new MutableDecimal13f(5555555555555L);
    }

    @Benchmark
    public MutableDecimal13f benchmarkConstructorLong() {
        return new MutableDecimal13f(longValue);
    }

    @Benchmark
    public MutableDecimal13f benchmarkConstructorDouble() {
        return new MutableDecimal13f(doubleValue);
    }

    @Benchmark
    public MutableDecimal13f benchmarkConstructorBigDecimal() {
        return new MutableDecimal13f(bigDecimalValue);
    }

    @Benchmark
    public MutableDecimal13f benchmarkConstructorBigInteger() {
        return new MutableDecimal13f(bigIntegerValue);
    }

    @Benchmark
    public MutableDecimal13f benchmarkConstructorString() {
        return new MutableDecimal13f(stringValue);
    }

    @Benchmark
    public MutableDecimal13f benchmarkConstructorDecimal13f() {
        return new MutableDecimal13f(decimal13fValue);
    }

    @Benchmark
    public MutableDecimal13f benchmarkStaticZero() {
        return MutableDecimal13f.zero();
    }

    @Benchmark
    public MutableDecimal13f benchmarkStaticOne() {
        return MutableDecimal13f.one();
    }

    @Benchmark
    public MutableDecimal13f benchmarkStaticTwo() {
        return MutableDecimal13f.two();
    }

    @Benchmark
    public MutableDecimal13f benchmarkStaticUnscaled() {
        return MutableDecimal13f.unscaled(longValue);
    }

    @Benchmark
    public MutableDecimal13f benchmarkClone() {
        return mutableInstance.clone();
    }

    @Benchmark
    public Decimal13f benchmarkToImmutable() {
        return mutableInstance.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal13f benchmarkToMutable() {
        return mutableInstance.toMutableDecimal();
    }

    @Benchmark
    public Multipliable13f benchmarkMultiplyExact() {
        return mutableInstance.multiplyExact();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return mutableInstance.getScale();
    }

    @Benchmark
    public Scale13f benchmarkGetScaleMetrics() {
        return mutableInstance.getScaleMetrics();
    }

    @Benchmark
    public Factory13f benchmarkGetFactory() {
        return mutableInstance.getFactory();
    }
}
