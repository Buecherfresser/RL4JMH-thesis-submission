package bench.generated.c084;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.exact.Multipliable7f;
import org.decimal4j.scale.Scale7f;
import org.decimal4j.factory.Factory7f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal7fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        MutableDecimal7f mutable;
        long unscaledValue;

        @Setup(Level.Trial)
        public void setUp() {
            mutable = MutableDecimal7f.one(); // start with value 1
            unscaledValue = 123456789L;       // arbitrary unscaled value
        }
    }

    @Benchmark
    public MutableDecimal7f benchmarkUnscaled(BenchmarkState state) {
        return MutableDecimal7f.unscaled(state.unscaledValue);
    }

    @Benchmark
    public MutableDecimal7f benchmarkZero() {
        return MutableDecimal7f.zero();
    }

    @Benchmark
    public MutableDecimal7f benchmarkOne() {
        return MutableDecimal7f.one();
    }

    @Benchmark
    public MutableDecimal7f benchmarkTwo() {
        return MutableDecimal7f.two();
    }

    @Benchmark
    public MutableDecimal7f benchmarkTen() {
        return MutableDecimal7f.ten();
    }

    @Benchmark
    public MutableDecimal7f benchmarkMillion() {
        return MutableDecimal7f.million();
    }

    @Benchmark
    public Multipliable7f benchmarkMultiplyExact(BenchmarkState state) {
        return state.mutable.multiplyExact();
    }

    @Benchmark
    public Decimal7f benchmarkToImmutableDecimal(BenchmarkState state) {
        return state.mutable.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal7f benchmarkClone(BenchmarkState state) {
        return state.mutable.clone();
    }

    @Benchmark
    public int benchmarkGetScale(BenchmarkState state) {
        return state.mutable.getScale();
    }

    @Benchmark
    public Scale7f benchmarkGetScaleMetrics(BenchmarkState state) {
        return state.mutable.getScaleMetrics();
    }

    @Benchmark
    public Factory7f benchmarkGetFactory(BenchmarkState state) {
        return state.mutable.getFactory();
    }
}
