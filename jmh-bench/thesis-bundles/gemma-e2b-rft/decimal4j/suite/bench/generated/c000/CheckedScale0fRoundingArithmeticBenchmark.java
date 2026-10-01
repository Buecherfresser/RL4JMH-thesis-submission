package bench.generated.c000;

import org.decimal4j.arithmetic.CheckedScale0fRoundingArithmetic;
import org.decimal4j.scale.Scale0f;
import org.decimal4j.truncate.DecimalRounding;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScale0fRoundingArithmeticBenchmark {

    private CheckedScale0fRoundingArithmetic arithmetic;

    // Input data for arithmetic operations
    private long uDecimal1;
    private long uDecimal2;
    private long unscaled1;
    private long unscaled2;
    private int scale;

    // Input data for conversion operations
    private float floatValue;
    private double doubleValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;

    // Input data for parsing
    private String parseString;
    private CharSequence parseCharSequence;
    private int parseStart;
    private int parseEnd;

    @Setup
    public void setup() {
        // Initialize the arithmetic object with a specific rounding mode
        this.arithmetic = new CheckedScale0fRoundingArithmetic(RoundingMode.HALF_UP);
        this.scale = 0;

        // Setup large, non-trivial long values for arithmetic tests
        this.uDecimal1 = 123456789012345L;
        this.uDecimal2 = 987654321098765L;
        this.unscaled1 = 1000000000000000L;
        this.unscaled2 = 500000000000000L;

        // Setup data for conversion tests
        this.floatValue = 3.14159f;
        this.doubleValue = 123456789012345.6789;
        this.bigDecimalValue = new BigDecimal("123456789012345.6789");
        this.stringValue = "123456789012345";

        // Setup data for parsing tests
        this.parseString = "123456789012345";
        this.parseCharSequence = "123456789012345";
        this.parseStart = 0;
        this.parseEnd = this.stringValue.length();
    }

    // --- Arithmetic Benchmarks ---

    public long benchmarkAddUnscaled() {
        return arithmetic.addUnscaled(uDecimal1, unscaled1, scale);
    }

    public long benchmarkSubtractUnscaled() {
        return arithmetic.subtractUnscaled(uDecimal1, unscaled1, scale);
    }

    public long benchmarkMultiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(uDecimal1, unscaled1, scale);
    }

    public long benchmarkDivideByUnscaled() {
        return arithmetic.divideByUnscaled(uDecimal1, unscaled1, scale);
    }

    public long benchmarkDivide() {
        return arithmetic.divide(uDecimal1, uDecimal2);
    }

    public long benchmarkMultiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(uDecimal1, 10);
    }

    public long benchmarkDivideByLong() {
        return arithmetic.divideByLong(uDecimal1, 1000L);
    }

    public long benchmarkDivideByPowerOf10() {
        return arithmetic.divideByPowerOf10(uDecimal1, 10);
    }

    public long benchmarkAvg() {
        return arithmetic.avg(uDecimal1, uDecimal2);
    }

    public long benchmarkInvert() {
        return arithmetic.invert(uDecimal1);
    }

    public long benchmarkSqrt() {
        return arithmetic.sqrt(uDecimal1);
    }

    public long benchmarkPow() {
        return arithmetic.pow(uDecimal1, 3);
    }

    public long benchmarkRound() {
        return arithmetic.round(uDecimal1, 5);
    }

    public long benchmarkShiftLeft() {
        return arithmetic.shiftLeft(uDecimal1, 4);
    }

    public long benchmarkShiftRight() {
        return arithmetic.shiftRight(uDecimal1, 4);
    }

    // --- Conversion Benchmarks ---

    public float benchmarkToFloat() {
        return arithmetic.toFloat(uDecimal1);
    }

    public double benchmarkToDouble() {
        return arithmetic.toDouble(uDecimal1);
    }

    public long benchmarkToUnscaled() {
        return arithmetic.toUnscaled(uDecimal1, 2);
    }

    public long benchmarkFromFloat() {
        return arithmetic.fromFloat(floatValue);
    }

    public long benchmarkFromDouble() {
        return arithmetic.fromDouble(doubleValue);
    }

    public long benchmarkFromUnscaled() {
        return arithmetic.fromUnscaled(unscaled1, 0);
    }

    public long benchmarkFromBigDecimal() {
        return arithmetic.fromBigDecimal(bigDecimalValue);
    }

    // --- Parsing Benchmarks ---

    public long benchmarkParseString() {
        return arithmetic.parse(parseString);
    }

    public long benchmarkParseCharSequence() {
        return arithmetic.parse(parseCharSequence, parseStart, parseEnd);
    }
}
