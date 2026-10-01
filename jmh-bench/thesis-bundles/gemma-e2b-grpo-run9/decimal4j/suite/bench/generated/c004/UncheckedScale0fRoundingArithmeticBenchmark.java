package bench.generated.c004;

import org.decimal4j.arithmetic.UncheckedScale0fRoundingArithmetic;
import java.math.RoundingMode;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScale0fRoundingArithmeticBenchmark {

    private UncheckedScale0fRoundingArithmetic arithmetic;

    @Setup
    public void setup() {
        // Initialize the arithmetic object. We use HALF_UP rounding mode as a default.
        try {
            this.arithmetic = new UncheckedScale0fRoundingArithmetic(RoundingMode.HALF_UP);
        } catch (Exception e) {
            // Handle potential exceptions during setup if necessary
        }
    }

    @Benchmark
    public void benchmarkAddUnscaled(Blackhole bh) {
        // Test addition. Inputs are arbitrary longs, scale is 0 for simplicity in this test.
        long uDecimal1 = 100L;
        long unscaled1 = 50L;
        long uDecimal2 = 200L;
        long unscaled2 = 10L;
        int scale = 0;

        long result = arithmetic.addUnscaled(uDecimal1, unscaled1, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyByUnscaled(Blackhole bh) {
        // Test multiplication.
        long uDecimal = 10L;
        long unscaled = 5L;
        int scale = 0;

        long result = arithmetic.multiplyByUnscaled(uDecimal, unscaled, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivideByLong(Blackhole bh) {
        // Test division by a long divisor.
        long uDecimalDividend = 1000L;
        long lDivisor = 7L;

        long result = arithmetic.divideByLong(uDecimalDividend, lDivisor);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSqrt(Blackhole bh) {
        // Test square root.
        long uDecimal = 16L;

        long result = arithmetic.sqrt(uDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPow(Blackhole bh) {
        // Test power function.
        long uDecimal = 2L;
        int exponent = 10;

        long result = arithmetic.pow(uDecimal, exponent);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToDouble(Blackhole bh) {
        // Test conversion to double.
        long uDecimal = 123456789L;

        double result = arithmetic.toDouble(uDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkFromDouble(Blackhole bh) {
        // Test conversion from double.
        double inputDouble = 123456789.0;

        long result = arithmetic.fromDouble(inputDouble);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        // Test parsing a string.
        String input = "123456789";

        long result = arithmetic.parse(input);
        bh.consume(result);
    }
}
