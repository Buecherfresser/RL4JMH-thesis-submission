package bench.generated.c079;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.exact.Multipliable2f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal2fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        MutableDecimal2f base;
        Decimal2f two;
        Decimal2f three;
        long unscaledValue;

        @Setup(Level.Trial)
        public void setup() {
            base = new MutableDecimal2f(12345L); // 123.45
            two = Decimal2f.TWO;
            three = Decimal2f.THREE;
            unscaledValue = 987654321L;
        }
    }

    @Benchmark
    public MutableDecimal2f benchmarkZero() {
        return MutableDecimal2f.zero();
    }

    @Benchmark
    public MutableDecimal2f benchmarkOne() {
        return MutableDecimal2f.one();
    }

    @Benchmark
    public MutableDecimal2f benchmarkUnscaled(BenchmarkState s) {
        return MutableDecimal2f.unscaled(s.unscaledValue);
    }

    @Benchmark
    public MutableDecimal2f benchmarkClone(BenchmarkState s) {
        return s.base.clone();
    }

    @Benchmark
    public Decimal2f benchmarkToImmutable(BenchmarkState s) {
        return s.base.toImmutableDecimal();
    }

    @Benchmark
    public Multipliable2f benchmarkMultiplyExact(BenchmarkState s) {
        return s.base.multiplyExact();
    }

    @Benchmark
    public MutableDecimal2f benchmarkAdd(BenchmarkState s) {
        MutableDecimal2f m = s.base.clone();
        return m.add(s.two);
    }

    @Benchmark
    public MutableDecimal2f benchmarkSubtract(BenchmarkState s) {
        MutableDecimal2f m = s.base.clone();
        return m.subtract(s.two);
    }

    @Benchmark
    public MutableDecimal2f benchmarkMultiply(BenchmarkState s) {
        MutableDecimal2f m = s.base.clone();
        return m.multiply(s.two);
    }

    @Benchmark
    public MutableDecimal2f benchmarkDivide(BenchmarkState s) {
        MutableDecimal2f m = s.base.clone();
        return m.divide(s.two);
    }

    @Benchmark
    public MutableDecimal2f benchmarkNegate(BenchmarkState s) {
        MutableDecimal2f m = s.base.clone();
        return m.negate();
    }

    @Benchmark
    public MutableDecimal2f benchmarkAbs(BenchmarkState s) {
        MutableDecimal2f m = s.base.clone();
        m.set(-12345L);
        return m.abs();
    }

    @Benchmark
    public MutableDecimal2f benchmarkSetLong(BenchmarkState s) {
        MutableDecimal2f m = new MutableDecimal2f();
        return m.set(55555L);
    }

    @Benchmark
    public MutableDecimal2f benchmarkSetString(BenchmarkState s) {
        MutableDecimal2f m = new MutableDecimal2f();
        return m.set("123.45");
    }

    @Benchmark
    public void benchmarkToMutableDecimal(BenchmarkState s, Blackhole bh) {
        bh.consume(s.base.toMutableDecimal());
    }
}
