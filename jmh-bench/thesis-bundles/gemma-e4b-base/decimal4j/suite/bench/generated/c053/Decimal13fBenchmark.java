package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal13f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable13f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal13fBenchmark {

    // --- Inputs prepared in Setup ---

    private String testString;
    private long testLong;
    private float testFloat;
    private double testDouble;
    private BigInteger testBigInteger;
    private BigDecimal testBigDecimal;
    private Decimal<?> testDecimal;
    private long testUnscaledLong;
    private int testScale;
    private RoundingMode testRoundingMode;

    @Setup(Level.Trial)
    public void setup() {
        // 1. String input
        testString = "1234567890123.456789";

        // 2. Long input
        testLong = 9876543210L;

        // 3. Float input
        testFloat = 123.4567f;

        // 4. Double input
        testDouble = 123.45678901234567;

        // 5. BigInteger input
        testBigInteger = BigInteger.valueOf(987654321012345L);

        // 6. BigDecimal input
        testBigDecimal = new BigDecimal("123.456789012345");

        // 7. Decimal<?> input (using a Decimal13f instance)
        testDecimal = Decimal13f.valueOf(123.456789012345);

        // 8. Unscaled long/scale input
        testUnscaledLong = 123456789L;
        testScale = 13;

        // 9. Rounding Mode
        testRoundingMode = RoundingMode.HALF_UP;
    }

    // --- Benchmarks for Constructors/Factory Methods ---

    @Benchmark
    public Decimal13f benchmarkStringConstructor() {
        return Decimal13f.valueOf(testString);
    }

    @Benchmark
    public Decimal13f benchmarkStringConstructorWithRounding() {
        return Decimal13f.valueOf(testString, testRoundingMode);
    }

    @Benchmark
    public Decimal13f benchmarkLongValue() {
        return Decimal13f.valueOf(testLong);
    }

    @Benchmark
    public Decimal13f benchmarkFloatValue() {
        return Decimal13f.valueOf(testFloat);
    }

    @Benchmark
    public Decimal13f benchmarkDoubleValue() {
        return Decimal13f.valueOf(testDouble);
    }

    @Benchmark
    public Decimal13f benchmarkBigIntegerValue() {
        return Decimal13f.valueOf(testBigInteger);
    }

    @Benchmark
    public Decimal13f benchmarkBigDecimalValue() {
        return Decimal13f.valueOf(testBigDecimal);
    }

    @Benchmark
    public Decimal13f benchmarkDecimalValue() {
        return Decimal13f.valueOf(testDecimal);
    }

    @Benchmark
    public Decimal13f benchmarkDecimalValueWithRounding() {
        return Decimal13f.valueOf(testDecimal, testRoundingMode);
    }

    @Benchmark
    public Decimal13f benchmarkUnscaledLongScale() {
        return Decimal13f.valueOfUnscaled(testUnscaledLong, testScale);
    }

    @Benchmark
    public Decimal13f benchmarkUnscaledLongScaleWithRounding() {
        return Decimal13f.valueOfUnscaled(testUnscaledLong, testScale, testRoundingMode);
    }

    // --- Benchmark for Multipliable ---

    @Benchmark
    public Multipliable13f benchmarkMultiplyExact() {
        return Decimal13f.valueOf(10.0).multiplyExact();
    }
}
