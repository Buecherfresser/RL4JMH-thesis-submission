package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal0f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal0fBenchmark {

    // --- Benchmarks for Static Factory Methods (Stateless Operations) ---

    @Benchmark
    public void benchmark_zero(Blackhole bh) {
        MutableDecimal0f result = MutableDecimal0f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_one(Blackhole bh) {
        MutableDecimal0f result = MutableDecimal0f.one();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_ten(Blackhole bh) {
        MutableDecimal0f result = MutableDecimal0f.ten();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_billion(Blackhole bh) {
        MutableDecimal0f result = MutableDecimal0f.billion();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_trillion(Blackhole bh) {
        MutableDecimal0f result = MutableDecimal0f.trillion();
        bh.consume(result);
    }

    // --- Benchmarks for Construction (Stateless) ---

    @Benchmark
    public void benchmark_fromLong(Blackhole bh) {
        // Using a large long value to test bounds/conversion logic
        MutableDecimal0f result = new MutableDecimal0f(1234567890123L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_fromBigInteger(Blackhole bh) {
        // Using a large BigInteger value
        MutableDecimal0f result = new MutableDecimal0f(new java.math.BigInteger("9876543210987654321"));
        bh.consume(result);
    }

    // --- Benchmarks for Arithmetic (Instance Operations) ---

    @Benchmark
    public void benchmark_multiplyExact(Blackhole bh) {
        // This method returns a Multipliable0f, which we consume.
        MutableDecimal0f a = MutableDecimal0f.one();
        a.multiplyExact();
        bh.consume(a);
    }

    @Benchmark
    public void benchmark_clone(Blackhole bh) {
        // Cloning is a common operation to test.
        MutableDecimal0f original = MutableDecimal0f.ten();
        MutableDecimal0f clone = original.clone();
        bh.consume(clone);
    }

    // --- Benchmarks for Immutable Conversion ---

    @Benchmark
    public void benchmark_toImmutable(Blackhole bh) {
        MutableDecimal0f mutableVal = MutableDecimal0f.four();
        // This operation should be fast as it relies on static factory methods.
        bh.consume(mutableVal.toImmutableDecimal());
    }
}
