package bench.generated.c082;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal5fBenchmark {

    // State fields for read-only/immutable operations or reusable setup
    private MutableDecimal5f zero;
    private MutableDecimal5f one;
    private MutableDecimal5f ten;
    private MutableDecimal5f hundredth;

    @Setup
    public void setup() {
        // Initialize reusable objects. Since they are static factory methods,
        // they are safe to call multiple times.
        this.zero = MutableDecimal5f.zero();
        this.one = MutableDecimal5f.one();
        this.ten = MutableDecimal5f.ten();
        this.hundredth = MutableDecimal5f.hundredth();
    }

    // --- Benchmarks for Static Factory Methods (Read-only/No mutation) ---

    @Benchmark
    public void benchmarkZero(Blackhole bh) {
        bh.consume(MutableDecimal5f.zero());
    }

    @Benchmark
    public void benchmarkOne(Blackhole bh) {
        bh.consume(MutableDecimal5f.one());
    }

    @Benchmark
    public void benchmarkTen(Blackhole bh) {
        bh.consume(MutableDecimal5f.ten());
    }

    @Benchmark
    public void benchmarkHundredth(Blackhole bh) {
        bh.consume(MutableDecimal5f.hundredth());
    }

    // --- Benchmarks for Mutable Operations (In-place mutation) ---

    @Benchmark
    public MutableDecimal5f benchmarkSetZero(Blackhole bh) {
        // Mutating operation: setZero()
        MutableDecimal5f result = zero.setZero();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal5f benchmarkSetOne(Blackhole bh) {
        // Mutating operation: setOne()
        MutableDecimal5f result = one.setOne();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal5f benchmarkSetUnscaled(Blackhole bh) {
        // Mutating operation: setUnscaled(long)
        MutableDecimal5f result = ten.setUnscaled(123456789L);
        bh.consume(result);
        return result;
    }

    // --- Benchmarks for Conversions (Immutable/Read-only) ---

    @Benchmark
    public void benchmarkToImmutable(Blackhole bh) {
        // Read-only operation: toImmutableDecimal()
        MutableDecimal5f mutableValue = MutableDecimal5f.half();
        bh.consume(mutableValue.toImmutableDecimal());
    }

    // --- Benchmarks for Arithmetic (Mutating) ---

    @Benchmark
    public MutableDecimal5f benchmarkAdd(Blackhole bh) {
        // Mutating operation: add()
        MutableDecimal5f result = zero.add(one);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal5f benchmarkMultiply(Blackhole bh) {
        // Mutating operation: multiply()
        MutableDecimal5f result = zero.multiply(ten);
        bh.consume(result);
        return result;
    }

    // --- Benchmarks for Multipliable Interface (Read-only) ---

    @Benchmark
    public org.decimal4j.exact.Multipliable5f benchmarkMultiplyExact(Blackhole bh) {
        // Read-only operation: multiplyExact()
        org.decimal4j.exact.Multipliable5f multiplier = zero.multiplyExact();
        bh.consume(multiplier);
        return null;
    }

    // --- Benchmarks for Constructors (Testing initialization path) ---

    @Benchmark
    public MutableDecimal5f benchmarkConstructorLong(Blackhole bh) {
        // Testing constructor path with a long
        return new MutableDecimal5f(123456789L);
    }

    @Benchmark
    public MutableDecimal5f benchmarkConstructorDouble(Blackhole bh) {
        // Testing constructor path with a double
        return new MutableDecimal5f(1.23456789);
    }
}
