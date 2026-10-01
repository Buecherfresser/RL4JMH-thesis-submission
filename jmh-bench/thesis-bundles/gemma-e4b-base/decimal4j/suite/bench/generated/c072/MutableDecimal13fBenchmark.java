package bench.generated.c072;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.mutable.MutableDecimal13f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.exact.Multipliable13f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal13fBenchmark {

    private MutableDecimal13f inputA;
    private MutableDecimal13f inputB;
    private String testString;
    private BigDecimal testBigDecimal;
    private long testLong;
    private double testDouble;

    @Setup(Level.Trial)
    public void setup() {
        // Inputs for construction
        testLong = 1234567890123L;
        testDouble = 123.45678901234567;
        testString = "1234567890123.456789012345"; // String with more than 13 fractional digits
        testBigDecimal = new BigDecimal("9876543210987.6543210");

        // Initialize base instances
        inputA = MutableDecimal13f.unscaled(testLong);
        inputB = MutableDecimal13f.unscaled(testLong / 2);
    }

    @Benchmark
    public void benchmark_construction_from_long(Blackhole bh) {
        MutableDecimal13f result = new MutableDecimal13f(testLong);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_construction_from_double(Blackhole bh) {
        MutableDecimal13f result = new MutableDecimal13f(testDouble);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_construction_from_string(Blackhole bh) {
        // This tests the complex parsing and rounding logic
        MutableDecimal13f result = new MutableDecimal13f(testString);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_construction_from_bigdecimal(Blackhole bh) {
        MutableDecimal13f result = new MutableDecimal13f(testBigDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_cloning(Blackhole bh) {
        // Clone the state of inputA
        MutableDecimal13f cloned = inputA.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void benchmark_multiply_exact_start(Blackhole bh) {
        // Test the start of the fluent exact multiplication API
        Multipliable13f multiplier = inputA.multiplyExact();
        // We must consume the result, even if we don't complete the multiplication
        bh.consume(multiplier);
    }

    @Benchmark
    public void benchmark_addition(Blackhole bh) {
        // Since MutableDecimal13f is mutable, we must clone inputs to ensure
        // the operation measures the calculation, not the state mutation side effects
        MutableDecimal13f a = inputA.clone();
        MutableDecimal13f b = inputB.clone();
        
        // Perform addition (mutates 'a')
        a.add(b);
        bh.consume(a);
    }
}
