package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import org.decimal4j.arithmetic.CheckedScaleNfTruncatingArithmetic;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scale5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScaleNfTruncatingArithmeticBenchmark {

    private CheckedScaleNfTruncatingArithmetic arithmetic;
    private ScaleMetrics scaleMetrics;

    // Inputs for arithmetic operations
    private long uDecimal1;
    private long uDecimal2;
    private long uDecimal3;
    private long uDecimal4;
    private long uDecimal5;
    private long uDecimal6;
    private int scale;
    private int exponent;
    private int precision;
    private long lDivisor;
    private int positions;

    // Inputs for conversion/parsing
    private String inputString;
    private BigDecimal inputBigDecimal;
    private long inputUnscaled;
    private int inputScale;

    @Setup(Level.Trial)
    public void setup() {
        // Use a fixed scale for setup consistency
        scaleMetrics = Scale5f.INSTANCE;
        arithmetic = new CheckedScaleNfTruncatingArithmetic(scaleMetrics);

        // Initialize inputs (must not be static final literals)
        uDecimal1 = 1234567890123L;
        uDecimal2 = 9876543210987L;
        uDecimal3 = 500000000L;
        uDecimal4 = 10L;
        uDecimal5 = 2L;
        uDecimal6 = 3L;
        
        scale = 5;
        exponent = 3;
        precision = 2;
        lDivisor = 100L;
        positions = 2;

        inputString = "1234567890";
        inputBigDecimal = new BigDecimal("987.654321");
        inputUnscaled = 12345;
        inputScale = 2;
    }

    // --- Arithmetic Operations ---

    @Benchmark
    public long bench_addUnscaled() {
        return arithmetic.addUnscaled(uDecimal1, uDecimal2, scale);
    }

    @Benchmark
    public long bench_subtractUnscaled() {
        return arithmetic.subtractUnscaled(uDecimal1, uDecimal2, scale);
    }

    @Benchmark
    public long bench_multiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(uDecimal1, uDecimal2, scale);
    }

    @Benchmark
    public long bench_divideByUnscaled() {
        return arithmetic.divideByUnscaled(uDecimal1, uDecimal2, scale);
    }

    @Benchmark
    public long bench_multiply() {
        return arithmetic.multiply(uDecimal1, uDecimal2);
    }

    @Benchmark
    public long bench_square() {
        return arithmetic.square(uDecimal1);
    }

    @Benchmark
    public long bench_divide() {
        return arithmetic.divide(uDecimal1, uDecimal2);
    }

    @Benchmark
    public long bench_pow() {
        return arithmetic.pow(uDecimal1, exponent);
    }

    @Benchmark
    public long bench_avg() {
        return arithmetic.avg(uDecimal1, uDecimal2);
    }

    @Benchmark
    public long bench_sqrt() {
        return arithmetic.sqrt(uDecimal1);
    }

    @Benchmark
    public long bench_divideByLong() {
        return arithmetic.divideByLong(uDecimal1, lDivisor);
    }

    @Benchmark
    public long bench_divideByPowerOf10() {
        return arithmetic.divideByPowerOf10(uDecimal1, positions);
    }

    @Benchmark
    public long bench_invert() {
        return arithmetic.invert(uDecimal1);
    }

    @Benchmark
    public long bench_multiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(uDecimal1, positions);
    }

    @Benchmark
    public long bench_shiftLeft() {
        return arithmetic.shiftLeft(uDecimal1, positions);
    }

    @Benchmark
    public long bench_shiftRight() {
        return arithmetic.shiftRight(uDecimal1, positions);
    }

    @Benchmark
    public long bench_round() {
        return arithmetic.round(uDecimal1, precision);
    }

    // --- Conversion Operations ---

    @Benchmark
    public long bench_fromLong() {
        return arithmetic.fromLong(uDecimal2);
    }

    @Benchmark
    public long bench_fromFloat() {
        return arithmetic.fromFloat((float) uDecimal1);
    }

    @Benchmark
    public long bench_fromDouble() {
        return arithmetic.fromDouble((double) uDecimal1);
    }

    @Benchmark
    public long bench_fromUnscaled() {
        return arithmetic.fromUnscaled(inputUnscaled, inputScale);
    }

    @Benchmark
    public long bench_fromBigDecimal() {
        return arithmetic.fromBigDecimal(inputBigDecimal);
    }

    @Benchmark
    public long bench_toLong() {
        return arithmetic.toLong(uDecimal1);
    }

    @Benchmark
    public float bench_toFloat() {
        return arithmetic.toFloat(uDecimal1);
    }

    @Benchmark
    public double bench_toDouble() {
        return arithmetic.toDouble(uDecimal1);
    }

    @Benchmark
    public long bench_toUnscaled() {
        return arithmetic.toUnscaled(uDecimal1, inputScale);
    }

    @Benchmark
    public long bench_parseString() {
        return arithmetic.parse(inputString);
    }

    @Benchmark
    public long bench_parseCharSequence() {
        // Using a substring of the input string
        return arithmetic.parse(inputString, 0, 5);
    }
}
