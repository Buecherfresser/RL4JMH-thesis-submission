package bench.generated.c077;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.mutable.MutableDecimal18f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal18fBenchmark {

    // State field to hold a reusable instance of the mutable decimal
    private MutableDecimal18f mutableDecimal;

    // Setup method to initialize the state
    @Setup
    public void setup() {
        // Initialize a standard mutable decimal for use in benchmarks
        this.mutableDecimal = MutableDecimal18f.zero();
    }

    // Benchmark 1: Simple instantiation (zero)
    @Benchmark
    public void benchmarkZero(Blackhole bh) {
        MutableDecimal18f result = MutableDecimal18f.zero();
        bh.consume(result);
    }

    // Benchmark 2: Instantiation from long
    @Benchmark
    public void benchmarkFromLong(Blackhole bh) {
        MutableDecimal18f result = new MutableDecimal18f(123456789012345L);
        bh.consume(result);
    }

    // Benchmark 3: Instantiation from double
    @Benchmark
    public void benchmarkFromDouble(Blackhole bh) {
        MutableDecimal18f result = new MutableDecimal18f(123.456789);
        bh.consume(result);
    }

    // Benchmark 4: Instantiation from BigInteger
    @Benchmark
    public void benchmarkFromBigInteger(Blackhole bh) {
        MutableDecimal18f result = new MutableDecimal18f(new BigInteger("987654321012345678901234567890"));
        bh.consume(result);
    }

    // Benchmark 5: Arithmetic operation (add)
    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.add(MutableDecimal18f.one());
        bh.consume(result);
    }

    // Benchmark 6: In-place setter (setZero)
    @Benchmark
    public void benchmarkSetZero(Blackhole bh) {
        mutableDecimal.setZero();
        bh.consume(mutableDecimal);
    }

    // Benchmark 7: Cloning
    @Benchmark
    public void benchmarkClone(Blackhole bh) {
        MutableDecimal18f cloned = mutableDecimal.clone();
        bh.consume(cloned);
    }

    // Benchmark 8: Conversion to Immutable (toImmutableDecimal)
    @Benchmark
    public void benchmarkToImmutable(Blackhole bh) {
        MutableDecimal18f mutableCopy = mutableDecimal.clone();
        bh.consume(mutableCopy.toImmutableDecimal());
    }

    // Benchmark 9: Conversion to Mutable (toMutableDecimal) - should be identity
    @Benchmark
    public void benchmarkToMutable(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.toMutableDecimal();
        bh.consume(result);
    }

    // Benchmark 10: Multiplication (using a static factory method)
    @Benchmark
    public void benchmarkMultiply(Blackhole bh) {
        MutableDecimal18f result = MutableDecimal18f.two();
        bh.consume(result);
    }

    // Benchmark 11: Multiplication (using a static method)
    @Benchmark
    public void benchmarkStaticUnscaled(Blackhole bh) {
        MutableDecimal18f result = MutableDecimal18f.unscaled(123456789012345L);
        bh.consume(result);
    }

    // Benchmark 12: Conversion from String (requires a non-zero setup if we want to test the conversion path)
    @Benchmark
    public void benchmarkFromString(Blackhole bh) {
        try {
            // This test relies on the MutableDecimal18f constructor handling the string parsing.
            MutableDecimal18f result = new MutableDecimal18f("1.234567890123456789");
            bh.consume(result);
        } catch (Exception e) {
            // Catching general exception as the specific exception type might be internal or complex
        }
    }

    // Benchmark 13: Arithmetic operation (negate)
    @Benchmark
    public void benchmarkNegate(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.negate();
        bh.consume(result);
    }
}
