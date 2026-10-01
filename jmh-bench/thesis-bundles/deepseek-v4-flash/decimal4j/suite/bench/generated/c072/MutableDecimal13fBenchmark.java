package bench.generated.c072;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.mutable.MutableDecimal13f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal13fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        MutableDecimal13f[] pool;
        int index;
        MutableDecimal13f readOnly;
        MutableDecimal13f b;
        BigDecimal bigDecimalValue;
        String stringValue;
        double doubleValue;
        long longValue;
        BigInteger bigIntegerValue;
        Decimal13f decimal13fValue;
        Decimal<?> decimalValue;
        Decimal0f decimal0fValue;

        @Setup(Level.Trial)
        public void setup() {
            pool = new MutableDecimal13f[1024];
            for (int i = 0; i < pool.length; i++) {
                pool[i] = MutableDecimal13f.unscaled(1234567890123456789L + i);
            }
            index = 0;
            readOnly = MutableDecimal13f.unscaled(1234567890123456789L);
            b = new MutableDecimal13f("2.5");
            bigDecimalValue = new BigDecimal("1234567890123456789.1234567890123");
            stringValue = "1234567890123456789.1234567890123";
            doubleValue = 1234567890123456789.1234567890123;
            longValue = 1234567890123456789L;
            bigIntegerValue = new BigInteger("1234567890123456789");
            decimal13fValue = Decimal13f.valueOf("1234567890123456789.1234567890123");
            decimalValue = decimal13fValue;
            decimal0fValue = Decimal0f.valueOf(1234567890L);
        }

        public MutableDecimal13f next() {
            return pool[index++ & (pool.length - 1)];
        }
    }

    @Benchmark
    public MutableDecimal13f newMutableDecimal13f(BenchmarkState state) {
        return new MutableDecimal13f();
    }

    @Benchmark
    public MutableDecimal13f newMutableDecimal13fLong(BenchmarkState state) {
        return new MutableDecimal13f(state.longValue);
    }

    @Benchmark
    public MutableDecimal13f newMutableDecimal13fDouble(BenchmarkState state) {
        return new MutableDecimal13f(state.doubleValue);
    }

    @Benchmark
    public MutableDecimal13f newMutableDecimal13fString(BenchmarkState state) {
        return new MutableDecimal13f(state.stringValue);
    }

    @Benchmark
    public MutableDecimal13f newMutableDecimal13fBigDecimal(BenchmarkState state) {
        return new MutableDecimal13f(state.bigDecimalValue);
    }

    @Benchmark
    public MutableDecimal13f newMutableDecimal13fDecimal13f(BenchmarkState state) {
        return new MutableDecimal13f(state.decimal13fValue);
    }

    @Benchmark
    public MutableDecimal13f newMutableDecimal13fDecimal(BenchmarkState state) {
        return new MutableDecimal13f(state.decimalValue);
    }

    @Benchmark
    public MutableDecimal13f setLong(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.set(state.longValue);
        return d;
    }

    @Benchmark
    public MutableDecimal13f setDouble(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.set(state.doubleValue);
        return d;
    }

    @Benchmark
    public MutableDecimal13f setString(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.set(state.stringValue);
        return d;
    }

    @Benchmark
    public MutableDecimal13f setBigDecimal(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.set(state.bigDecimalValue);
        return d;
    }

    @Benchmark
    public MutableDecimal13f setDecimal(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.set(state.decimal13fValue);
        return d;
    }

    @Benchmark
    public MutableDecimal13f setUnscaled(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.setUnscaled(state.longValue);
        return d;
    }

    @Benchmark
    public MutableDecimal13f add(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.add(state.b);
        return d;
    }

    @Benchmark
    public MutableDecimal13f subtract(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.subtract(state.b);
        return d;
    }

    @Benchmark
    public MutableDecimal13f multiply(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.multiply(state.b);
        return d;
    }

    @Benchmark
    public MutableDecimal13f divide(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.divide(state.b, RoundingMode.HALF_UP);
        return d;
    }

    @Benchmark
    public MutableDecimal13f remainder(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.remainder(state.b);
        return d;
    }

    @Benchmark
    public MutableDecimal13f negate(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.negate();
        return d;
    }

    @Benchmark
    public MutableDecimal13f abs(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.abs();
        return d;
    }

    @Benchmark
    public MutableDecimal13f square(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.square();
        return d;
    }

    @Benchmark
    public MutableDecimal13f sqrt(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.sqrt();
        return d;
    }

    @Benchmark
    public MutableDecimal13f pow(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.pow(2);
        return d;
    }

    @Benchmark
    public MutableDecimal13f avg(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.avg(state.b);
        return d;
    }

    @Benchmark
    public MutableDecimal13f shiftLeft(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.shiftLeft(2);
        return d;
    }

    @Benchmark
    public MutableDecimal13f shiftRight(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.shiftRight(2);
        return d;
    }

    @Benchmark
    public MutableDecimal13f round(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        d.round(2, RoundingMode.HALF_UP);
        return d;
    }

    @Benchmark
    public Decimal13f multiplyExact(BenchmarkState state) {
        MutableDecimal13f d = state.next();
        return d.multiplyExact().by(state.decimal0fValue);
    }

    @Benchmark
    public Decimal13f toImmutableDecimal(BenchmarkState state) {
        return state.readOnly.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal13f toMutableDecimal(BenchmarkState state) {
        return state.readOnly.toMutableDecimal();
    }

    @Benchmark
    public MutableDecimal13f clone(BenchmarkState state) {
        return state.readOnly.clone();
    }

    @Benchmark
    public long longValue(BenchmarkState state) {
        return state.readOnly.longValue();
    }

    @Benchmark
    public double doubleValue(BenchmarkState state) {
        return state.readOnly.doubleValue();
    }

    @Benchmark
    public float floatValue(BenchmarkState state) {
        return state.readOnly.floatValue();
    }

    @Benchmark
    public BigDecimal toBigDecimal(BenchmarkState state) {
        return state.readOnly.toBigDecimal();
    }

    @Benchmark
    public BigInteger toBigInteger(BenchmarkState state) {
        return state.readOnly.toBigInteger();
    }

    @Benchmark
    public long unscaledValue(BenchmarkState state) {
        return state.readOnly.unscaledValue();
    }

    @Benchmark
    public String toString(BenchmarkState state) {
        return state.readOnly.toString();
    }

    @Benchmark
    public int getScale(BenchmarkState state) {
        return state.readOnly.getScale();
    }

    @Benchmark
    public org.decimal4j.scale.Scale13f getScaleMetrics(BenchmarkState state) {
        return state.readOnly.getScaleMetrics();
    }

    @Benchmark
    public org.decimal4j.factory.Factory13f getFactory(BenchmarkState state) {
        return state.readOnly.getFactory();
    }
}
