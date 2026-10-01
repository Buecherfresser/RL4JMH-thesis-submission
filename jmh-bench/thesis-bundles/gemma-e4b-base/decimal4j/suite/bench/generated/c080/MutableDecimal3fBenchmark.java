package bench.generated.c080;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.exact.Multipliable3f;
import java.math.BigDecimal;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal3fBenchmark {

    private MutableDecimal3f baseDecimal;
    private MutableDecimal3f operand2;
    private String testString;
    private double testDouble;
    private long testLong;

    @Setup(Level.Trial)
    public void setup() {
        // Setup base values
        baseDecimal = MutableDecimal3f.ten(); // 0.100
        operand2 = MutableDecimal3f.five(); // 0.005

        // Setup input types
        testString = "123.4567"; // String input for parsing/rounding test
        testDouble = 123.456789; // Double input for rounding test
        testLong = 123456L; // Long input
    }

    // --- Construction Benchmarks ---

    @Benchmark
    public MutableDecimal3f bench_constructFromLong() {
        // Since the constructor is simple, we measure the creation itself.
        // We must create a new instance for each invocation if we want to measure construction time.
        return new MutableDecimal3f(testLong);
    }

    @Benchmark
    public MutableDecimal3f bench_constructFromDouble() {
        // Measures conversion from double, which involves rounding logic.
        return new MutableDecimal3f(testDouble);
    }

    @Benchmark
    public MutableDecimal3f bench_constructFromString() {
        // Measures parsing and rounding from string.
        return new MutableDecimal3f(testString);
    }

    @Benchmark
    public MutableDecimal3f bench_unscaledStaticFactory() {
        // Measures static factory method usage.
        return MutableDecimal3f.unscaled(testLong);
    }

    // --- State and Utility Benchmarks ---

    @Benchmark
    public void bench_getScale(Blackhole bh) {
        // Read operation
        MutableDecimal3f d = baseDecimal;
        bh.consume(d.getScale());
    }

    @Benchmark
    public void bench_getScaleMetrics(Blackhole bh) {
        // Read operation
        MutableDecimal3f d = baseDecimal;
        bh.consume(d.getScaleMetrics());
    }

    @Benchmark
    public Decimal3f bench_toImmutableDecimal() {
        // Conversion operation
        MutableDecimal3f d = baseDecimal;
        return d.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal3f bench_clone() {
        // Cloning operation
        MutableDecimal3f d = baseDecimal;
        return d.clone();
    }

    // --- Arithmetic Benchmarks ---
    // Note: Since MutableDecimal3f is mutable, we must clone the baseDecimal 
    // before each operation to ensure we are measuring the operation itself, 
    // not the state reset/mutation cost.

    @Benchmark
    public MutableDecimal3f bench_add() {
        // Measures addition: baseDecimal + operand2
        MutableDecimal3f a = baseDecimal.clone();
        MutableDecimal3f b = operand2.clone();
        // Assuming standard arithmetic methods exist on AbstractMutableDecimal
        return a.add(b);
    }

    @Benchmark
    public MutableDecimal3f bench_multiply() {
        // Measures multiplication: baseDecimal * operand2
        MutableDecimal3f a = baseDecimal.clone();
        MutableDecimal3f b = operand2.clone();
        // Assuming standard arithmetic methods exist on AbstractMutableDecimal
        return a.multiply(b);
    }

    @Benchmark
    public MutableDecimal3f bench_negate() {
        // Measures negation
        MutableDecimal3f a = baseDecimal.clone();
        return a.negate();
    }

    // --- Specialized Operation Benchmarks ---

    @Benchmark
    public Multipliable3f bench_multiplyExactSetup() {
        // Measures the setup for exact multiplication
        MutableDecimal3f d = baseDecimal.clone();
        return d.multiplyExact();
    }
}
