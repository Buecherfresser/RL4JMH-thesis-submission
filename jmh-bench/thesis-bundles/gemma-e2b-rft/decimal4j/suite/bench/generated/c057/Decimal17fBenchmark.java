package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.scale.Scale17f;
import org.decimal4j.exact.Multipliable17f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
public class Decimal17fBenchmark {

    private Decimal17f setupDecimal;
    private BigDecimal setupBigDecimal;
    private double setupDouble;
    private BigInteger setupBigInteger;
    private Decimal17f zero;
    private Decimal17f one;

    @Setup
    public void setup() {
        // Setup basic constants
        this.zero = Decimal17f.ZERO;
        this.one = Decimal17f.ONE;

        // Setup a representative double value
        this.setupDouble = 123.4567890123456789;

        // Setup a representative BigDecimal value
        // Using a value that requires scale handling
        this.setupBigDecimal = new BigDecimal("123.4567890123456789");

        // Setup a representative BigInteger value
        this.setupBigInteger = new BigInteger("9876543210");

        // Setup a Decimal17f from a string conversion
        try {
            this.setupDecimal = Decimal17f.valueOf("123.4567890123456789");
        } catch (NumberFormatException e) {
            throw new RuntimeException("Setup failed: Could not parse string for Decimal17f", e);
        }
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public void convertFromDouble(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf(setupDouble);
        bh.consume(result);
    }

    @Benchmark
    public void convertFromBigDecimal(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf(setupBigDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void convertFromBigInteger(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf(setupBigInteger);
        bh.consume(result);
    }

    @Benchmark
    public void convertFromString(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf("9876543210.123456789012345678");
        bh.consume(result);
    }

    @Benchmark
    public void convertFromUnscaledLong(Blackhole bh) {
        long unscaledValue = 1234567890123456789L;
        Decimal17f result = Decimal17f.valueOfUnscaled(unscaledValue);
        bh.consume(result);
    }

    @Benchmark
    public void convertFromUnscaledLongWithScale(Blackhole bh) {
        long unscaledValue = 1234567890123456789L;
        int scale = 10;
        Decimal17f result = Decimal17f.valueOfUnscaled(unscaledValue, scale);
        bh.consume(result);
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public void arithmeticAddition(Blackhole bh) {
        Decimal17f result = setupDecimal.add(one);
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticSubtraction(Blackhole bh) {
        Decimal17f result = setupDecimal.subtract(zero);
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticMultiplicationExact(Blackhole bh) {
        // Multiply by a constant (ONE). We consume the returned Multipliable17f object.
        Multipliable17f result = setupDecimal.multiplyExact();
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticDivision(Blackhole bh) {
        // Divide by a constant (TWO)
        Decimal17f result = setupDecimal.divide(Decimal17f.TWO);
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticNegation(Blackhole bh) {
        Decimal17f result = setupDecimal.negate();
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticSquare(Blackhole bh) {
        Decimal17f result = setupDecimal.square();
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticRoundHalfUp(Blackhole bh) {
        // Test rounding functionality using a double conversion
        Decimal17f result = Decimal17f.valueOf(setupDouble, RoundingMode.HALF_UP);
        bh.consume(result);
    }
}
