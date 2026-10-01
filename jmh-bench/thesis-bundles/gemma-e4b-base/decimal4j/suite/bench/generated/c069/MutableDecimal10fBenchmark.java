package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.immutable.Decimal10f;
import java.math.BigDecimal;
import java.math.BigInteger;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal10fBenchmark {

    private MutableDecimal10f baseDecimal;
    private MutableDecimal10f operand1;
    private MutableDecimal10f operand2;
    private String testString;

    @Setup
    public void setup() {
        // Initialize base decimal (e.g., 1.0)
        baseDecimal = MutableDecimal10f.one();
        
        // Initialize operands for arithmetic
        operand1 = MutableDecimal10f.ten(); // 10.0
        operand2 = MutableDecimal10f.half(); // 0.5

        // Initialize string input
        testString = "1234567890.1234567890";
    }

    // --- Construction Benchmarks ---

    @Benchmark
    public MutableDecimal10f constructFromLong() {
        // Use a large long value to ensure the operation is non-trivial
        long largeValue = 9876543210L;
        return new MutableDecimal10f(largeValue);
    }

    @Benchmark
    public MutableDecimal10f constructFromDouble() {
        // Use a double value that requires rounding to scale 10
        double value = 123.456789012345;
        return new MutableDecimal10f(value);
    }

    @Benchmark
    public MutableDecimal10f constructFromString() {
        // Test parsing a complex string
        return new MutableDecimal10f(testString);
    }

    // --- Arithmetic Benchmarks (Mutating Operations) ---

    @Benchmark
    public MutableDecimal10f add() {
        // Since MutableDecimal10f is mutable, we must clone the baseDecimal 
        // before modification to ensure the benchmark is isolated.
        MutableDecimal10f result = baseDecimal.clone();
        return result.add(operand1);
    }

    @Benchmark
    public MutableDecimal10f multiply() {
        MutableDecimal10f result = baseDecimal.clone();
        return result.multiply(operand2);
    }

    @Benchmark
    public MutableDecimal10f negate() {
        MutableDecimal10f result = baseDecimal.clone();
        return result.negate();
    }

    // --- State Mutation and Accessor Benchmarks ---

    @Benchmark
    public MutableDecimal10f setUnscaled() {
        // Test direct mutation of the internal state
        MutableDecimal10f result = baseDecimal.clone();
        long newUnscaled = 5000000000L;
        result.setUnscaled(newUnscaled);
        return result;
    }

    @Benchmark
    public MutableDecimal10f clone() {
        // Test the cloning operation
        return baseDecimal.clone();
    }

    @Benchmark
    public long getUnscaledValue() {
        // Test reading the internal state
        return baseDecimal.unscaledValue();
    }

    @Benchmark
    public Decimal10f toImmutableDecimal() {
        // Test conversion to the immutable counterpart
        return baseDecimal.toImmutableDecimal();
    }
}
