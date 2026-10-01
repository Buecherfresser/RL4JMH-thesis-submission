package bench.generated.c070;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.immutable.Decimal11f;
import org.decimal4j.scale.Scale11f;
import org.decimal4j.factory.Factory11f;
import org.decimal4j.exact.Multipliable11f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal11fBenchmark {

    private MutableDecimal11f baseDecimal;
    private MutableDecimal11f operandDecimal;
    private Decimal11f immutableCopy;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize baseDecimal using a complex input (BigDecimal)
        BigDecimal complexValue = new BigDecimal("1234567890.12345678901");
        baseDecimal = new MutableDecimal11f(complexValue);

        // Initialize operandDecimal using a simple input (long)
        operandDecimal = MutableDecimal11f.two();

        // Initialize immutable copy
        immutableCopy = baseDecimal.toImmutableDecimal();
    }

    @Benchmark
    public void benchmark_clone(Blackhole bh) {
        // Clone operation
        MutableDecimal11f cloned = baseDecimal.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void benchmark_toImmutableDecimal(Blackhole bh) {
        // Conversion to immutable type
        Decimal11f immutable = baseDecimal.toImmutableDecimal();
        bh.consume(immutable);
    }

    @Benchmark
    public void benchmark_multiplyExact_setup(Blackhole bh) {
        // Starting the exact multiplication chain
        Multipliable11f multiplier = baseDecimal.multiplyExact();
        bh.consume(multiplier);
    }

    @Benchmark
    public void benchmark_add(Blackhole bh) {
        // Since MutableDecimal11f is mutable, we must clone the base state
        // to ensure the operation is performed on a fresh instance for timing.
        MutableDecimal11f a = baseDecimal.clone();
        MutableDecimal11f b = operandDecimal.clone();

        // Perform in-place addition (assuming standard arithmetic methods exist)
        a.add(b);
        bh.consume(a);
    }

    @Benchmark
    public void benchmark_multiply(Blackhole bh) {
        // Since MutableDecimal11f is mutable, we must clone the base state
        MutableDecimal11f a = baseDecimal.clone();
        MutableDecimal11f b = operandDecimal.clone();

        // Perform in-place multiplication (assuming standard arithmetic methods exist)
        a.multiply(b);
        bh.consume(a);
    }
}
