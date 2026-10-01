package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.mutable.MutableDecimal10f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal10fBenchmark {

    // State field for the mutable decimal instance.
    private MutableDecimal10f mutableDecimal;

    // A static, immutable reference for methods that don't rely on instance state
    // or are static factory methods.
    private static MutableDecimal10f zero;

    @Setup
    public void setup() {
        // Initialize the mutable decimal instance once per benchmark run.
        this.mutableDecimal = MutableDecimal10f.zero();
        // Static initialization is safe here as it's immutable/static.
        zero = MutableDecimal10f.zero();
    }

    @Benchmark
    public void benchmark_zero(Blackhole bh) {
        // Test a static factory method that returns a new instance.
        MutableDecimal10f result = MutableDecimal10f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_one(Blackhole bh) {
        // Test a static factory method that returns a new instance.
        MutableDecimal10f result = MutableDecimal10f.one();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_set_zero(Blackhole bh) {
        // Test an in-place setter method.
        mutableDecimal.setZero();
        bh.consume(mutableDecimal);
    }

    @Benchmark
    public void benchmark_set_unscaled(Blackhole bh) {
        // Test an in-place setter method.
        mutableDecimal.setUnscaled(123456789012L);
        bh.consume(mutableDecimal);
    }

    @Benchmark
    public void benchmark_clone(Blackhole bh) {
        // Test the clone method.
        MutableDecimal10f cloned = mutableDecimal.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void benchmark_toImmutableDecimal(Blackhole bh) {
        // Test conversion to immutable.
        org.decimal4j.immutable.Decimal10f immutable = mutableDecimal.toImmutableDecimal();
        bh.consume(immutable);
    }

    @Benchmark
    public void benchmark_multiplyExact(Blackhole bh) {
        // Test an arithmetic method that returns a Multipliable object.
        // We call it on the instance, which is mutable.
        mutableDecimal.multiplyExact();
        bh.consume(mutableDecimal);
    }
}
