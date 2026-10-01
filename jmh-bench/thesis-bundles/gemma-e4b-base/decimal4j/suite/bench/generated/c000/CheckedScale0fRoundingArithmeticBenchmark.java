package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.arithmetic.CheckedScale0fRoundingArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScale0fRoundingArithmeticBenchmark {

    private CheckedScale0fRoundingArithmetic arithmetic;

    // Inputs for arithmetic operations
    private long uDecimal1;
    private long uDecimal2;
    private long uDecimal3;
    private long uDecimal4;
    private long uDecimalDividend;
    private long uDecimalDivisor;
    private long uDecimalBase;
    private long uDecimalUnscaled;
    private int scale;
    private int precision;
    private int exponent;
    private int shiftAmount;
    private long lDivisor;

    // Inputs for conversions and parsing
    private float fValue;
    private double dValue;
    private BigDecimal bdValue;
    private String sValue;
    private CharSequence csValue;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize SUT with a specific rounding mode
        arithmetic = new CheckedScale0fRoundingArithmetic(RoundingMode.HALF_UP);

        // Initialize common inputs
        uDecimal1 = 1234567890123L;
        uDecimal2 = 9876543210987L;
        uDecimal3 = 500000000L;
        uDecimal4 = 10L;
        uDecimalDividend = 1000000000L;
        uDecimalDivisor = 3L;
        uDecimalBase = 2L;
        uDecimalUnscaled = 12345L;
        scale = 0; // Scale0f
        precision = 5;
        exponent = 3;
        shiftAmount = 4;
        lDivisor = 7L;

        // Conversion inputs
        fValue = 3.14159f;
        dValue = 3.1415926535;
        bdValue = new BigDecimal("123.456");
        sValue = "987654321";
        csValue = "12345";
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public long benchAddUnscaled() {
        return arithmetic.addUnscaled(uDecimal1, uDecimal2, scale);
    }

    @Benchmark
    public long benchSubtractUnscaled() {
        return arithmetic.subtractUnscaled(uDecimal1, uDecimal2, scale);
    }

    @Benchmark
    public long benchMultiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(uDecimal1, uDecimal2, scale);
    }

    @Benchmark
    public long benchDivideByUnscaled() {
        return arithmetic.divideByUnscaled(uDecimal1, uDecimal2, scale);
    }

    @Benchmark
    public long benchDivide() {
        return arithmetic.divide(uDecimalDividend, uDecimalDivisor);
    }

    @Benchmark
    public long benchMultiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(uDecimal1, exponent);
    }

    @Benchmark
    public long benchDivideByLong() {
        return arithmetic.divideByLong(uDecimalDividend, lDivisor);
    }

    @Benchmark
    public long benchDivideByPowerOf10() {
        return arithmetic.divideByPowerOf10(uDecimal1, exponent);
    }

    @Benchmark
    public long benchAvg() {
        return arithmetic.avg(uDecimal1, uDecimal2);
    }

    @Benchmark
    public long benchInvert() {
        return arithmetic.invert(uDecimal1);
    }

    @Benchmark
    public long benchSqrt() {
        return arithmetic.sqrt(uDecimal1);
    }

    @Benchmark
    public long benchPow() {
        return arithmetic.pow(uDecimalBase, exponent);
    }

    @Benchmark
    public long benchRound() {
        return arithmetic.round(uDecimal1, precision);
    }

    @Benchmark
    public long benchShiftLeft() {
        return arithmetic.shiftLeft(uDecimal1, shiftAmount);
    }

    @Benchmark
    public long benchShiftRight() {
        return arithmetic.shiftRight(uDecimal1, shiftAmount);
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public float benchToFloat() {
        return arithmetic.toFloat(uDecimal1);
    }

    @Benchmark
    public double benchToDouble() {
        return arithmetic.toDouble(uDecimal1);
    }

    @Benchmark
    public long benchToUnscaled() {
        return arithmetic.toUnscaled(uDecimal1, scale);
    }

    @Benchmark
    public long benchFromFloat() {
        return arithmetic.fromFloat(fValue);
    }

    @Benchmark
    public long benchFromDouble() {
        return arithmetic.fromDouble(dValue);
    }

    @Benchmark
    public long benchFromUnscaled() {
        return arithmetic.fromUnscaled(uDecimalUnscaled, scale);
    }

    @Benchmark
    public long benchFromBigDecimal() {
        return arithmetic.fromBigDecimal(bdValue);
    }

    @Benchmark
    public long benchParseString() {
        return arithmetic.parse(sValue);
    }

    @Benchmark
    public long benchParseCharSequence() {
        return arithmetic.parse(csValue, 0, csValue.length());
    }
}
