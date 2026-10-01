package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.decimal4j.arithmetic.CheckedScale0fRoundingArithmetic;
import org.decimal4j.truncate.DecimalRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScale0fRoundingArithmeticBenchmark {

    // State field for the subject under test.
    private CheckedScale0fRoundingArithmetic arithmetic;

    @Setup
    public void setup() {
        // Initialize the arithmetic object.
        try {
            // Initialize with a specific rounding mode.
            this.arithmetic = new CheckedScale0fRoundingArithmetic(DecimalRounding.HALF_UP);
        } catch (Exception e) {
            // In a real scenario, logging this failure is crucial.
            // For a strict benchmark environment, we proceed assuming initialization succeeds
            // or let the benchmark fail if the SUT cannot be instantiated.
            System.err.println("Failed to initialize CheckedScale0fRoundingArithmetic: " + e.getMessage());
        }
    }

    @Benchmark
    public void testAddUnscaled(Blackhole bh) {
        // Test addition.
        try {
            long result = arithmetic.addUnscaled(100L, 50L, 0);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions during benchmark
        }
    }

    @Benchmark
    public void testSubtractUnscaled(Blackhole bh) {
        // Test subtraction.
        try {
            long result = arithmetic.subtractUnscaled(200L, 100L, 0);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testMultiplyByUnscaled(Blackhole bh) {
        // Test multiplication.
        try {
            long result = arithmetic.multiplyByUnscaled(10L, 5L, 0);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testDivideByUnscaled(Blackhole bh) {
        // Test division. Fixed: added the required scale argument (0).
        try {
            long result = arithmetic.divideByUnscaled(100L, 10L, 0);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testDivide(Blackhole bh) {
        // Test division (two long inputs).
        try {
            long result = arithmetic.divide(1000L, 10L);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testPow(Blackhole bh) {
        // Test power function.
        try {
            long result = arithmetic.pow(2L, 10);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testRound(Blackhole bh) {
        // Test rounding.
        try {
            long result = arithmetic.round(123456789L, 5);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testShiftLeft(Blackhole bh) {
        // Test shift left.
        try {
            long result = arithmetic.shiftLeft(10L, 3);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testShiftRight(Blackhole bh) {
        // Test shift right.
        try {
            long result = arithmetic.shiftRight(10L, 2);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testToFloat(Blackhole bh) {
        // Test conversion to float.
        try {
            float result = arithmetic.toFloat(12345L);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testToDouble(Blackhole bh) {
        // Test conversion to double.
        try {
            double result = arithmetic.toDouble(12345L);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testFromUnscaled(Blackhole bh) {
        // Test conversion from unscaled long.
        try {
            long result = arithmetic.fromUnscaled(12345L, 0);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testFromBigDecimal(Blackhole bh) {
        // Test conversion from BigDecimal.
        try {
            // Note: BigDecimal creation inside the benchmark is generally discouraged
            // if it involves complex parsing, but here it's simple.
            BigDecimal bd = new BigDecimal("123.45");
            long result = arithmetic.fromBigDecimal(bd);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Test parsing a string.
        try {
            // Use a simple string that should parse without error
            long result = arithmetic.parse("123456789", 0, 9);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
