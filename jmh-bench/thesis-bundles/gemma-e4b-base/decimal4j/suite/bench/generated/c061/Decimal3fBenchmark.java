package bench.generated.c061;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal3f;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.exact.Multipliable3f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal3fBenchmark {

    private Decimal3f inputDecimal;
    private String inputString;
    private double inputDouble;
    private BigDecimal inputBigDecimal;
    private long inputLong;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Standard Decimal3f instance
        inputDecimal = Decimal3f.valueOf(123.456);

        // 2. Complex string input for parsing
        inputString = "12345.6789"; // Will be rounded

        // 3. Double input for conversion
        inputDouble = 987.654321;

        // 4. BigDecimal input for conversion
        inputBigDecimal = new BigDecimal("54321.98765");

        // 5. Long input for conversion
        inputLong = 123456789L;
    }

    // --- Construction/Parsing Benchmarks ---

    @Benchmark
    public Decimal3f benchmarkParseString(Blackhole bh) {
        Decimal3f result = new Decimal3f(inputString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal3f benchmarkParseDouble(Blackhole bh) {
        Decimal3f result = Decimal3f.valueOf(inputDouble);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal3f benchmarkParseBigDecimal(Blackhole bh) {
        Decimal3f result = Decimal3f.valueOf(inputBigDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal3f benchmarkParseLong(Blackhole bh) {
        Decimal3f result = Decimal3f.valueOf(inputLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal3f benchmarkUnscaledLong(Blackhole bh) {
        // Using a simple unscaled value
        Decimal3f result = Decimal3f.valueOfUnscaled(12345L);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal3f benchmarkUnscaledLongScale(Blackhole bh) {
        // Using a different scale
        Decimal3f result = Decimal3f.valueOfUnscaled(12345L, 5);
        bh.consume(result);
        return result;
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public Decimal3f benchmarkAdd(Blackhole bh) {
        Decimal3f result = inputDecimal.add(inputDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal3f benchmarkSubtract(Blackhole bh) {
        Decimal3f result = inputDecimal.subtract(inputDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal3f benchmarkMultiply(Blackhole bh) {
        Decimal3f result = inputDecimal.multiply(inputDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal3f benchmarkDivide(Blackhole bh) {
        Decimal3f result = inputDecimal.divide(inputDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal3f benchmarkNegate(Blackhole bh) {
        Decimal3f result = inputDecimal.negate();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal3f benchmarkSquare(Blackhole bh) {
        Decimal3f result = inputDecimal.square();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal3f benchmarkSqrt(Blackhole bh) {
        Decimal3f result = inputDecimal.sqrt();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal3f benchmarkPow(Blackhole bh) {
        // Power of 2
        Decimal3f result = inputDecimal.pow(2);
        bh.consume(result);
        return result;
    }

    // --- Conversion and Accessor Benchmarks ---

    @Benchmark
    public String benchmarkToString(Blackhole bh) {
        String result = inputDecimal.toString();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double benchmarkDoubleValue(Blackhole bh) {
        double result = inputDecimal.doubleValue();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkLongValue(Blackhole bh) {
        long result = inputDecimal.longValue();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkUnscaledValue(Blackhole bh) {
        long result = inputDecimal.unscaledValue();
        bh.consume(result);
        return result;
    }

    // --- Specialized Operations Benchmarks ---

    @Benchmark
    public Multipliable3f benchmarkMultiplyExact(Blackhole bh) {
        Multipliable3f result = inputDecimal.multiplyExact();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal3f benchmarkToMutable(Blackhole bh) {
        MutableDecimal3f result = inputDecimal.toMutableDecimal();
        bh.consume(result);
        return result;
    }
}
