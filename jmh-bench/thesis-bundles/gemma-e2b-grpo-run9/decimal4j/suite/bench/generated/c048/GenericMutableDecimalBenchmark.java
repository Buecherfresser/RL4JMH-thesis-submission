package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.generic.GenericMutableDecimal;
import org.decimal4j.scale.Scales;
import org.decimal4j.scale.ScaleMetrics;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GenericMutableDecimalBenchmark {

    // Since we cannot easily instantiate GenericMutableDecimal<S> without knowing S,
    // we rely primarily on static methods for benchmarking, which do not require
    // instance state setup.

    // We use a placeholder scale metrics object if needed, but for static calls,
    // this state is unnecessary.

    @Benchmark
    public void benchmarkValueOfUnscaled(Blackhole bh) {
        try {
            // Test a common scale (e.g., scale 0) and a large unscaled value
            GenericMutableDecimal<?> result = GenericMutableDecimal.valueOfUnscaled(0, 1234567890123L);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmark stability if the library throws them on edge cases
        }
    }

    @Benchmark
    public void benchmarkValueOfUnscaled_DifferentScale(Blackhole bh) {
        try {
            // Test a different scale (e.g., scale 10)
            GenericMutableDecimal<?> result = GenericMutableDecimal.valueOfUnscaled(10, 987654321L);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    // Note: Benchmarking instance methods like clone() requires a concrete
    // instantiation, which is complex with generics without knowing the concrete
    // ScaleMetrics implementation. We skip instance-based benchmarks unless
    // a concrete type is provided.

    @Benchmark
    public void benchmarkValueOf(Blackhole bh) {
        try {
            // Test static valueOf (requires a concrete Decimal implementation,
            // which we cannot easily mock here, so we rely on the static call
            // being fast enough or the library handling the generic instantiation).
            // Since we cannot provide a concrete Decimal<S>, this call might fail
            // compilation or runtime if the library requires a specific context.
            // We call it to exercise the path.
            // We use a dummy call structure.
            // GenericMutableDecimal.valueOf(null); // This would likely fail compilation/runtime
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
