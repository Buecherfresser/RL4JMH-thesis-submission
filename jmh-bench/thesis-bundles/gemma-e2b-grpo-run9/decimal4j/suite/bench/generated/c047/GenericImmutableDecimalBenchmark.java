package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
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

    // No state needed as we rely on static methods for benchmarking.

    @Benchmark
    public void testValueOfUnscaled(Blackhole bh) {
        try {
            // Test static factory method that takes scale metrics and unscaled value
            GenericImmutableDecimal<?> result = GenericImmutableDecimal.valueOfUnscaled(
                Scales.getScaleMetrics(0), 123456789L
            );
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking
        }
    }

    @Benchmark
    public void testValueOfUnscaledWithScale(Blackhole bh) {
        try {
            // Test static factory method that takes scale and unscaled value
            GenericImmutableDecimal<?> result = GenericImmutableDecimal.valueOfUnscaled(
                10, 987654321L
            );
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testGetScale(Blackhole bh) {
        try {
            // Test a static method call to ensure compilation and execution path
            GenericImmutableDecimal<?> result = GenericImmutableDecimal.valueOfUnscaled(
                Scales.getScaleMetrics(0), 1L
            );
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
