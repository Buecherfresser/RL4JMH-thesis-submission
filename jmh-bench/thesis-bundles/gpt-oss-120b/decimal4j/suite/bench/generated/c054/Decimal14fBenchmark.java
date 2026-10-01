package bench.generated.c054;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.mutable.MutableDecimal14f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal14fBenchmark {

    // Operands for arithmetic benchmarks
    private Decimal14f operandA;
    private Decimal14f operandB;

    // Inputs for valueOf overloads
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private long unscaledValue;
    private long unscaledValueWithScale;
    private int otherScale;
    private RoundingMode roundingMode;

    @Setup
    public void setup() {
        // Representative non‑trivial values
        this.longValue = 123456789L;
        this.floatValue = 12345.6789f;
        this.doubleValue = 12345.6789012345;
        this.bigIntegerValue = new BigInteger("12345678901234567890");
        this.bigDecimalValue = new BigDecimal("12345.67890123456789");
        this.stringValue = "12345.67890123456789";
        this.unscaledValue = 1234567890123456L; // already scaled by 10^14
        this.unscaledValueWithScale = 123456789L;
        this.otherScale = 8; // different scale for conversion
        this.roundingMode = RoundingMode.HALF_UP;

        // Operands for arithmetic
        this.operandA = Decimal14f.valueOf(12345L);
        this.operandB = Decimal14f.valueOf(6789L);
    }

    // -------------------------------------------------------------------------
    // valueOf overloads
    // -------------------------------------------------------------------------

    @Benchmark
    public Decimal14f benchValueOfLong() {
        return Decimal14f.valueOf(longValue);
    }

    @Benchmark
    public Decimal14f benchValueOfFloat() {
        return Decimal14f.valueOf(floatValue);
    }

    @Benchmark
    public Decimal14f benchValueOfFloatRounding() {
        return Decimal14f.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal14f benchValueOfDouble() {
        return Decimal14f.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal14f benchValueOfDoubleRounding() {
        return Decimal14f.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal14f benchValueOfBigInteger() {
        return Decimal14f.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal14f benchValueOfBigDecimal() {
        return Decimal14f.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal14f benchValueOfBigDecimalRounding() {
        return Decimal14f.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal14f benchValueOfString() {
        return Decimal14f.valueOf(stringValue);
    }

    @Benchmark
    public Decimal14f benchValueOfStringRounding() {
        return Decimal14f.valueOf(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal14f benchValueOfUnscaled() {
        return Decimal14f.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal14f benchValueOfUnscaledWithScale() {
        return Decimal14f.valueOfUnscaled(unscaledValueWithScale, otherScale);
    }

    @Benchmark
    public Decimal14f benchValueOfUnscaledWithRounding() {
        return Decimal14f.valueOfUnscaled(unscaledValueWithScale, otherScale, roundingMode);
    }

    // -------------------------------------------------------------------------
    // Conversion and factory methods
    // -------------------------------------------------------------------------

    @Benchmark
    public MutableDecimal14f benchToMutableDecimal() {
        return operandA.toMutableDecimal();
    }

    @Benchmark
    public Decimal14f benchToImmutableDecimal() {
        return operandA.toImmutableDecimal();
    }

    // -------------------------------------------------------------------------
    // Basic arithmetic (inherited from Decimal interface)
    // -------------------------------------------------------------------------

    @Benchmark
    public Decimal14f benchAdd() {
        return operandA.add(operandB);
    }

    @Benchmark
    public Decimal14f benchSubtract() {
        return operandA.subtract(operandB);
    }

    @Benchmark
    public Decimal14f benchMultiply() {
        return operandA.multiply(operandB);
    }

    @Benchmark
    public Decimal14f benchDivide() {
        return operandA.divide(operandB);
    }

    @Benchmark
    public Decimal14f benchNegate() {
        return operandA.negate();
    }

    @Benchmark
    public Decimal14f benchAbs() {
        return operandA.abs();
    }

    @Benchmark
    public Decimal14f benchSquare() {
        return operandA.square();
    }

    @Benchmark
    public Decimal14f benchSqrt() {
        return operandA.sqrt();
    }

    @Benchmark
    public Decimal14f benchRound() {
        return operandA.round(2, roundingMode);
    }

    @Benchmark
    public Decimal14f benchShiftLeft() {
        return operandA.shiftLeft(3);
    }

    @Benchmark
    public Decimal14f benchShiftRight() {
        return operandA.shiftRight(2);
    }

    // -------------------------------------------------------------------------
    // Conversion to primitive types (consumed via Blackhole to avoid dead code)
    // -------------------------------------------------------------------------

    @Benchmark
    public void benchToLong(Blackhole bh) {
        bh.consume(operandA.longValue());
    }

    @Benchmark
    public void benchToDouble(Blackhole bh) {
        bh.consume(operandA.doubleValue());
    }

    @Benchmark
    public void benchToBigDecimal(Blackhole bh) {
        bh.consume(operandA.toBigDecimal());
    }

    @Benchmark
    public void benchToString(Blackhole bh) {
        bh.consume(operandA.toString());
    }
}
