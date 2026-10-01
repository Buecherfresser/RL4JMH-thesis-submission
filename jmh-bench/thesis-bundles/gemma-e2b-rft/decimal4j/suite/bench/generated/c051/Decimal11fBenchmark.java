package bench.generated.c051;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.immutable.Decimal11f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Decimal11fBenchmark {

    private Decimal11f setupDecimal11f;
    private BigDecimal setupBigDecimal;
    private double setupDouble;
    private String setupString;
    private BigInteger setupBigInteger;

    // Define a complex input value for setup
    private static final String COMPLEX_STRING = "1234567890.1234567890123";
    private static final double COMPLEX_DOUBLE = 1234567890.123456789;
    private static final BigDecimal COMPLEX_BIG_DECIMAL = new BigDecimal("1234567890.1234567890123");
    private static final BigInteger COMPLEX_BIG_INTEGER = new BigInteger("1234567890123");

    @Setup
    public void setup() {
        // Setup Decimal11f from String
        setupDecimal11f = Decimal11f.valueOf(COMPLEX_STRING);

        // Setup BigDecimal
        setupBigDecimal = COMPLEX_BIG_DECIMAL;

        // Setup Double
        setupDouble = COMPLEX_DOUBLE;

        // Setup String
        setupString = COMPLEX_STRING;

        // Setup BigInteger
        setupBigInteger = COMPLEX_BIG_INTEGER;
    }

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        long value = 1234567890L;
        Decimal11f result = Decimal11f.valueOf(value);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(setupDouble);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(setupBigDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfString(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(setupString);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDoubleWithRounding(Blackhole bh) {
        RoundingMode mode = RoundingMode.HALF_UP;
        Decimal11f result = Decimal11f.valueOf(setupDouble, mode);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfBigDecimalWithRounding(Blackhole bh) {
        RoundingMode mode = RoundingMode.HALF_DOWN;
        Decimal11f result = Decimal11f.valueOf(setupBigDecimal, mode);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfUnscaledLong(Blackhole bh) {
        long unscaledValue = 1234567890L;
        Decimal11f result = Decimal11f.valueOfUnscaled(unscaledValue);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfUnscaledLongWithScale(Blackhole bh) {
        long unscaledValue = 1234567890L;
        int scale = 5;
        Decimal11f result = Decimal11f.valueOfUnscaled(unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfUnscaledLongWithRounding(Blackhole bh) {
        long unscaledValue = 1234567890L;
        int scale = 10;
        RoundingMode mode = RoundingMode.CEILING;
        Decimal11f result = Decimal11f.valueOfUnscaled(unscaledValue, scale, mode);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfBigInteger(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(setupBigInteger);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDecimal(Blackhole bh) {
        // Use the setup Decimal11f as the input Decimal<?>
        Decimal<?> inputDecimal = setupDecimal11f;
        Decimal11f result = Decimal11f.valueOf(inputDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiplyExact(Blackhole bh) {
        // Test the creation of the Multipliable11f object
        Decimal11f factor = Decimal11f.ONE;
        org.decimal4j.exact.Multipliable11f multiplier = factor.multiplyExact();
        bh.consume(multiplier);
    }
}
