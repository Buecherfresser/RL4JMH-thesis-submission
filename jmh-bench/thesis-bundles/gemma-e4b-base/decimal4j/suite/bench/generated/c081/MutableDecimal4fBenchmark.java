package bench.generated.c081;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.scale.Scale4f;
import org.decimal4j.factory.Factory4f;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable4f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal4fBenchmark {

    private MutableDecimal4f a;
    private MutableDecimal4f b;
    private MutableDecimal4f c; // Used for results
    private String inputString;
    private double inputDouble;
    private BigInteger inputBigInt;
    private BigDecimal inputBigDecimal;
    private Decimal4f inputImmutable;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize inputs for arithmetic operations
        a = MutableDecimal4f.two(); // 2.0000
        b = MutableDecimal4f.three(); // 3.0000
        c = MutableDecimal4f.zero();

        // Initialize complex inputs
        inputString = "123.4567";
        inputDouble = 123.456789;
        inputBigInt = new BigInteger("987654321012345");
        inputBigDecimal = new BigDecimal("987654321012345.6789");
        inputImmutable = Decimal4f.valueOf(123.4567);
    }

    // --- Construction Benchmarks ---

    @Benchmark
    public MutableDecimal4f constructFromLong() {
        // Test construction from long
        return new MutableDecimal4f(1234567L);
    }

    @Benchmark
    public MutableDecimal4f constructFromString() {
        // Test construction from String
        return new MutableDecimal4f(inputString);
    }

    @Benchmark
    public MutableDecimal4f constructFromDouble() {
        // Test construction from double
        return new MutableDecimal4f(inputDouble);
    }

    @Benchmark
    public MutableDecimal4f constructFromBigInteger() {
        // Test construction from BigInteger
        return new MutableDecimal4f(inputBigInt);
    }

    @Benchmark
    public MutableDecimal4f constructFromBigDecimal() {
        // Test construction from BigDecimal
        return new MutableDecimal4f(inputBigDecimal);
    }

    @Benchmark
    public MutableDecimal4f constructFromImmutableDecimal() {
        // Test construction from Decimal4f
        return new MutableDecimal4f(inputImmutable);
    }

    @Benchmark
    public MutableDecimal4f constructFromGenericDecimal() {
        // Test construction from generic Decimal<?>
        Decimal<Scale4f> genericDecimal = Decimal4f.valueOf(123.4567);
        return new MutableDecimal4f(genericDecimal);
    }

    @Benchmark
    public MutableDecimal4f cloneInstance() {
        // Test cloning
        return a.clone();
    }

    // --- Accessor Benchmarks ---

    @Benchmark
    public Scale4f getScaleMetrics() {
        return a.getScaleMetrics();
    }

    @Benchmark
    public int getScale() {
        return a.getScale();
    }

    @Benchmark
    public Factory4f getFactory() {
        return a.getFactory();
    }

    // --- Arithmetic Benchmarks (Mutable Operations) ---

    @Benchmark
    public MutableDecimal4f add() {
        // a = a + b
        a.add(b);
        return a;
    }

    @Benchmark
    public MutableDecimal4f subtract() {
        // a = a - b
        a.subtract(b);
        return a;
    }

    @Benchmark
    public MutableDecimal4f multiply() {
        // a = a * b
        a.multiply(b);
        return a;
    }

    @Benchmark
    public MutableDecimal4f negate() {
        // a = -a
        a.negate();
        return a;
    }

    @Benchmark
    public MutableDecimal4f abs() {
        // a = |a|
        a.abs();
        return a;
    }

    @Benchmark
    public MutableDecimal4f square() {
        // a = a * a
        a.square();
        return a;
    }

    @Benchmark
    public MutableDecimal4f shiftLeft() {
        // a = a * 10^n (n=2)
        a.shiftLeft(2);
        return a;
    }

    @Benchmark
    public MutableDecimal4f shiftRight() {
        // a = a / 10^n (n=2)
        a.shiftRight(2);
        return a;
    }

    // --- Conversion and Utility Benchmarks ---

    @Benchmark
    public Decimal4f toImmutableDecimal() {
        return a.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal4f toMutableDecimal() {
        return a.toMutableDecimal();
    }

    @Benchmark
    public Multipliable4f multiplyExact() {
        return a.multiplyExact();
    }
}
