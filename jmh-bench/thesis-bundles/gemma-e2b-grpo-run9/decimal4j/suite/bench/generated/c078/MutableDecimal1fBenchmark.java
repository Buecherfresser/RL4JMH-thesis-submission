package bench.generated.c078;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.mutable.MutableDecimal1f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal1fBenchmark {

    // State field to hold a mutable instance, initialized once per benchmark run
    private MutableDecimal1f mutableDecimal;

    // Setup method to initialize the state
    @Setup
    public void setup() {
        // Initialize a standard mutable decimal for use in benchmarks
        this.mutableDecimal = MutableDecimal1f.zero();
    }

    // --- Benchmarks for Static Factory Methods ---

    @Benchmark
    public void benchmarkZero(Blackhole bh) {
        // Call a static method and consume the result
        bh.consume(MutableDecimal1f.zero());
    }

    @Benchmark
    public void benchmarkOne(Blackhole bh) {
        bh.consume(MutableDecimal1f.one());
    }

    @Benchmark
    public void benchmarkTen(Blackhole bh) {
        bh.consume(MutableDecimal1f.ten());
    }

    @Benchmark
    public void benchmarkHundred(Blackhole bh) {
        bh.consume(MutableDecimal1f.hundred());
    }

    @Benchmark
    public void benchmarkTrillion(Blackhole bh) {
        bh.consume(MutableDecimal1f.trillion());
    }

    // --- Benchmarks for Instance Methods (Mutating/Cloning) ---

    @Benchmark
    public void benchmarkClone(Blackhole bh) {
        // Clone operation
        MutableDecimal1f cloned = mutableDecimal.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void benchmarkSet(Blackhole bh) {
        // Mutating operation (setting a value)
        mutableDecimal.set(10L, null);
        bh.consume(mutableDecimal);
    }

    @Benchmark
    public void benchmarkSetDouble(Blackhole bh) {
        // Mutating operation (setting a double value)
        mutableDecimal.set(1.5, null);
        bh.consume(mutableDecimal);
    }

    // --- Benchmarks for Arithmetic Operations (using the instance state) ---

    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        // Mutating operation (addition)
        mutableDecimal.set(1L, null);
        mutableDecimal.set(1L, null);
        bh.consume(mutableDecimal);
    }

    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        // Operation that returns a Multipliable1f (which involves internal logic)
        mutableDecimal.set(1L, null);
        bh.consume(mutableDecimal.multiplyExact());
    }

    // --- Benchmarks for Conversion/Factory Methods (using the instance state) ---

    @Benchmark
    public void benchmarkToImmutable(Blackhole bh) {
        // Conversion to immutable type
        bh.consume(mutableDecimal.toImmutableDecimal());
    }
}
