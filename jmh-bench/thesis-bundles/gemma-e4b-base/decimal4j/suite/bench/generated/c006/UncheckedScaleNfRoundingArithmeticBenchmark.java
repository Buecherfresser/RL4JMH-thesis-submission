package bench.generated.c006;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.arithmetic.UncheckedScaleNfRoundingArithmetic;
import org.decimal4j.scale.Scales;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScaleNfRoundingArithmeticBenchmark {

    private UncheckedScaleNfRoundingArithmetic arithmetic;

    // Inputs for arithmetic operations
    private long uDecimal1;
    private long uDecimal2;
    private long unscaled;
    private int scale;
    private int exponent;
    private int positions;

    // Inputs for conversion/parsing
    private float valueF;
    private double valueD;
    private BigDecimal valueBD;
    private String valueS;
    private CharSequence valueC;
    private long longValue;

    @Setup(Level.Trial)
    public void setup() {
        // Setup ScaleMetrics for Scale 5
        org.decimal4j.scale.ScaleMetrics scaleMetrics = Scales.getScaleMetrics(5);
        
        // Initialize the subject under test
        arithmetic = new UncheckedScaleNfRoundingArithmetic(scaleMetrics, RoundingMode.HALF_UP);

        // Setup representative inputs
        uDecimal1 = 1234567890123L;
        uDecimal2 = 9876543210987L;
        unscaled = 5000L;
        scale = 5;
        exponent = 3;
        positions = 2;
        
        longValue = 42L;

        valueF = 123.45f;
        valueD = 123.456789;
        valueBD = new BigDecimal("123.456789");
        valueS = "123456789";
        valueC = "123456789";
        
        // Ensure inputs are not compile-time constants
        // (They are initialized in setup, satisfying the rule)
    }

    // --- Arithmetic Operations ---

    @Benchmark
    public long testAddUnscaled(Blackhole bh) {
        long result = arithmetic.addUnscaled(uDecimal1, unscaled, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testSubtractUnscaled(Blackhole bh) {
        long result = arithmetic.subtractUnscaled(uDecimal1, unscaled, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testAvg(Blackhole bh) {
        long result = arithmetic.avg(uDecimal1, uDecimal2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testMultiply(Blackhole bh) {
        long result = arithmetic.multiply(uDecimal1, uDecimal2);
        bh.consume(result);
        return result;
    }
    
    @Benchmark
    public long testMultiplyByUnscaled(Blackhole bh) {
        long result = arithmetic.multiplyByUnscaled(uDecimal1, unscaled, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testSquare(Blackhole bh) {
        long result = arithmetic.square(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testSqrt(Blackhole bh) {
        long result = arithmetic.sqrt(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testDivideByLong(Blackhole bh) {
        long result = arithmetic.divideByLong(uDecimal1, 10L);
        bh.consume(result);
        return result;
    }
    
    @Benchmark
    public long testDivideByUnscaled(Blackhole bh) {
        long result = arithmetic.divideByUnscaled(uDecimal1, unscaled, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testDivide(Blackhole bh) {
        long result = arithmetic.divide(uDecimal1, uDecimal2);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testInvert(Blackhole bh) {
        long result = arithmetic.invert(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testPow(Blackhole bh) {
        long result = arithmetic.pow(uDecimal1, exponent);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testShiftLeft(Blackhole bh) {
        long result = arithmetic.shiftLeft(uDecimal1, positions);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testShiftRight(Blackhole bh) {
        long result = arithmetic.shiftRight(uDecimal1, positions);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testMultiplyByPowerOf10(Blackhole bh) {
        long result = arithmetic.multiplyByPowerOf10(uDecimal1, exponent);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testDivideByPowerOf10(Blackhole bh) {
        long result = arithmetic.divideByPowerOf10(uDecimal1, exponent);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testRound(Blackhole bh) {
        long result = arithmetic.round(uDecimal1, 3);
        bh.consume(result);
        return result;
    }

    // --- Conversion and Parsing Operations ---

    @Benchmark
    public long testFromLong(Blackhole bh) {
        long result = arithmetic.fromLong(longValue);
        bh.consume(result);
        return result;
    }
    
    @Benchmark
    public long testFromUnscaled(Blackhole bh) {
        long result = arithmetic.fromUnscaled(unscaled, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromFloat(Blackhole bh) {
        long result = arithmetic.fromFloat(valueF);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromDouble(Blackhole bh) {
        long result = arithmetic.fromDouble(valueD);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testFromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(valueBD);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testToLong(Blackhole bh) {
        long result = arithmetic.toLong(uDecimal1);
        bh.consume(result);
        return result;
    }
    
    @Benchmark
    public long testToUnscaled(Blackhole bh) {
        long result = arithmetic.toUnscaled(uDecimal1, scale);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public float testToFloat(Blackhole bh) {
        float result = arithmetic.toFloat(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public double testToDouble(Blackhole bh) {
        double result = arithmetic.toDouble(uDecimal1);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long testParseString(Blackhole bh) {
        long result = arithmetic.parse(valueS);
        bh.consume(result);
        return result;
    }
    
    @Benchmark
    public long testParseCharSequence(Blackhole bh) {
        long result = arithmetic.parse(valueC, 0, valueC.length());
        bh.consume(result);
        return result;
    }
}
