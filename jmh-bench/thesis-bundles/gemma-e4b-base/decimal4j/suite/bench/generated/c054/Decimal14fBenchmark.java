package bench.generated.c054;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal14f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable14f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal14fBenchmark {

    private long testLong;
    private float testFloat;
    private double testDouble;
    private String testString;
    private BigDecimal testBigDecimal;
    private BigInteger testBigInteger;
    private Decimal14f testDecimal14f;
    private long testUnscaledLong;

    @Setup
    public void setup() {
        // 1. Long input
        testLong = 1234567890123L;

        // 2. Float input
        testFloat = 123.456789f;

        // 3. Double input
        testDouble = 123.45678901234567;

        // 4. String input (complex)
        testString = "12345.67890123456789"; // More than 14 digits after decimal

        // 5. BigDecimal input
        testBigDecimal = new BigDecimal("98765.43210987654321");

        // 6. BigInteger input
        testBigInteger = new BigInteger("1234567890123456789012345");

        // 7. Decimal<?> input (using Decimal14f itself)
        testDecimal14f = Decimal14f.valueOf(123.45678901234567);

        // 8. Unscaled long input
        testUnscaledLong = 987654321L;
    }

    // --- Conversion from Primitives ---

    @Benchmark
    public Decimal14f benchmarkValueOfLong(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOf(testLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfFloat(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOf(testFloat);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfFloatWithRounding(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOf(testFloat, RoundingMode.HALF_DOWN);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfDouble(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOf(testDouble);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOf(testDouble, RoundingMode.CEILING);
        bh.consume(result);
        return result;
    }

    // --- Conversion from Complex Types ---

    @Benchmark
    public Decimal14f benchmarkValueOfBigInteger(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOf(testBigInteger);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOf(testBigDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfBigDecimalWithRounding(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOf(testBigDecimal, RoundingMode.DOWN);
        bh.consume(result);
        return result;
    }

    // --- Conversion from String ---

    @Benchmark
    public Decimal14f benchmarkValueOfString(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOf(testString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfStringWithRounding(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOf(testString, RoundingMode.HALF_UP);
        bh.consume(result);
        return result;
    }

    // --- Conversion from Decimal<?> ---

    @Benchmark
    public Decimal14f benchmarkValueOfDecimal(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOf(testDecimal14f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfDecimalWithRounding(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOf(testDecimal14f, RoundingMode.HALF_EVEN);
        bh.consume(result);
        return result;
    }

    // --- Conversion from Unscaled Long ---

    @Benchmark
    public Decimal14f benchmarkValueOfUnscaledLong(Blackhole bh) {
        Decimal14f result = Decimal14f.valueOfUnscaled(testUnscaledLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfUnscaledLongWithScale(Blackhole bh) {
        // Using scale 5 as a representative non-zero scale
        Decimal14f result = Decimal14f.valueOfUnscaled(testUnscaledLong, 5);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfUnscaledLongWithScaleAndRounding(Blackhole bh) {
        // Using scale 5 and a specific rounding mode
        Decimal14f result = Decimal14f.valueOfUnscaled(testUnscaledLong, 5, RoundingMode.UP);
        bh.consume(result);
        return result;
    }

    // --- Arithmetic/Utility Methods ---

    @Benchmark
    public Multipliable14f benchmarkMultiplyExact(Blackhole bh) {
        Multipliable14f result = Decimal14f.ONE.multiplyExact();
        bh.consume(result);
        return result;
    }
}
