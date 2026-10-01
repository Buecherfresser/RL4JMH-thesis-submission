package bench.generated.c080;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.factory.Factory3f;
import org.decimal4j.scale.Scale3f;
import org.decimal4j.exact.Multipliable3f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal3fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        MutableDecimal3f one;
        MutableDecimal3f two;

        @Setup(Level.Trial)
        public void setUp() {
            one = MutableDecimal3f.one();
            two = MutableDecimal3f.two();
        }
    }

    @Benchmark
    public MutableDecimal3f benchZeroFactory() {
        return MutableDecimal3f.zero();
    }

    @Benchmark
    public MutableDecimal3f benchOneFactory() {
        return MutableDecimal3f.one();
    }

    @Benchmark
    public MutableDecimal3f benchFromLong() {
        return new MutableDecimal3f(123456L);
    }

    @Benchmark
    public MutableDecimal3f benchFromDouble() {
        return new MutableDecimal3f(12345.678);
    }

    @Benchmark
    public MutableDecimal3f benchFromString() {
        return new MutableDecimal3f("12345.678");
    }

    @Benchmark
    public MutableDecimal3f benchClone(BenchmarkState s) {
        return s.one.clone();
    }

    @Benchmark
    public int benchGetScale(BenchmarkState s) {
        return s.one.getScale();
    }

    @Benchmark
    public Scale3f benchGetScaleMetrics(BenchmarkState s) {
        return s.one.getScaleMetrics();
    }

    @Benchmark
    public Factory3f benchGetFactory(BenchmarkState s) {
        return s.one.getFactory();
    }

    @Benchmark
    public Decimal3f benchToImmutable(BenchmarkState s) {
        return s.one.toImmutableDecimal();
    }

    @Benchmark
    public Multipliable3f benchMultiplyExact(BenchmarkState s) {
        return s.one.multiplyExact();
    }

    @Benchmark
    public MutableDecimal3f benchAdd(BenchmarkState s) {
        MutableDecimal3f a = s.one.clone();
        return a.add(s.two);
    }

    @Benchmark
    public MutableDecimal3f benchSubtract(BenchmarkState s) {
        MutableDecimal3f a = s.one.clone();
        return a.subtract(s.two);
    }

    @Benchmark
    public MutableDecimal3f benchMultiply(BenchmarkState s) {
        MutableDecimal3f a = s.one.clone();
        return a.multiply(s.two);
    }

    @Benchmark
    public MutableDecimal3f benchDivide(BenchmarkState s) {
        MutableDecimal3f a = s.one.clone();
        return a.divide(s.two);
    }

    @Benchmark
    public MutableDecimal3f benchNegate(BenchmarkState s) {
        MutableDecimal3f a = s.one.clone();
        return a.negate();
    }

    @Benchmark
    public MutableDecimal3f benchAbs(BenchmarkState s) {
        MutableDecimal3f a = s.one.clone();
        return a.abs();
    }

    @Benchmark
    public void benchSetLong(BenchmarkState s, Blackhole bh) {
        MutableDecimal3f a = s.one.clone();
        bh.consume(a.set(987654L));
    }

    @Benchmark
    public void benchSetDouble(BenchmarkState s, Blackhole bh) {
        MutableDecimal3f a = s.one.clone();
        bh.consume(a.set(98765.432));
    }

    @Benchmark
    public void benchSetString(BenchmarkState s, Blackhole bh) {
        MutableDecimal3f a = s.one.clone();
        bh.consume(a.set("98765.432"));
    }

    @Benchmark
    public void benchSetZero(BenchmarkState s, Blackhole bh) {
        MutableDecimal3f a = s.one.clone();
        bh.consume(a.setZero());
    }

    @Benchmark
    public void benchSetOne(BenchmarkState s, Blackhole bh) {
        MutableDecimal3f a = s.one.clone();
        bh.consume(a.setOne());
    }

    @Benchmark
    public void benchSetMinusOne(BenchmarkState s, Blackhole bh) {
        MutableDecimal3f a = s.one.clone();
        bh.consume(a.setMinusOne());
    }
}
