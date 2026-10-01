package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale6f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal6fBenchmark {

    /**
     * Benchmark for converting a long value to Decimal6f.
     * This tests the path that handles simple long values.
     */
    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Use a dynamic long value to avoid FINAL/static final issues
        Decimal6f result = Decimal6f.valueOf(123456789L);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a double value to Decimal6f (default rounding).
     */
    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Use a dynamic double value
        Decimal6f result = Decimal6f.valueOf(3.1415926535);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a BigInteger value to Decimal6f.
     */
    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        // Use a dynamic BigInteger value
        Decimal6f result = Decimal6f.valueOf(new BigInteger("1234567890123456789"));
        bh.consume(result);
    }

    /**
     * Benchmark for converting a BigDecimal value to Decimal6f (default rounding).
     */
    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Use a dynamic BigDecimal value
        BigDecimal bd = new BigDecimal("123.4567890123456789");
        Decimal6f result = Decimal6f.valueOf(bd);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a String value to Decimal6f (default rounding).
     */
    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        // Use a dynamic String value
        Decimal6f result = Decimal6f.valueOf("987654321.123456");
        bh.consume(result);
    }

    /**
     * Benchmark for converting a float value to Decimal6f (default rounding).
     */
    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        // Use a dynamic float value
        Decimal6f result = Decimal6f.valueOf(1.2345f);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a double value with specific rounding mode.
     */
    @Benchmark
    public void benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        // Test a specific rounding mode (e.g., HALF_UP)
        Decimal6f result = Decimal6f.valueOf(1.0 / 3.0, java.math.RoundingMode.HALF_UP);
        bh.consume(result);
    }
}
