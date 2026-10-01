package bench.generated.c070;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.mutable.MutableDecimal11f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal11fBenchmark {

    // State field is not strictly necessary for benchmarking static methods,
    // but kept for structural completeness if instance methods were added later.
    // We rely on static methods which create new instances each time.

    @Benchmark
    public void benchmark_zero(Blackhole bh) {
        MutableDecimal11f result = MutableDecimal11f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_one(Blackhole bh) {
        MutableDecimal11f result = MutableDecimal11f.one();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_ten(Blackhole bh) {
        MutableDecimal11f result = MutableDecimal11f.ten();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_half(Blackhole bh) {
        MutableDecimal11f result = MutableDecimal11f.half();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_unscaled(Blackhole bh) {
        // Test static method that returns a new instance
        MutableDecimal11f result = MutableDecimal11f.unscaled(1234567890123L);
        bh.consume(result);
    }
}
