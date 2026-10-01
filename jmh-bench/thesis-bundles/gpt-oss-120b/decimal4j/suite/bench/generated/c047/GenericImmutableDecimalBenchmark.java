package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.generic.GenericImmutableDecimal;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GenericImmutableDecimalBenchmark {

    private GenericImmutableDecimal<ScaleMetrics> decA;
    private GenericImmutableDecimal<ScaleMetrics> decB;
    private ScaleMetrics scaleMetrics;

    @Setup(Level.Trial)
    public void setup() {
        // use scale 5 for the benchmarks
        scaleMetrics = Scales.getScaleMetrics(5);
        decA = GenericImmutableDecimal.valueOfUnscaled(scaleMetrics, 123456789L);
        decB = GenericImmutableDecimal.valueOfUnscaled(scaleMetrics, 987654321L);
    }

    // -----------------------------------------------------------------------
    // Construction benchmarks
    // -----------------------------------------------------------------------

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfUnscaledScaleMetrics() {
        return GenericImmutableDecimal.valueOfUnscaled(scaleMetrics, 55555555L);
    }

    @Benchmark
    public GenericImmutableDecimal<?> benchmarkValueOfUnscaledIntScale() {
        return GenericImmutableDecimal.valueOfUnscaled(7, 77777777L);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfFromDecimal() {
        return GenericImmutableDecimal.valueOf(decA);
    }

    // -----------------------------------------------------------------------
    // Conversion benchmarks
    // -----------------------------------------------------------------------

    @Benchmark
    public Object benchmarkToMutableDecimal() {
        return decA.toMutableDecimal();
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkToImmutableDecimal() {
        return decA.toImmutableDecimal();
    }

    // -----------------------------------------------------------------------
    // Accessor benchmarks
    // -----------------------------------------------------------------------

    @Benchmark
    public int benchmarkGetScale() {
        return decA.getScale();
    }

    @Benchmark
    public ScaleMetrics benchmarkGetScaleMetrics() {
        return decA.getScaleMetrics();
    }

    // -----------------------------------------------------------------------
    // Arithmetic benchmarks (immutable, each call creates a new instance)
    // -----------------------------------------------------------------------

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkAdd() {
        return decA.add(decB);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkSubtract() {
        return decA.subtract(decB);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkMultiply() {
        return decA.multiply(decB);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkDivide() {
        return decA.divide(decB);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkNegate() {
        return decA.negate();
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkAbs() {
        return decA.abs();
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkSquare() {
        return decA.square();
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkSqrt() {
        return decA.sqrt();
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkPow() {
        return decA.pow(3);
    }
}
