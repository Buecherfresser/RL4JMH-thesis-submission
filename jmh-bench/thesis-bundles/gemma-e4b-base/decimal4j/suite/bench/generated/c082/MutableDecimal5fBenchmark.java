package bench.generated.c082;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal5f;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal5fBenchmark {

    private MutableDecimal5f inputA;
    private MutableDecimal5f inputB;
    private MutableDecimal5f inputC;
    private String testString;
    private BigDecimal testBigDecimal;
    private BigInteger testBigInteger;
    private Decimal5f testDecimal5f;

    @Setup
    public void setup() {
        // 1. Setup inputs for arithmetic operations
        // Use static factory methods for consistent setup
        inputA = MutableDecimal5f.ten(); // 10.00000
        inputB = MutableDecimal5f.half(); // 0.50000
        inputC = MutableDecimal5f.one(); // 1.00000

        // 2. Setup inputs for construction tests
        testString = "123.45678"; // String input
        testBigDecimal = new BigDecimal("987.654321"); // BigDecimal input
        testBigInteger = BigInteger.valueOf(123456789L); // BigInteger input
        testDecimal5f = Decimal5f.valueOf(123.45678); // Decimal5f input
    }

    // --- Construction Benchmarks ---

    @Benchmark
    public void constructFromLong(Blackhole bh) {
        // Use a representative long value
        MutableDecimal5f result = new MutableDecimal5f(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromDouble(Blackhole bh) {
        // Use a representative double value
        MutableDecimal5f result = new MutableDecimal5f(123.456789);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromString(Blackhole bh) {
        // Use the pre-setup string
        MutableDecimal5f result = new MutableDecimal5f(testString);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromBigInteger(Blackhole bh) {
        // Use the pre-setup BigInteger
        MutableDecimal5f result = new MutableDecimal5f(testBigInteger);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromBigDecimal(Blackhole bh) {
        // Use the pre-setup BigDecimal
        MutableDecimal5f result = new MutableDecimal5f(testBigDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromDecimal5f(Blackhole bh) {
        // Use the pre-setup Decimal5f
        MutableDecimal5f result = new MutableDecimal5f(testDecimal5f);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromGenericDecimal(Blackhole bh) {
        // Simulate conversion from a generic Decimal<?>
        // We use inputA as a stand-in for a generic Decimal
        MutableDecimal5f result = new MutableDecimal5f(inputA);
        bh.consume(result);
    }

    @Benchmark
    public void constructZero(Blackhole bh) {
        MutableDecimal5f result = MutableDecimal5f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void constructUnscaled(Blackhole bh) {
        // Test static unscaled factory method
        MutableDecimal5f result = MutableDecimal5f.unscaled(987654321L);
        bh.consume(result);
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public void add(Blackhole bh) {
        // Since MutableDecimal5f is mutable, we must clone inputs to ensure independence
        MutableDecimal5f a = inputA.clone();
        MutableDecimal5f b = inputB.clone();
        
        // a = a + b
        MutableDecimal5f result = a.add(b);
        bh.consume(result);
    }

    @Benchmark
    public void subtract(Blackhole bh) {
        MutableDecimal5f a = inputA.clone();
        MutableDecimal5f b = inputB.clone();
        
        // a = a - b
        MutableDecimal5f result = a.subtract(b);
        bh.consume(result);
    }

    @Benchmark
    public void multiply(Blackhole bh) {
        MutableDecimal5f a = inputA.clone();
        MutableDecimal5f b = inputB.clone();
        
        // a = a * b
        MutableDecimal5f result = a.multiply(b);
        bh.consume(result);
    }

    @Benchmark
    public void divide(Blackhole bh) {
        MutableDecimal5f a = inputA.clone();
        MutableDecimal5f b = inputB.clone();
        
        // a = a / b
        MutableDecimal5f result = a.divide(b);
        bh.consume(result);
    }

    @Benchmark
    public void negate(Blackhole bh) {
        MutableDecimal5f a = inputA.clone();
        
        // a = -a
        MutableDecimal5f result = a.negate();
        bh.consume(result);
    }

    @Benchmark
    public void abs(Blackhole bh) {
        // Use a negative input for meaningful test
        MutableDecimal5f a = MutableDecimal5f.minusOne().clone();
        
        // a = |a|
        MutableDecimal5f result = a.abs();
        bh.consume(result);
    }

    @Benchmark
    public void square(Blackhole bh) {
        MutableDecimal5f a = inputA.clone();
        
        // a = a * a
        MutableDecimal5f result = a.square();
        bh.consume(result);
    }

    @Benchmark
    public void round(Blackhole bh) {
        MutableDecimal5f a = inputA.clone();
        
        // Rounding to 2 decimal places
        MutableDecimal5f result = a.round(2);
        bh.consume(result);
    }

    // --- Utility and Conversion Benchmarks ---

    @Benchmark
    public void clone(Blackhole bh) {
        // Clone the inputA
        MutableDecimal5f result = inputA.clone();
        bh.consume(result);
    }

    @Benchmark
    public void toImmutableDecimal(Blackhole bh) {
        // Convert mutable to immutable
        Decimal5f result = inputA.toImmutableDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void toMutableDecimal(Blackhole bh) {
        // Convert immutable to mutable (should be identity operation)
        MutableDecimal5f result = Decimal5f.valueOf(1.0).toMutableDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void multiplyExactSetup(Blackhole bh) {
        // Setup for exact multiplication
        MutableDecimal5f multiplier = inputA.clone();
        
        // This returns a Multipliable5f object
        org.decimal4j.exact.Multipliable5f result = multiplier.multiplyExact();
        bh.consume(result);
    }
}
