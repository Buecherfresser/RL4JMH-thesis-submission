package bench.generated.c079;

import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable2f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal2fBenchmark {

    @State(Scope.Thread)
    public static class ThreadState {
        private static final int SIZE = 1024; // power of two for fast modulo

        MutableDecimal2f[] receivers;
        MutableDecimal2f[] operands;
        long[] longValues;
        double[] doubleValues;
        int index = 0;

        @Setup(Level.Trial)
        public void setup() {
            receivers = new MutableDecimal2f[SIZE];
            operands = new MutableDecimal2f[SIZE];
            longValues = new long[SIZE];
            doubleValues = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                receivers[i] = new MutableDecimal2f("123.45");
                operands[i] = new MutableDecimal2f("67.89");
                longValues[i] = 12345L;
                doubleValues[i] = 123.456;
            }
        }

        public int nextIndex() {
            int i = index;
            index = (index + 1) & (SIZE - 1);
            return i;
        }
    }

    // ---------- Arithmetic operations (mutating) ----------

    @Benchmark
    public MutableDecimal2f addDecimal(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].add(state.operands[i]);
    }

    @Benchmark
    public MutableDecimal2f addLong(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].add(state.longValues[i]);
    }

    @Benchmark
    public MutableDecimal2f addDouble(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].add(state.doubleValues[i]);
    }

    @Benchmark
    public MutableDecimal2f subtractDecimal(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].subtract(state.operands[i]);
    }

    @Benchmark
    public MutableDecimal2f subtractLong(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].subtract(state.longValues[i]);
    }

    @Benchmark
    public MutableDecimal2f subtractDouble(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].subtract(state.doubleValues[i]);
    }

    @Benchmark
    public MutableDecimal2f multiplyDecimal(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].multiply(state.operands[i]);
    }

    @Benchmark
    public MutableDecimal2f multiplyLong(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].multiply(state.longValues[i]);
    }

    @Benchmark
    public MutableDecimal2f multiplyDouble(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].multiply(state.doubleValues[i]);
    }

    @Benchmark
    public MutableDecimal2f divideDecimal(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].divide(state.operands[i]);
    }

    @Benchmark
    public MutableDecimal2f divideLong(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].divide(state.longValues[i]);
    }

    @Benchmark
    public MutableDecimal2f divideDouble(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].divide(state.doubleValues[i]);
    }

    @Benchmark
    public MutableDecimal2f remainderDecimal(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].remainder(state.operands[i]);
    }

    @Benchmark
    public MutableDecimal2f negate(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].negate();
    }

    @Benchmark
    public MutableDecimal2f abs(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].abs();
    }

    @Benchmark
    public MutableDecimal2f pow(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].pow(2);
    }

    @Benchmark
    public MutableDecimal2f sqrt(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].sqrt();
    }

    @Benchmark
    public MutableDecimal2f invert(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].invert();
    }

    @Benchmark
    public MutableDecimal2f avgDecimal(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].avg(state.operands[i]);
    }

    @Benchmark
    public MutableDecimal2f shiftLeft(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].shiftLeft(2);
    }

    @Benchmark
    public MutableDecimal2f shiftRight(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].shiftRight(2);
    }

    @Benchmark
    public MutableDecimal2f round(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].round(1);
    }

    @Benchmark
    public MutableDecimal2f multiplyUnscaled(ThreadState state) {
        int i = state.nextIndex();
        TruncationPolicy policy = UncheckedRounding.HALF_UP;
        return state.receivers[i].multiplyUnscaled(state.longValues[i], policy);
    }

    @Benchmark
    public Decimal<?> multiplyExact(ThreadState state) {
        int i = state.nextIndex();
        Multipliable2f m = state.receivers[i].multiplyExact();
        return m.by(state.operands[i]);
    }

    // ---------- Conversions and misc ----------

    @Benchmark
    public Decimal2f toImmutableDecimal(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal2f toMutableDecimal(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].toMutableDecimal();
    }

    @Benchmark
    public MutableDecimal2f clone(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].clone();
    }

    @Benchmark
    public long longValue(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].longValue();
    }

    @Benchmark
    public double doubleValue(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].doubleValue();
    }

    @Benchmark
    public float floatValue(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].floatValue();
    }

    @Benchmark
    public BigDecimal toBigDecimal(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].toBigDecimal();
    }

    @Benchmark
    public BigInteger toBigInteger(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].toBigInteger();
    }

    @Benchmark
    public long unscaledValue(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].unscaledValue();
    }

    @Benchmark
    public int getScale(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].getScale();
    }

    @Benchmark
    public String toString(ThreadState state) {
        int i = state.nextIndex();
        return state.receivers[i].toString();
    }

    // ---------- Static factories and constructors ----------

    @Benchmark
    public MutableDecimal2f unscaledLong(Blackhole bh) {
        return MutableDecimal2f.unscaled(12345L);
    }

    @Benchmark
    public MutableDecimal2f zero(Blackhole bh) {
        return MutableDecimal2f.zero();
    }

    @Benchmark
    public MutableDecimal2f one(Blackhole bh) {
        return MutableDecimal2f.one();
    }

    @Benchmark
    public MutableDecimal2f ten(Blackhole bh) {
        return MutableDecimal2f.ten();
    }

    @Benchmark
    public MutableDecimal2f constructorString(Blackhole bh) {
        return new MutableDecimal2f("123.45");
    }

    @Benchmark
    public MutableDecimal2f constructorLong(Blackhole bh) {
        return new MutableDecimal2f(12345L);
    }

    @Benchmark
    public MutableDecimal2f constructorDouble(Blackhole bh) {
        return new MutableDecimal2f(123.456);
    }

    @Benchmark
    public MutableDecimal2f constructorBigDecimal(Blackhole bh) {
        return new MutableDecimal2f(new BigDecimal("123.45"));
    }

    @Benchmark
    public MutableDecimal2f constructorDecimal2f(Blackhole bh) {
        return new MutableDecimal2f(Decimal2f.valueOf("123.45"));
    }
}
