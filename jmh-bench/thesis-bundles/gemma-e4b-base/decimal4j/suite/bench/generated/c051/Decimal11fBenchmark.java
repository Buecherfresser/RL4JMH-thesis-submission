package bench.generated.c051;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal11f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable11f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal11fBenchmark {

    private long largeLong;
    private float testFloat;
    private double testDouble;
    private String testString;
    private BigInteger testBigInteger;
    private BigDecimal testBigDecimal;
    private Decimal<?> testDecimal;
    private Decimal<?> otherScaleDecimal;

    @Setup(Level.Trial)
    public void setup() {
        // Inputs for long
        largeLong = Long.MAX_VALUE / 2;
        
        // Inputs for float
        testFloat = 123.4567f;
        
        // Inputs for double
        testDouble = 987.654321098765;
        
        // Inputs for String
        testString = "12345678901.1234567890123"; // More than 11 fraction digits
        
        // Inputs for BigInteger
        testBigInteger = new BigInteger("12345678901234567890");
        
        // Inputs for BigDecimal
        testBigDecimal = new BigDecimal("9876543210.1234567890123");
        
        // Inputs for Decimal<?>
        testDecimal = Decimal11f.valueOf(123L);
        
        // Input for Decimal<?> (Fixed: using Decimal11f.valueOf instead of illegal Decimal.valueOf)
        otherScaleDecimal = Decimal11f.valueOf(new BigDecimal("1.2345"));
    }

    @Benchmark
    public Decimal11f benchmarkValueOfLong(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(largeLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfFloatDefault(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(testFloat);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfFloatRoundingMode(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(testFloat, RoundingMode.HALF_DOWN);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfDoubleDefault(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(testDouble);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfDoubleRoundingMode(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(testDouble, RoundingMode.CEILING);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfStringDefault(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(testString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfStringRoundingMode(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(testString, RoundingMode.DOWN);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfBigInteger(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(testBigInteger);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfBigDecimalDefault(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(testBigDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfBigDecimalRoundingMode(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(testBigDecimal, RoundingMode.UP);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfDecimalDefault(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(testDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfDecimalRoundingMode(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(otherScaleDecimal, RoundingMode.HALF_EVEN);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfUnscaledLong(Blackhole bh) {
        // Test case where scale is different from 11
        long unscaled = 1234567890123L;
        int scale = 5;
        Decimal11f result = Decimal11f.valueOfUnscaled(unscaled, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal11f benchmarkValueOfUnscaledLongRoundingMode(Blackhole bh) {
        // Test case where scale is different from 11 and rounding is applied
        long unscaled = 1234567890123L;
        int scale = 15;
        Decimal11f result = Decimal11f.valueOfUnscaled(unscaled, scale, RoundingMode.HALF_UP);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Multipliable11f benchmarkMultiplyExact(Blackhole bh) {
        // Test the fluent multiplication start
        Multipliable11f multiplier = Decimal11f.ONE.multiplyExact();
        bh.consume(multiplier);
        return multiplier;
    }
}
