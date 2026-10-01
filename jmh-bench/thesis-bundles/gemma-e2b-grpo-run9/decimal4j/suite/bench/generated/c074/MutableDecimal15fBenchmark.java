package bench.generated.c074;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.mutable.MutableDecimal15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal15fBenchmark {

    // State field to hold a mutable instance, initialized in setup
    private MutableDecimal15f mutableDecimal;

    @Setup
    public void setup() {
        // Initialize a mutable decimal instance. This is safe as it's a simple constructor.
        this.mutableDecimal = MutableDecimal15f.zero();
    }

    // --- Benchmarks for static factory methods ---

    @Benchmark
    public void benchmark_zero(Blackhole bh) {
        MutableDecimal15f result = MutableDecimal15f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_one(Blackhole bh) {
        MutableDecimal15f result = MutableDecimal15f.one();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_ten(Blackhole bh) {
        MutableDecimal15f result = MutableDecimal15f.ten();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_half(Blackhole bh) {
        MutableDecimal15f result = MutableDecimal15f.half();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_thousandth(Blackhole bh) {
        MutableDecimal15f result = MutableDecimal15f.thousandth();
        bh.consume(result);
    }

    // --- Benchmarks for static utility methods ---

    @Benchmark
    public void benchmark_unscaled(Blackhole bh) {
        // Test the static method that returns a new instance
        MutableDecimal15f result = MutableDecimal15f.unscaled(123456789012345L);
        bh.consume(result);
    }

    // --- Benchmarks for instance methods (mutating/cloning) ---

    @Benchmark
    public void benchmark_clone(Blackhole bh) {
        // Test the clone method
        MutableDecimal15f cloned = this.mutableDecimal.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void benchmark_toMutableDecimal(Blackhole bh) {
        // Test the method that returns 'this'
        MutableDecimal15f result = this.mutableDecimal.toMutableDecimal();
        bh.consume(result);
    }

    // --- Benchmarks for conversion methods (using setup state) ---

    @Benchmark
    public void benchmark_toImmutableDecimal(Blackhole bh) {
        // Test conversion to immutable
        org.decimal4j.immutable.Decimal15f immutable = this.mutableDecimal.toImmutableDecimal();
        bh.consume(immutable);
    }
}
