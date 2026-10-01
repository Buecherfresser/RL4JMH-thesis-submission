package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.mutable.MutableDecimal12f;
import org.decimal4j.exact.Multipliable12f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal12fBenchmark {

    // State field for the mutable decimal instance.
    private MutableDecimal12f mutableDecimal;

    // Helper method to initialize a mutable decimal for use in benchmarks
    private MutableDecimal12f setupMutableDecimal() {
        // Using a simple constructor for setup, as it initializes to zero.
        return MutableDecimal12f.zero();
    }

    @Setup
    public void setup() {
        // Initialize a mutable decimal instance once for use across benchmarks
        this.mutableDecimal = setupMutableDecimal();
    }

    // --- Benchmarks for Static Factory Methods ---

    @Benchmark
    public void benchmarkZero(Blackhole bh) {
        MutableDecimal12f result = MutableDecimal12f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkOne(Blackhole bh) {
        MutableDecimal12f result = MutableDecimal12f.one();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkTen(Blackhole bh) {
        MutableDecimal12f result = MutableDecimal12f.ten();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkHalf(Blackhole bh) {
        MutableDecimal12f result = MutableDecimal12f.half();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkTrillionth(Blackhole bh) {
        MutableDecimal12f result = MutableDecimal12f.trillionth();
        bh.consume(result);
    }

    // --- Benchmarks for Static Utility Methods ---

    @Benchmark
    public void benchmarkUnscaled(Blackhole bh) {
        // Test unscaled factory method
        MutableDecimal12f result = MutableDecimal12f.unscaled(123456789012L);
        bh.consume(result);
    }

    // --- Benchmarks for Instance Methods (Mutating/Cloning) ---

    @Benchmark
    public void benchmarkClone(Blackhole bh) {
        // Test clone method
        MutableDecimal12f cloned = this.mutableDecimal.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void benchmarkToMutableDecimal(Blackhole bh) {
        // Test toMutableDecimal method (should return self)
        MutableDecimal12f result = this.mutableDecimal.toMutableDecimal();
        bh.consume(result);
    }

    // --- Benchmarks for Conversion Methods (Requires input creation) ---

    @Benchmark
    public void benchmarkFromLong(Blackhole bh) {
        // Test constructor from long
        MutableDecimal12f result = new MutableDecimal12f(123456789012L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromDouble(Blackhole bh) {
        // Test constructor from double
        MutableDecimal12f result = new MutableDecimal12f(123.456789012);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromBigDecimal(Blackhole bh) {
        // Test constructor from BigDecimal
        MutableDecimal12f result = new MutableDecimal12f(new java.math.BigDecimal("123.456789012"));
        bh.consume(result);
    }

    // --- Benchmarks for Arithmetic/Multiplication (Requires instance state) ---

    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        // Test multiplyExact (returns Multipliable12f, which we consume)
        // FIX: The method returns Multipliable12f, not MutableDecimal12f.
        this.mutableDecimal.multiplyExact();
    }
}
