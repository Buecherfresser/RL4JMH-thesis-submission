package bench.generated.c070;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.immutable.Decimal11f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.exact.Multipliable11f;
import org.decimal4j.factory.Factory11f;
import org.decimal4j.scale.Scale11f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal11fBenchmark {

    private MutableDecimal11f mutable;

    @Setup(Level.Trial)
    public void setUp() {
        mutable = new MutableDecimal11f(123456789L);
    }

    @Benchmark
    public MutableDecimal11f benchmarkZero() {
        return MutableDecimal11f.zero();
    }

    @Benchmark
    public MutableDecimal11f benchmarkOne() {
        return MutableDecimal11f.one();
    }

    @Benchmark
    public MutableDecimal11f benchmarkTwo() {
        return MutableDecimal11f.two();
    }

    @Benchmark
    public MutableDecimal11f benchmarkUnscaled() {
        return MutableDecimal11f.unscaled(987654321L);
    }

    @Benchmark
    public MutableDecimal11f benchmarkClone() {
        return mutable.clone();
    }

    @Benchmark
    public Decimal11f benchmarkToImmutable() {
        return mutable.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal11f benchmarkToMutable() {
        return mutable.toMutableDecimal();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return mutable.getScale();
    }

    @Benchmark
    public Scale11f benchmarkGetScaleMetrics() {
        return mutable.getScaleMetrics();
    }

    @Benchmark
    public Factory11f benchmarkGetFactory() {
        return mutable.getFactory();
    }

    @Benchmark
    public Object benchmarkMultiplyExact(Blackhole bh) {
        Multipliable11f multiplier = mutable.multiplyExact();
        Object result = multiplier.by(Decimal2f.ONE);
        bh.consume(result);
        return result;
    }
}
