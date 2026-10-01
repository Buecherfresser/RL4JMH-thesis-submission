package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.exact.Multipliable10f;
import org.decimal4j.api.Decimal;
import java.math.BigDecimal;
import java.math.BigInteger;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal10fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        long longValue;
        double doubleValue;
        String stringValue;
        BigDecimal bigDecimalValue;
        BigInteger bigIntegerValue;
        long unscaledValue;
        MutableDecimal10f mutableFromLong;
        MutableDecimal10f mutableFromDouble;
        MutableDecimal10f mutableFromString;
        MutableDecimal10f mutableFromBigDecimal;
        MutableDecimal10f mutableFromBigInteger;

        @Setup(Level.Trial)
        public void setUp() {
            longValue = 123456789L;
            doubleValue = 12345.6789012345;
            stringValue = "12345.6789012345";
            bigDecimalValue = new BigDecimal("12345.6789012345");
            bigIntegerValue = new BigInteger("12345678901234567890");
            unscaledValue = 1234567890123L;
            mutableFromLong = new MutableDecimal10f(longValue);
            mutableFromDouble = new MutableDecimal10f(doubleValue);
            mutableFromString = new MutableDecimal10f(stringValue);
            mutableFromBigDecimal = new MutableDecimal10f(bigDecimalValue);
            mutableFromBigInteger = new MutableDecimal10f(bigIntegerValue);
        }
    }

    @Benchmark
    public MutableDecimal10f benchmarkZeroFactory(BenchmarkState s) {
        return MutableDecimal10f.zero();
    }

    @Benchmark
    public MutableDecimal10f benchmarkOneFactory(BenchmarkState s) {
        return MutableDecimal10f.one();
    }

    @Benchmark
    public MutableDecimal10f benchmarkUnscaledFactory(BenchmarkState s) {
        return MutableDecimal10f.unscaled(s.unscaledValue);
    }

    @Benchmark
    public MutableDecimal10f benchmarkFromLong(BenchmarkState s) {
        return new MutableDecimal10f(s.longValue);
    }

    @Benchmark
    public MutableDecimal10f benchmarkFromDouble(BenchmarkState s) {
        return new MutableDecimal10f(s.doubleValue);
    }

    @Benchmark
    public MutableDecimal10f benchmarkFromString(BenchmarkState s) {
        return new MutableDecimal10f(s.stringValue);
    }

    @Benchmark
    public MutableDecimal10f benchmarkFromBigDecimal(BenchmarkState s) {
        return new MutableDecimal10f(s.bigDecimalValue);
    }

    @Benchmark
    public MutableDecimal10f benchmarkFromBigInteger(BenchmarkState s) {
        return new MutableDecimal10f(s.bigIntegerValue);
    }

    @Benchmark
    public MutableDecimal10f benchmarkClone(BenchmarkState s) {
        return s.mutableFromLong.clone();
    }

    @Benchmark
    public Decimal10f benchmarkToImmutable(BenchmarkState s) {
        return s.mutableFromLong.toImmutableDecimal();
    }

    @Benchmark
    public Multipliable10f benchmarkMultiplyExact(BenchmarkState s) {
        return s.mutableFromLong.multiplyExact();
    }

    @Benchmark
    public MutableDecimal10f benchmarkAdd(BenchmarkState s) {
        return s.mutableFromLong.add(s.mutableFromLong);
    }

    @Benchmark
    public MutableDecimal10f benchmarkSubtract(BenchmarkState s) {
        return s.mutableFromLong.subtract(s.mutableFromLong);
    }

    @Benchmark
    public MutableDecimal10f benchmarkMultiply(BenchmarkState s) {
        return s.mutableFromLong.multiply(s.mutableFromLong);
    }

    @Benchmark
    public MutableDecimal10f benchmarkDivide(BenchmarkState s) {
        return s.mutableFromLong.divide(s.mutableFromLong);
    }

    @Benchmark
    public MutableDecimal10f benchmarkNegate(BenchmarkState s) {
        return s.mutableFromLong.negate();
    }

    @Benchmark
    public MutableDecimal10f benchmarkAbs(BenchmarkState s) {
        return s.mutableFromLong.abs();
    }

    @Benchmark
    public MutableDecimal10f benchmarkSetLong(BenchmarkState s) {
        MutableDecimal10f m = new MutableDecimal10f();
        return m.set(s.longValue);
    }

    @Benchmark
    public MutableDecimal10f benchmarkSetDouble(BenchmarkState s) {
        MutableDecimal10f m = new MutableDecimal10f();
        return m.set(s.doubleValue);
    }

    @Benchmark
    public MutableDecimal10f benchmarkSetString(BenchmarkState s) {
        MutableDecimal10f m = new MutableDecimal10f();
        return m.set(s.stringValue);
    }

    @Benchmark
    public MutableDecimal10f benchmarkSetBigDecimal(BenchmarkState s) {
        MutableDecimal10f m = new MutableDecimal10f();
        return m.set(s.bigDecimalValue);
    }

    @Benchmark
    public MutableDecimal10f benchmarkSetBigInteger(BenchmarkState s) {
        MutableDecimal10f m = new MutableDecimal10f();
        return m.set(s.bigIntegerValue);
    }
}
