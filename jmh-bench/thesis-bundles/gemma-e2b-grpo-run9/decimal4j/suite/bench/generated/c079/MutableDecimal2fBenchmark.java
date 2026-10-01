package bench.generated.c079;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.immutable.Decimal2f;
import java.math.BigDecimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal2fBenchmark {

    // State field for the mutable decimal instance.
    private MutableDecimal2f mutableDecimal;

    @Setup
    public void setup() {
        // Initialize a mutable decimal instance.
        this.mutableDecimal = MutableDecimal2f.zero();
    }

    @Benchmark
    public void benchmarkZero(Blackhole bh) {
        // Test a static factory method
        MutableDecimal2f result = MutableDecimal2f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkOne(Blackhole bh) {
        // Test a static factory method
        MutableDecimal2f result = MutableDecimal2f.one();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkUnscaled(Blackhole bh) {
        // Test a static factory method
        MutableDecimal2f result = MutableDecimal2f.unscaled(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSet(Blackhole bh) {
        // Test a mutating method (requires an instance)
        mutableDecimal.set(100L);
        bh.consume(mutableDecimal);
    }

    @Benchmark
    public void benchmarkSetZero(Blackhole bh) {
        // Test a mutating method
        mutableDecimal.setZero();
        bh.consume(mutableDecimal);
    }

    @Benchmark
    public void benchmarkToImmutable(Blackhole bh) {
        // Test conversion to immutable. The method returns Decimal2f.
        Decimal2f immutable = mutableDecimal.toImmutableDecimal();
        bh.consume(immutable);
    }

    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        // Test a method that returns a Multipliable2f. Consume the result.
        bh.consume(mutableDecimal.multiplyExact());
    }

    @Benchmark
    public void benchmarkClone(Blackhole bh) {
        // Test clone operation
        MutableDecimal2f cloned = mutableDecimal.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void benchmarkConstructorFromLong(Blackhole bh) {
        // Test constructor that takes a long
        MutableDecimal2f result = new MutableDecimal2f(12345L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConstructorFromDouble(Blackhole bh) {
        // Test constructor that takes a double
        MutableDecimal2f result = new MutableDecimal2f(123.45);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConstructorFromBigDecimal(Blackhole bh) {
        // Test constructor that takes a BigDecimal
        MutableDecimal2f result = new MutableDecimal2f(new java.math.BigDecimal("123.45"));
        bh.consume(result);
    }
}
