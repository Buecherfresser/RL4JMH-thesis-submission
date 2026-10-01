package bench.generated.c063;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal5f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal5fBenchmark {

    private Decimal5f inputA;
    private Decimal5f inputB;
    private BigDecimal bigDecimalInput;
    private BigInteger bigIntegerInput;
    private String stringInput;

    @Setup
    public void setup() {
        // Setup two representative Decimal5f instances
        inputA = Decimal5f.valueOf(1234567890123L);
        inputB = Decimal5f.valueOf(9876543210987L);

        // Setup complex inputs for conversion methods
        bigDecimalInput = new BigDecimal("12345.6789012345");
        bigIntegerInput = new BigInteger("9876543210987654321");
        stringInput = "123.45678";
    }

    // --- Value Creation Benchmarks ---

    @Benchmark
    public void valueOfLong(Blackhole bh) {
        // Test valueOf(long)
        Decimal5f result = Decimal5f.valueOf(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDouble(Blackhole bh) {
        // Test valueOf(double)
        Decimal5f result = Decimal5f.valueOf(123.456789);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigDecimal(Blackhole bh) {
        // Test valueOf(BigDecimal)
        Decimal5f result = Decimal5f.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigInteger(Blackhole bh) {
        // Test valueOf(BigInteger)
        Decimal5f result = Decimal5f.valueOf(bigIntegerInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfString(Blackhole bh) {
        // Test valueOf(String)
        Decimal5f result = new Decimal5f(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfStringWithRounding(Blackhole bh) {
        // Test valueOf(String, RoundingMode)
        String complexString = "1.123456"; // Requires rounding
        Decimal5f result = Decimal5f.valueOf(complexString, RoundingMode.HALF_UP);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDecimal(Blackhole bh) {
        // Test valueOf(Decimal<?>)
        Decimal<?> genericDecimal = Decimal5f.valueOf(1.23456789); // Create a temporary Decimal5f
        Decimal5f result = Decimal5f.valueOf(genericDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaledLong(Blackhole bh) {
        // Test valueOfUnscaled(long unscaledValue)
        long unscaled = 1234567890L;
        Decimal5f result = Decimal5f.valueOfUnscaled(unscaled);
        bh.consume(result);
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public void add(Blackhole bh) {
        // Test addition
        Decimal5f result = inputA.add(inputB);
        bh.consume(result);
    }

    @Benchmark
    public void multiply(Blackhole bh) {
        // Test multiplication
        Decimal5f result = inputA.multiply(inputB);
        bh.consume(result);
    }

    @Benchmark
    public void divide(Blackhole bh) {
        // Test division
        Decimal5f result = inputA.divide(inputB);
        bh.consume(result);
    }

    @Benchmark
    public void negate(Blackhole bh) {
        // Test negation
        Decimal5f result = inputA.negate();
        bh.consume(result);
    }

    @Benchmark
    public void square(Blackhole bh) {
        // Test squaring
        Decimal5f result = inputA.square();
        bh.consume(result);
    }

    // --- Conversion and Utility Benchmarks ---

    @Benchmark
    public void doubleValue(Blackhole bh) {
        // Test doubleValue()
        double result = inputA.doubleValue();
        bh.consume(result);
    }

    @Benchmark
    public void BigDecimalValue(Blackhole bh) {
        // Test toBigDecimal()
        BigDecimal result = inputA.toBigDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void StringValue(Blackhole bh) {
        // Test toString()
        String result = inputA.toString();
        bh.consume(result);
    }

    @Benchmark
    public void unscaledValue(Blackhole bh) {
        // Test unscaledValue()
        long result = inputA.unscaledValue();
        bh.consume(result);
    }

    @Benchmark
    public void multiplyExact(Blackhole bh) {
        // Test multiplyExact()
        Multipliable5f multiplier = inputA.multiplyExact();
        // We must consume the result of the fluent call to ensure the operation runs
        multiplier.by(Decimal5f.valueOf(2));
        bh.consume(multiplier);
    }
}
