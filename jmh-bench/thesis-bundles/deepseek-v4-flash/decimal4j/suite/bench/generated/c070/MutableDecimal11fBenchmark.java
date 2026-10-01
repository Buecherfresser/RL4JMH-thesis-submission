package bench.generated.c070;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.immutable.Decimal11f;
import org.decimal4j.factory.Factory11f;
import org.decimal4j.scale.Scale11f;
import org.decimal4j.exact.Multipliable11f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal11fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchState {
        final int N = 1 << 10;  // 1024
        int idx = 0;

        // shared receiver pool for most mutating operations
        MutableDecimal11f[] receivers = new MutableDecimal11f[N];
        // dedicated receiver pool for shiftLeft (initialised to zero)
        MutableDecimal11f[] shiftLeftReceivers = new MutableDecimal11f[N];
        // operand pools for arithmetic
        MutableDecimal11f[] addOperands = new MutableDecimal11f[N];
        MutableDecimal11f[] subOperands = new MutableDecimal11f[N];
        MutableDecimal11f[] mulOperands = new MutableDecimal11f[N];
        MutableDecimal11f[] divOperands = new MutableDecimal11f[N];
        MutableDecimal11f[] remOperands = new MutableDecimal11f[N];

        // raw inputs for constructors and setters
        long[] longValues = new long[N];
        double[] doubleValues = new double[N];
        String[] stringValues = new String[N];
        BigDecimal[] bigDecimalValues = new BigDecimal[N];

        // reusable instances for read-only benchmarks
        MutableDecimal11f one = MutableDecimal11f.one();
        MutableDecimal11f half = MutableDecimal11f.half();
        MutableDecimal11f setter = MutableDecimal11f.one();

        @Setup(Level.Trial)
        public void setup() {
            for (int i = 0; i < N; i++) {
                receivers[i] = MutableDecimal11f.one();
                shiftLeftReceivers[i] = MutableDecimal11f.zero();

                // add/subtract alternating ±1e-11 to keep values bounded
                addOperands[i] = MutableDecimal11f.unscaled((i & 1) == 0 ? 1 : -1);
                subOperands[i] = MutableDecimal11f.unscaled((i & 1) == 0 ? 1 : -1);

                // multiply/divide by values near 1.0, alternating above/below
                if ((i & 1) == 0) {
                    mulOperands[i] = Decimal11f.valueOf("1.00000000001").toMutableDecimal();
                    divOperands[i] = Decimal11f.valueOf("1.00000000001").toMutableDecimal();
                } else {
                    mulOperands[i] = Decimal11f.valueOf("0.99999999999").toMutableDecimal();
                    divOperands[i] = Decimal11f.valueOf("0.99999999999").toMutableDecimal();
                }
                // remainder divisor = 2.0
                remOperands[i] = MutableDecimal11f.two();

                longValues[i] = 123456789L + i;
                doubleValues[i] = 123456789.123456789d + i;
                stringValues[i] = "123456789.12345678901";
                bigDecimalValues[i] = new BigDecimal("123456789.12345678901");
            }
        }

        int nextIdx() {
            return idx++ & (N - 1);
        }
    }

    // ----- constructors -----
    @Benchmark
    public MutableDecimal11f newFromLong(BenchState s) {
        return new MutableDecimal11f(s.longValues[s.nextIdx()]);
    }

    @Benchmark
    public MutableDecimal11f newFromDouble(BenchState s) {
        return new MutableDecimal11f(s.doubleValues[s.nextIdx()]);
    }

    @Benchmark
    public MutableDecimal11f newFromString(BenchState s) {
        return new MutableDecimal11f(s.stringValues[s.nextIdx()]);
    }

    @Benchmark
    public MutableDecimal11f newFromBigDecimal(BenchState s) {
        return new MutableDecimal11f(s.bigDecimalValues[s.nextIdx()]);
    }

    // ----- static factories -----
    @Benchmark
    public MutableDecimal11f zero() {
        return MutableDecimal11f.zero();
    }

    @Benchmark
    public MutableDecimal11f one() {
        return MutableDecimal11f.one();
    }

    @Benchmark
    public MutableDecimal11f unscaled(BenchState s) {
        return MutableDecimal11f.unscaled(s.longValues[s.nextIdx()]);
    }

    // ----- information methods -----
    @Benchmark
    public int getScale(BenchState s) {
        return s.one.getScale();
    }

    @Benchmark
    public Scale11f getScaleMetrics(BenchState s) {
        return s.one.getScaleMetrics();
    }

    @Benchmark
    public Factory11f getFactory(BenchState s) {
        return s.one.getFactory();
    }

    @Benchmark
    public Decimal11f toImmutableDecimal(BenchState s) {
        return s.one.toImmutableDecimal();
    }

    @Benchmark
    public BigDecimal toBigDecimal(BenchState s) {
        return s.one.toBigDecimal();
    }

    @Benchmark
    public double doubleValue(BenchState s) {
        return s.one.doubleValue();
    }

    @Benchmark
    public long longValue(BenchState s) {
        return s.one.longValue();
    }

    @Benchmark
    public String toString(BenchState s) {
        return s.one.toString();
    }

    // ----- setters -----
    @Benchmark
    public MutableDecimal11f setLong(BenchState s) {
        return s.setter.set(s.longValues[s.nextIdx()]);
    }

    @Benchmark
    public MutableDecimal11f setDouble(BenchState s) {
        return s.setter.set(s.doubleValues[s.nextIdx()]);
    }

    @Benchmark
    public MutableDecimal11f setString(BenchState s) {
        return s.setter.set(s.stringValues[s.nextIdx()]);
    }

    @Benchmark
    public MutableDecimal11f setBigDecimal(BenchState s) {
        return s.setter.set(s.bigDecimalValues[s.nextIdx()]);
    }

    @Benchmark
    public MutableDecimal11f setZero(BenchState s) {
        return s.setter.setZero();
    }

    @Benchmark
    public MutableDecimal11f setOne(BenchState s) {
        return s.setter.setOne();
    }

    // ----- arithmetic (mutating) -----
    @Benchmark
    public MutableDecimal11f add(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].add(s.addOperands[i]);
    }

    @Benchmark
    public MutableDecimal11f addLong(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].add(1L);
    }

    @Benchmark
    public MutableDecimal11f subtract(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].subtract(s.subOperands[i]);
    }

    @Benchmark
    public MutableDecimal11f multiply(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].multiply(s.mulOperands[i]);
    }

    @Benchmark
    public MutableDecimal11f divide(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].divide(s.divOperands[i]);
    }

    @Benchmark
    public MutableDecimal11f remainder(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].remainder(s.remOperands[i]);
    }

    @Benchmark
    public MutableDecimal11f negate(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].negate();
    }

    @Benchmark
    public MutableDecimal11f abs(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].abs();
    }

    @Benchmark
    public MutableDecimal11f square(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].square();
    }

    @Benchmark
    public MutableDecimal11f sqrt(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].sqrt();
    }

    @Benchmark
    public MutableDecimal11f invert(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].invert();
    }

    @Benchmark
    public MutableDecimal11f avg(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].avg(s.one);
    }

    @Benchmark
    public MutableDecimal11f shiftLeft(BenchState s) {
        int i = s.nextIdx();
        return s.shiftLeftReceivers[i].shiftLeft(2);
    }

    @Benchmark
    public MutableDecimal11f shiftRight(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].shiftRight(2);
    }

    @Benchmark
    public MutableDecimal11f pow(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].pow(2);
    }

    @Benchmark
    public MutableDecimal11f round(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].round(2);
    }

    @Benchmark
    public Multipliable11f multiplyExact(BenchState s) {
        int i = s.nextIdx();
        return s.receivers[i].multiplyExact();
    }

    // ----- comparison and misc -----
    @Benchmark
    public boolean isZero(BenchState s) {
        return s.one.isZero();
    }

    @Benchmark
    public boolean isOne(BenchState s) {
        return s.one.isOne();
    }

    @Benchmark
    public int compareTo(BenchState s) {
        return s.one.compareTo(s.half);
    }

    @Benchmark
    public boolean equals(BenchState s) {
        return s.one.equals(s.half);
    }

    @Benchmark
    public int hashCode(BenchState s) {
        return s.one.hashCode();
    }
}
