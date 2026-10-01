package bench.generated.c081;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.factory.Factory4f;
import org.decimal4j.exact.Multipliable4f;
import org.decimal4j.scale.Scale4f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal4fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        MutableDecimal4f mutable;
        Decimal4f immutable;
        long unscaledValue;

        @Setup(Level.Trial)
        public void setUp() {
            // Example value 12345 corresponds to 1.2345 with scale 4
            this.unscaledValue = 12_345L;
            this.mutable = new MutableDecimal4f(this.unscaledValue);
            this.immutable = Decimal4f.valueOf(this.unscaledValue);
        }
    }

    @Benchmark
    public MutableDecimal4f benchZero() {
        return MutableDecimal4f.zero();
    }

    @Benchmark
    public MutableDecimal4f benchOne() {
        return MutableDecimal4f.one();
    }

    @Benchmark
    public MutableDecimal4f benchTwo() {
        return MutableDecimal4f.two();
    }

    @Benchmark
    public MutableDecimal4f benchTen() {
        return MutableDecimal4f.ten();
    }

    @Benchmark
    public MutableDecimal4f benchHundred() {
        return MutableDecimal4f.hundred();
    }

    @Benchmark
    public MutableDecimal4f benchThousand() {
        return MutableDecimal4f.thousand();
    }

    @Benchmark
    public MutableDecimal4f benchMillion() {
        return MutableDecimal4f.million();
    }

    @Benchmark
    public MutableDecimal4f benchBillion() {
        return MutableDecimal4f.billion();
    }

    @Benchmark
    public MutableDecimal4f benchTrillion() {
        return MutableDecimal4f.trillion();
    }

    @Benchmark
    public MutableDecimal4f benchMinusOne() {
        return MutableDecimal4f.minusOne();
    }

    @Benchmark
    public MutableDecimal4f benchHalf() {
        return MutableDecimal4f.half();
    }

    @Benchmark
    public MutableDecimal4f benchTenth() {
        return MutableDecimal4f.tenth();
    }

    @Benchmark
    public MutableDecimal4f benchHundredth() {
        return MutableDecimal4f.hundredth();
    }

    @Benchmark
    public MutableDecimal4f benchThousandth() {
        return MutableDecimal4f.thousandth();
    }

    @Benchmark
    public MutableDecimal4f benchUnscaled(BenchmarkState state) {
        return MutableDecimal4f.unscaled(state.unscaledValue);
    }

    @Benchmark
    public Multipliable4f benchMultiplyExact(BenchmarkState state) {
        return state.mutable.multiplyExact();
    }

    @Benchmark
    public Decimal4f benchToImmutableDecimal(BenchmarkState state) {
        return state.mutable.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal4f benchClone(BenchmarkState state) {
        return state.mutable.clone();
    }

    @Benchmark
    public Factory4f benchGetFactory(BenchmarkState state) {
        return state.mutable.getFactory();
    }

    @Benchmark
    public Scale4f benchGetScaleMetrics(BenchmarkState state) {
        return state.mutable.getScaleMetrics();
    }

    @Benchmark
    public int benchGetScale(BenchmarkState state) {
        return state.mutable.getScale();
    }
}
