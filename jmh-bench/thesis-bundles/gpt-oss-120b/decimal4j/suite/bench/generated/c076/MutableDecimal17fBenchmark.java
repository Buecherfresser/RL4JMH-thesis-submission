package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal17f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.exact.Multipliable17f;
import org.decimal4j.scale.Scale17f;
import org.decimal4j.factory.Factory17f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal17fBenchmark {

    private MutableDecimal17f base;
    private Decimal17f one;
    private long unscaledValue;
    private long setUnscaledValue;

    @Setup(Level.Trial)
    public void setup() {
        base = new MutableDecimal17f(123456789L);
        one = Decimal17f.ONE;
        unscaledValue = 987654321L;
        setUnscaledValue = 555555555L;
    }

    @Benchmark
    public MutableDecimal17f benchZeroFactory() {
        return MutableDecimal17f.zero();
    }

    @Benchmark
    public MutableDecimal17f benchOneFactory() {
        return MutableDecimal17f.one();
    }

    @Benchmark
    public MutableDecimal17f benchUnscaled() {
        return MutableDecimal17f.unscaled(unscaledValue);
    }

    @Benchmark
    public MutableDecimal17f benchClone() {
        return base.clone();
    }

    @Benchmark
    public MutableDecimal17f benchSetZero() {
        MutableDecimal17f m = base.clone();
        return m.setZero();
    }

    @Benchmark
    public MutableDecimal17f benchSetOne() {
        MutableDecimal17f m = base.clone();
        return m.setOne();
    }

    @Benchmark
    public MutableDecimal17f benchSetUnscaled() {
        MutableDecimal17f m = base.clone();
        return m.setUnscaled(setUnscaledValue);
    }

    @Benchmark
    public Decimal17f benchToImmutable() {
        MutableDecimal17f m = base.clone();
        return m.toImmutableDecimal();
    }

    @Benchmark
    public Multipliable17f benchMultiplyExact() {
        MutableDecimal17f m = base.clone();
        return m.multiplyExact();
    }

    @Benchmark
    public MutableDecimal17f benchAdd() {
        MutableDecimal17f m = base.clone();
        return m.add(one);
    }

    @Benchmark
    public MutableDecimal17f benchNegate() {
        MutableDecimal17f m = base.clone();
        return m.negate();
    }

    @Benchmark
    public MutableDecimal17f benchAbs() {
        MutableDecimal17f m = base.clone();
        return m.abs();
    }

    @Benchmark
    public MutableDecimal17f benchDivide() {
        MutableDecimal17f m = base.clone();
        return m.divide(one);
    }

    @Benchmark
    public int benchGetScale() {
        return base.getScale();
    }

    @Benchmark
    public Scale17f benchGetScaleMetrics() {
        return base.getScaleMetrics();
    }

    @Benchmark
    public Factory17f benchGetFactory() {
        return base.getFactory();
    }
}
