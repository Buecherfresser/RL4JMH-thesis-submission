package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.generic.GenericMutableDecimal;
import org.decimal4j.generic.GenericImmutableDecimal;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GenericMutableDecimalBenchmark {

    private ScaleMetrics scaleMetrics;
    private GenericMutableDecimal<ScaleMetrics> base;
    private GenericMutableDecimal<ScaleMetrics> other;

    @Setup
    public void setup() {
        // Use scale 5 as a representative scale
        this.scaleMetrics = Scales.getScaleMetrics(5);
        // Base value: 123456 * 10^-5
        this.base = new GenericMutableDecimal<>(scaleMetrics, 123456L);
        // Other value: 654321 * 10^-5
        this.other = new GenericMutableDecimal<>(scaleMetrics, 654321L);
    }

    // -------------------------------------------------------------------------
    // Construction and cloning
    // -------------------------------------------------------------------------

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkConstructor() {
        return new GenericMutableDecimal<>(scaleMetrics, 987654L);
    }

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkClone() {
        return base.clone();
    }

    // -------------------------------------------------------------------------
    // Read‑only operations
    // -------------------------------------------------------------------------

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkToImmutable() {
        return base.toImmutableDecimal();
    }

    @Benchmark
    public String benchmarkToString() {
        return base.toString();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return base.getScale();
    }

    @Benchmark
    public ScaleMetrics benchmarkGetScaleMetrics() {
        return base.getScaleMetrics();
    }

    @Benchmark
    public long benchmarkUnscaledValue() {
        return base.unscaledValue();
    }

    // -------------------------------------------------------------------------
    // Mutating arithmetic operations (each works on a fresh clone)
    // -------------------------------------------------------------------------

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkAdd() {
        GenericMutableDecimal<ScaleMetrics> copy = base.clone();
        return copy.add(other.unscaledValue());
    }

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkSubtract() {
        GenericMutableDecimal<ScaleMetrics> copy = base.clone();
        return copy.subtract(other.unscaledValue());
    }

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkMultiply() {
        GenericMutableDecimal<ScaleMetrics> copy = base.clone();
        return copy.multiply(other.unscaledValue());
    }

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkDivide() {
        GenericMutableDecimal<ScaleMetrics> copy = base.clone();
        return copy.divide(other.unscaledValue());
    }

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkNegate() {
        GenericMutableDecimal<ScaleMetrics> copy = base.clone();
        return copy.negate();
    }

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkAbs() {
        GenericMutableDecimal<ScaleMetrics> copy = base.clone();
        return copy.abs();
    }

    // -------------------------------------------------------------------------
    // Mutating setters
    // -------------------------------------------------------------------------

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkSet() {
        GenericMutableDecimal<ScaleMetrics> copy = base.clone();
        return copy.set(other.unscaledValue());
    }

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkSetUnscaled() {
        GenericMutableDecimal<ScaleMetrics> copy = base.clone();
        return copy.setUnscaled(other.unscaledValue());
    }

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkSetZero() {
        GenericMutableDecimal<ScaleMetrics> copy = base.clone();
        return copy.setZero();
    }

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkSetOne() {
        GenericMutableDecimal<ScaleMetrics> copy = base.clone();
        return copy.setOne();
    }

    // -------------------------------------------------------------------------
    // Miscellaneous (using Blackhole to avoid dead‑code elimination)
    // -------------------------------------------------------------------------

    @Benchmark
    public void benchmarkConsumeWithBlackhole(Blackhole bh) {
        bh.consume(base);
        bh.consume(other);
        bh.consume(scaleMetrics);
    }
}
