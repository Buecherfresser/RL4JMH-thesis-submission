package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import org.decimal4j.mutable.MutableDecimal15f;
import org.decimal4j.immutable.Decimal15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal15fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchState {
        MutableDecimal15f[] operands;
        MutableDecimal15f[] others;
        MutableDecimal15f[] nonNegativeOperands; // for sqrt
        int index;
        int exponent;
        int shift;
        int precision;
        long longValue;
        double doubleValue;
        String stringValue;
        BigDecimal bigDecimalValue;

        @Setup(Level.Trial)
        public void setup() {
            Random rand = new Random(12345);
            int n = 1024;
            operands = new MutableDecimal15f[n];
            others = new MutableDecimal15f[n];
            nonNegativeOperands = new MutableDecimal15f[n];
            for (int i = 0; i < n; i++) {
                double d = (rand.nextDouble() - 0.5) * 2000; // -1000 to 1000
                operands[i] = new MutableDecimal15f(d);
                double d2 = (rand.nextDouble() - 0.5) * 2000;
                // avoid zero for division
                if (Math.abs(d2) < 0.001) d2 = 1.0;
                others[i] = new MutableDecimal15f(d2);
                double d3 = rand.nextDouble() * 1000; // non-negative
                nonNegativeOperands[i] = new MutableDecimal15f(d3);
            }
            index = 0;
            exponent = 2;
            shift = 3;
            precision = 5;
            longValue = 123456789L;
            doubleValue = 123.456789;
            stringValue = "123.456789012345678";
            bigDecimalValue = new BigDecimal("123.456789012345678");
        }

        public MutableDecimal15f nextOperand() {
            MutableDecimal15f op = operands[index];
            index = (index + 1) % operands.length;
            return op;
        }

        public MutableDecimal15f nextOther() {
            MutableDecimal15f op = others[index];
            // use same index as operand for simplicity
            return op;
        }

        public MutableDecimal15f nextNonNegative() {
            MutableDecimal15f op = nonNegativeOperands[index];
            return op;
        }
    }

    // --- Arithmetic operations (mutating) ---

    @Benchmark
    public MutableDecimal15f add(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        MutableDecimal15f b = state.nextOther();
        return a.add(b);
    }

    @Benchmark
    public MutableDecimal15f subtract(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        MutableDecimal15f b = state.nextOther();
        return a.subtract(b);
    }

    @Benchmark
    public MutableDecimal15f multiply(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        MutableDecimal15f b = state.nextOther();
        return a.multiply(b);
    }

    @Benchmark
    public MutableDecimal15f divide(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        MutableDecimal15f b = state.nextOther();
        return a.divide(b, RoundingMode.HALF_UP);
    }

    @Benchmark
    public MutableDecimal15f remainder(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        MutableDecimal15f b = state.nextOther();
        return a.remainder(b);
    }

    @Benchmark
    public MutableDecimal15f avg(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        MutableDecimal15f b = state.nextOther();
        return a.avg(b);
    }

    @Benchmark
    public MutableDecimal15f negate(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.negate();
    }

    @Benchmark
    public MutableDecimal15f abs(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.abs();
    }

    @Benchmark
    public MutableDecimal15f invert(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.invert();
    }

    @Benchmark
    public MutableDecimal15f square(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.square();
    }

    @Benchmark
    public MutableDecimal15f sqrt(BenchState state) {
        MutableDecimal15f a = state.nextNonNegative();
        return a.sqrt();
    }

    @Benchmark
    public MutableDecimal15f pow(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.pow(state.exponent);
    }

    @Benchmark
    public MutableDecimal15f shiftLeft(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.shiftLeft(state.shift);
    }

    @Benchmark
    public MutableDecimal15f shiftRight(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.shiftRight(state.shift);
    }

    @Benchmark
    public MutableDecimal15f round(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.round(state.precision);
    }

    // --- Set operations ---

    @Benchmark
    public MutableDecimal15f setLong(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.set(state.longValue);
    }

    @Benchmark
    public MutableDecimal15f setDouble(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.set(state.doubleValue);
    }

    @Benchmark
    public MutableDecimal15f setString(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.set(state.stringValue);
    }

    @Benchmark
    public MutableDecimal15f setBigDecimal(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.set(state.bigDecimalValue);
    }

    @Benchmark
    public MutableDecimal15f setUnscaled(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.setUnscaled(state.longValue, 15);
    }

    // --- Conversions and other non-mutating operations ---

    @Benchmark
    public long unscaledValue(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.unscaledValue();
    }

    @Benchmark
    public double doubleValue(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.doubleValue();
    }

    @Benchmark
    public float floatValue(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.floatValue();
    }

    @Benchmark
    public long longValue(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.longValue();
    }

    @Benchmark
    public int intValue(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.intValue();
    }

    @Benchmark
    public BigDecimal toBigDecimal(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.toBigDecimal();
    }

    @Benchmark
    public String toString(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.toString();
    }

    @Benchmark
    public Decimal15f toImmutableDecimal(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal15f toMutableDecimal(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.toMutableDecimal();
    }

    @Benchmark
    public MutableDecimal15f clone(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.clone();
    }

    @Benchmark
    public int getScale(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.getScale();
    }

    @Benchmark
    public org.decimal4j.scale.Scale15f getScaleMetrics(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.getScaleMetrics();
    }

    @Benchmark
    public org.decimal4j.factory.Factory15f getFactory(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.getFactory();
    }

    @Benchmark
    public org.decimal4j.exact.Multipliable15f multiplyExact(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        return a.multiplyExact();
    }

    @Benchmark
    public int compareTo(BenchState state) {
        MutableDecimal15f a = state.nextOperand();
        MutableDecimal15f b = state.nextOther();
        return a.compareTo(b);
    }
}
