package bench.generated.c108;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import org.decimal4j.truncate.DecimalRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DecimalRoundingBenchmark {

    /**
     * Benchmarks the static factory method valueOf, which involves an internal lookup.
     * This is a safe, read-only operation.
     */
    @Benchmark
    public void benchmarkValueOf(Blackhole bh) {
        // Test static factory method
        DecimalRounding.valueOf(RoundingMode.HALF_UP);
        bh.consume(null);
    }

    /**
     * Benchmarks another static call to ensure execution path coverage.
     */
    @Benchmark
    public void benchmarkValueOf_HALF_EVEN(Blackhole bh) {
        // Ensure the lookup path is exercised
        DecimalRounding.valueOf(RoundingMode.HALF_EVEN);
        bh.consume(null);
    }
}
