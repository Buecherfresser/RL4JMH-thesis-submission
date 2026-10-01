package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.generic.GenericImmutableDecimal;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factories;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GenericImmutableDecimalBenchmark {

    // We fix the scale to 5 for concrete benchmarking
    private static final int TEST_SCALE = 5;
    private ScaleMetrics scaleMetrics;
    private GenericImmutableDecimal<ScaleMetrics> subjectDecimal;
    private GenericImmutableDecimal<ScaleMetrics> inputDecimalForValueOf;

    @Setup
    public void setup() {
        // 1. Setup Scale Metrics
        scaleMetrics = Scales.getScaleMetrics(TEST_SCALE);

        // 2. Setup Subject Decimal (e.g., unscaled value 12345)
        long unscaledValue = 12345L;
        subjectDecimal = GenericImmutableDecimal.valueOfUnscaled(scaleMetrics, unscaledValue);

        // 3. Setup Input Decimal for valueOf(Decimal)
        // Create a temporary decimal instance to pass to valueOf
        Decimal<ScaleMetrics> tempDecimal = new GenericImmutableDecimal<ScaleMetrics>(scaleMetrics, 98765L);
        inputDecimalForValueOf = GenericImmutableDecimal.valueOf(tempDecimal);
    }

    @Benchmark
    public void benchmarkGetScaleMetrics(Blackhole bh) {
        ScaleMetrics metrics = subjectDecimal.getScaleMetrics();
        bh.consume(metrics);
    }

    @Benchmark
    public void benchmarkGetScale(Blackhole bh) {
        int scale = subjectDecimal.getScale();
        bh.consume(scale);
    }

    @Benchmark
    public void benchmarkGetFactory(Blackhole bh) {
        org.decimal4j.generic.GenericDecimalFactory<ScaleMetrics> factory = subjectDecimal.getFactory();
        bh.consume(factory);
    }

    @Benchmark
    public void benchmarkUnscaledValue(Blackhole bh) {
        long unscaled = subjectDecimal.unscaledValue();
        bh.consume(unscaled);
    }

    @Benchmark
    public void benchmarkValueOf(Blackhole bh) {
        // Test valueOf(Decimal<S> decimal)
        GenericImmutableDecimal<ScaleMetrics> result = GenericImmutableDecimal.valueOf(inputDecimalForValueOf);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaled(Blackhole bh) {
        // Test valueOfUnscaled(S scaleMetrics, long unscaled)
        long unscaled = 54321L;
        GenericImmutableDecimal<ScaleMetrics> result = GenericImmutableDecimal.valueOfUnscaled(scaleMetrics, unscaled);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledByInt(Blackhole bh) {
        // Test valueOfUnscaled(int scale, long unscaled)
        int scale = 10;
        long unscaled = 100L;
        GenericImmutableDecimal<?> result = GenericImmutableDecimal.valueOfUnscaled(scale, unscaled);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToMutableDecimal(Blackhole bh) {
        // Test toMutableDecimal()
        org.decimal4j.generic.GenericMutableDecimal<ScaleMetrics> result = subjectDecimal.toMutableDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToImmutableDecimal(Blackhole bh) {
        // Test toImmutableDecimal() (should return self)
        GenericImmutableDecimal<ScaleMetrics> result = subjectDecimal.toImmutableDecimal();
        bh.consume(result);
    }
}
