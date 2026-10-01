package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal12f;
import org.decimal4j.immutable.Decimal12f;
import org.decimal4j.factory.Factory12f;
import org.decimal4j.scale.Scale12f;
import org.decimal4j.exact.Multipliable12f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal12fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        MutableDecimal12f mutable;
        long longValue;
        double doubleValue;
        String stringValue;
        BigInteger bigInteger;
        BigDecimal bigDecimal;

        @Setup(Level.Trial)
        public void setUp() {
            mutable = new MutableDecimal12f();
            longValue = 1234567890123L;               // arbitrary value within range
            doubleValue = 12345.678901234;            // 12‑scale compatible
            stringValue = "12345.678901234567";       // more than 12 fraction digits, will be rounded
            bigInteger = new BigInteger("98765432109876543210");
            bigDecimal = new BigDecimal("98765.432109876543210");
        }
    }

    @Benchmark
    public MutableDecimal12f benchmarkConstructorLong(BenchmarkState s) {
        return new MutableDecimal12f(s.longValue);
    }

    @Benchmark
    public MutableDecimal12f benchmarkConstructorDouble(BenchmarkState s) {
        return new MutableDecimal12f(s.doubleValue);
    }

    @Benchmark
    public MutableDecimal12f benchmarkConstructorString(BenchmarkState s) {
        return new MutableDecimal12f(s.stringValue);
    }

    @Benchmark
    public MutableDecimal12f benchmarkConstructorBigInteger(BenchmarkState s) {
        return new MutableDecimal12f(s.bigInteger);
    }

    @Benchmark
    public MutableDecimal12f benchmarkConstructorBigDecimal(BenchmarkState s) {
        return new MutableDecimal12f(s.bigDecimal);
    }

    @Benchmark
    public MutableDecimal12f benchmarkStaticZero() {
        return MutableDecimal12f.zero();
    }

    @Benchmark
    public MutableDecimal12f benchmarkStaticOne() {
        return MutableDecimal12f.one();
    }

    @Benchmark
    public MutableDecimal12f benchmarkStaticTwo() {
        return MutableDecimal12f.two();
    }

    @Benchmark
    public MutableDecimal12f benchmarkClone(BenchmarkState s) {
        return s.mutable.clone();
    }

    @Benchmark
    public Decimal12f benchmarkToImmutable(BenchmarkState s) {
        return s.mutable.toImmutableDecimal();
    }

    @Benchmark
    public Multipliable12f benchmarkMultiplyExact(BenchmarkState s) {
        return s.mutable.multiplyExact();
    }

    @Benchmark
    public int benchmarkGetScale(BenchmarkState s) {
        return s.mutable.getScale();
    }

    @Benchmark
    public Scale12f benchmarkGetScaleMetrics(BenchmarkState s) {
        return s.mutable.getScaleMetrics();
    }

    @Benchmark
    public Factory12f benchmarkGetFactory(BenchmarkState s) {
        return s.mutable.getFactory();
    }

    @Benchmark
    public MutableDecimal12f benchmarkSetLong(BenchmarkState s) {
        return s.mutable.set(s.longValue);
    }

    @Benchmark
    public MutableDecimal12f benchmarkSetDouble(BenchmarkState s) {
        return s.mutable.set(s.doubleValue);
    }

    @Benchmark
    public MutableDecimal12f benchmarkSetString(BenchmarkState s) {
        return s.mutable.set(s.stringValue);
    }

    @Benchmark
    public MutableDecimal12f benchmarkSetBigInteger(BenchmarkState s) {
        return s.mutable.set(s.bigInteger);
    }

    @Benchmark
    public MutableDecimal12f benchmarkSetBigDecimal(BenchmarkState s) {
        return s.mutable.set(s.bigDecimal);
    }

    @Benchmark
    public MutableDecimal12f benchmarkSetZero(BenchmarkState s) {
        return s.mutable.setZero();
    }

    @Benchmark
    public MutableDecimal12f benchmarkSetOne(BenchmarkState s) {
        return s.mutable.setOne();
    }

    @Benchmark
    public MutableDecimal12f benchmarkSetMinusOne(BenchmarkState s) {
        return s.mutable.setMinusOne();
    }

    @Benchmark
    public MutableDecimal12f benchmarkSetUnscaled(BenchmarkState s) {
        // setUnscaled(long, int) uses the provided scale; 12 is the fixed scale for this type
        return s.mutable.setUnscaled(s.longValue, 12);
    }
}
