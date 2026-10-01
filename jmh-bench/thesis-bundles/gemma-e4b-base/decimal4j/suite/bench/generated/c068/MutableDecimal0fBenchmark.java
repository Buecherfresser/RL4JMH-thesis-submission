package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.immutable.Decimal0f;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable0f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal0fBenchmark {

    private MutableDecimal0f a;
    private MutableDecimal0f b;

    // Inputs for construction tests
    private long longInput;
    private double doubleInput;
    private String stringInput;
    private BigInteger bigIntegerInput;
    private BigDecimal bigDecimalInput;
    private Decimal0f decimal0fInput;
    private Decimal<?> genericDecimalInput;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Initialize inputs once per trial
        longInput = 1234567890123L;
        doubleInput = 3.1415926535;
        stringInput = "987654321";
        bigIntegerInput = new BigInteger("12345678901234567890");
        bigDecimalInput = new BigDecimal("987654321.12345");
        decimal0fInput = Decimal0f.valueOf(12345L);
        
        // Create a generic decimal instance for testing the constructor
        // We use a simple Decimal0f instance as a stand-in for Decimal<?>
        genericDecimalInput = decimal0fInput; 
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Initialize mutable instances for arithmetic tests before each invocation
        // We use static factory methods for clean, predictable state
        a = MutableDecimal0f.one();
        b = MutableDecimal0f.two();
    }

    // --- Construction Benchmarks ---

    @Benchmark
    public MutableDecimal0f constructFromLong() {
        // Since the constructor mutates 'this', we must create a new instance for each run
        return new MutableDecimal0f(longInput);
    }

    @Benchmark
    public MutableDecimal0f constructFromDouble() {
        return new MutableDecimal0f(doubleInput);
    }

    @Benchmark
    public MutableDecimal0f constructFromString() {
        return new MutableDecimal0f(stringInput);
    }

    @Benchmark
    public MutableDecimal0f constructFromBigInteger() {
        return new MutableDecimal0f(bigIntegerInput);
    }

    @Benchmark
    public MutableDecimal0f constructFromBigDecimal() {
        return new MutableDecimal0f(bigDecimalInput);
    }

    @Benchmark
    public MutableDecimal0f constructFromDecimal0f() {
        return new MutableDecimal0f(decimal0fInput);
    }

    @Benchmark
    public MutableDecimal0f constructFromGenericDecimal() {
        return new MutableDecimal0f(genericDecimalInput);
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public MutableDecimal0f add() {
        // a.add(b) modifies a and returns a
        return a.add(b);
    }

    @Benchmark
    public MutableDecimal0f subtract() {
        // a.subtract(b) modifies a and returns a
        return a.subtract(b);
    }

    @Benchmark
    public MutableDecimal0f multiply() {
        // a.multiply(b) modifies a and returns a
        return a.multiply(b);
    }

    @Benchmark
    public MutableDecimal0f divide() {
        // a.divide(b) modifies a and returns a
        return a.divide(b);
    }

    @Benchmark
    public MutableDecimal0f negate() {
        // a.negate() modifies a and returns a
        return a.negate();
    }

    @Benchmark
    public MutableDecimal0f square() {
        // a.square() modifies a and returns a
        return a.square();
    }

    // --- Utility Benchmarks ---

    @Benchmark
    public MutableDecimal0f clone() {
        return a.clone();
    }

    @Benchmark
    public Decimal0f toImmutableDecimal() {
        return a.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal0f toMutableDecimal() {
        return a.toMutableDecimal();
    }

    @Benchmark
    public Multipliable0f multiplyExact() {
        return a.multiplyExact();
    }
}
