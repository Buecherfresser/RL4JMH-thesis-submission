package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.decimal4j.arithmetic.UncheckedScale0fRoundingArithmetic;
import org.decimal4j.scale.Scale0f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScale0fRoundingArithmeticBenchmark {

    private UncheckedScale0fRoundingArithmetic arithmetic;

    // Input data prepared in @Setup
    private long uDecimal1;
    private long uDecimal2;
    private long unscaledValue;
    private int scale;
    private float floatValue;
    private double doubleValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;

    @Setup
    public void setup() {
        // Initialize the arithmetic object with a specific rounding mode (e.g., HALF_UP)
        this.arithmetic = new UncheckedScale0fRoundingArithmetic(RoundingMode.HALF_UP);

        // Prepare long inputs
        this.uDecimal1 = 123456789012345L;
        this.uDecimal2 = 987654321098765L;
        this.unscaledValue = 12345L;
        this.scale = 5;

        // Prepare float/double inputs
        this.floatValue = 3.14159f;
        this.doubleValue = 123456789012345.6789;

        // Prepare BigDecimal input
        this.bigDecimalValue = new BigDecimal("123456789012345.6789");

        // Prepare String input
        this.stringValue = "123456789012345";
    }

    // --- Arithmetic Operations (Longs) ---

    @Benchmark
    public void addUnscaled(Blackhole bh) {
        long result = arithmetic.addUnscaled(uDecimal1, unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void subtractUnscaled(Blackhole bh) {
        long result = arithmetic.subtractUnscaled(uDecimal1, unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByUnscaled(Blackhole bh) {
        long result = arithmetic.multiplyByUnscaled(uDecimal1, unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void divide(Blackhole bh) {
        long result = arithmetic.divide(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void divideByLong(Blackhole bh) {
        long result = arithmetic.divideByLong(uDecimal1, 1000L);
        bh.consume(result);
    }

    @Benchmark
    public void divideByUnscaled(Blackhole bh) {
        long result = arithmetic.divideByUnscaled(uDecimal1, unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void avg(Blackhole bh) {
        long result = arithmetic.avg(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void invert(Blackhole bh) {
        long result = arithmetic.invert(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void shiftLeft(Blackhole bh) {
        long result = arithmetic.shiftLeft(uDecimal1, 5);
        bh.consume(result);
    }

    @Benchmark
    public void shiftRight(Blackhole bh) {
        long result = arithmetic.shiftRight(uDecimal1, 5);
        bh.consume(result);
    }

    @Benchmark
    public void divideByPowerOf10(Blackhole bh) {
        long result = arithmetic.divideByPowerOf10(uDecimal1, 3);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByPowerOf10(Blackhole bh) {
        long result = arithmetic.multiplyByPowerOf10(uDecimal1, 2);
        bh.consume(result);
    }

    @Benchmark
    public void sqrt(Blackhole bh) {
        long result = arithmetic.sqrt(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void pow(Blackhole bh) {
        long result = arithmetic.pow(uDecimal1, 3);
        bh.consume(result);
    }

    @Benchmark
    public void round(Blackhole bh) {
        long result = arithmetic.round(uDecimal1, 10);
        bh.consume(result);
    }

    // --- Conversion Operations ---

    @Benchmark
    public void toFloat(Blackhole bh) {
        float result = arithmetic.toFloat(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void toDouble(Blackhole bh) {
        double result = arithmetic.toDouble(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void fromFloat(Blackhole bh) {
        long result = arithmetic.fromFloat(floatValue);
        bh.consume(result);
    }

    @Benchmark
    public void fromDouble(Blackhole bh) {
        long result = arithmetic.fromDouble(doubleValue);
        bh.consume(result);
    }

    @Benchmark
    public void fromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(bigDecimalValue);
        bh.consume(result);
    }

    @Benchmark
    public void toUnscaled(Blackhole bh) {
        long result = arithmetic.toUnscaled(uDecimal1, scale);
        bh.consume(result);
    }

    @Benchmark
    public void fromUnscaled(Blackhole bh) {
        long result = arithmetic.fromUnscaled(unscaledValue, scale);
        bh.consume(result);
    }

    // --- Parsing Operations ---

    @Benchmark
    public void parseString(Blackhole bh) {
        long result = arithmetic.parse(stringValue);
        bh.consume(result);
    }

    @Benchmark
    public void parseCharSequence(Blackhole bh) {
        // Benchmark a substring operation
        long result = arithmetic.parse(stringValue.substring(0, 10));
        bh.consume(result);
    }
}
