package bench.generated.c077;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal18f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.api.Decimal;
import java.math.BigDecimal;
import java.math.BigInteger;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1) @Warmup(iterations = 1, time = 1) @Measurement(iterations = 3, time = 1)
public class MutableDecimal18fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchState {
        private static final int POOL_SIZE = 1024; // power of 2
        MutableDecimal18f[] receivers;
        MutableDecimal18f[] operands;
        int index;
        MutableDecimal18f single;
        long longOperand = 2L;
        double doubleOperand = 2.0;
        BigDecimal bigDecimalOperand = new BigDecimal("2.0");
        String stringOperand = "2.0";
        BigInteger bigIntegerOperand = BigInteger.valueOf(2);
        Decimal18f decimal18fOperand = Decimal18f.valueOf(2.0);
        Decimal0f decimal0fOperand = Decimal0f.valueOf(2);

        @Setup(Level.Trial)
        public void setup() {
            receivers = new MutableDecimal18f[POOL_SIZE];
            operands = new MutableDecimal18f[POOL_SIZE];
            for (int i = 0; i < POOL_SIZE; i++) {
                receivers[i] = new MutableDecimal18f(1.0);
                operands[i] = new MutableDecimal18f(2.0);
            }
            single = new MutableDecimal18f(1.0);
        }

        @Setup(Level.Iteration)
        public void reset() {
            for (int i = 0; i < POOL_SIZE; i++) {
                receivers[i].set(1.0);
            }
            index = 0;
        }

        public MutableDecimal18f nextReceiver() {
            return receivers[index & (POOL_SIZE - 1)];
        }

        public MutableDecimal18f nextOperand() {
            return operands[index & (POOL_SIZE - 1)];
        }

        public void advanceIndex() {
            index = (index + 1) & (POOL_SIZE - 1);
        }
    }

    @Benchmark
    public MutableDecimal18f add(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        MutableDecimal18f o = state.nextOperand();
        state.advanceIndex();
        return r.add(o);
    }

    @Benchmark
    public MutableDecimal18f subtract(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        MutableDecimal18f o = state.nextOperand();
        state.advanceIndex();
        return r.subtract(o);
    }

    @Benchmark
    public MutableDecimal18f multiply(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        MutableDecimal18f o = state.nextOperand();
        state.advanceIndex();
        return r.multiply(o);
    }

    @Benchmark
    public MutableDecimal18f divide(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        MutableDecimal18f o = state.nextOperand();
        state.advanceIndex();
        return r.divide(o);
    }

    @Benchmark
    public MutableDecimal18f addLong(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        state.advanceIndex();
        return r.add(state.longOperand);
    }

    @Benchmark
    public MutableDecimal18f addDouble(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        state.advanceIndex();
        return r.add(state.doubleOperand);
    }

    @Benchmark
    public MutableDecimal18f setLong(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        state.advanceIndex();
        return r.set(state.longOperand);
    }

    @Benchmark
    public MutableDecimal18f setDouble(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        state.advanceIndex();
        return r.set(state.doubleOperand);
    }

    @Benchmark
    public MutableDecimal18f setString(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        state.advanceIndex();
        return r.set(state.stringOperand);
    }

    @Benchmark
    public MutableDecimal18f setBigDecimal(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        state.advanceIndex();
        return r.set(state.bigDecimalOperand);
    }

    @Benchmark
    public MutableDecimal18f setBigInteger(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        state.advanceIndex();
        return r.set(state.bigIntegerOperand);
    }

    @Benchmark
    public MutableDecimal18f setDecimal18f(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        state.advanceIndex();
        return r.set(state.decimal18fOperand);
    }

    @Benchmark
    public Decimal18f toImmutableDecimal(BenchState state) {
        return state.single.toImmutableDecimal();
    }

    @Benchmark
    public long unscaledValue(BenchState state) {
        return state.single.unscaledValue();
    }

    @Benchmark
    public int getScale(BenchState state) {
        return state.single.getScale();
    }

    @Benchmark
    public MutableDecimal18f clone(BenchState state) {
        return state.single.clone();
    }

    @Benchmark
    public Decimal<?> multiplyExact(BenchState state) {
        MutableDecimal18f r = state.nextReceiver();
        state.advanceIndex();
        return r.multiplyExact().by(state.decimal0fOperand);
    }

    @Benchmark
    public MutableDecimal18f zero() {
        return MutableDecimal18f.zero();
    }

    @Benchmark
    public MutableDecimal18f one() {
        return MutableDecimal18f.one();
    }

    @Benchmark
    public MutableDecimal18f unscaled() {
        return MutableDecimal18f.unscaled(123456789012345678L);
    }

    @Benchmark
    public MutableDecimal18f newFromLong() {
        return new MutableDecimal18f(123456789012345678L);
    }

    @Benchmark
    public MutableDecimal18f newFromDouble() {
        return new MutableDecimal18f(1234567890.123456789);
    }

    @Benchmark
    public MutableDecimal18f newFromString() {
        return new MutableDecimal18f("1234567890.123456789");
    }
}
