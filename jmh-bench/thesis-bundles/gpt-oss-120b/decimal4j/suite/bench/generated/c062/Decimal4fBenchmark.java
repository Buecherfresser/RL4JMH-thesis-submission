package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable4f;
import org.decimal4j.mutable.MutableDecimal4f;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal4fBenchmark {

    // Immutable operands used for binary operations
    private Decimal4f operandA;
    private Decimal4f operandB;

    // Various representations for factory methods
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private long unscaledValue;
    private int unscaledScale;

    @Setup(Level.Trial)
    public void setUp() {
        // Values chosen to stay well within the representable range
        operandA = Decimal4f.valueOf(12345L);
        operandB = Decimal4f.valueOf(6789L);

        longValue = 123456789L;
        floatValue = 12345.6789f;
        doubleValue = 12345.67890123;
        bigIntegerValue = new BigInteger("12345678901234567890");
        bigDecimalValue = new BigDecimal("12345.67890123456789");
        stringValue = "12345.6789";
        unscaledValue = 123456789L; // represents 12345.6789 after scaling
        unscaledScale = 2; // will be rescaled to scale 4
    }

    // -------------------------------------------------------------------------
    // Static factory methods
    // -------------------------------------------------------------------------

    @Benchmark
    public Decimal4f valueOf_long() {
        return Decimal4f.valueOf(longValue);
    }

    @Benchmark
    public Decimal4f valueOf_float() {
        return Decimal4f.valueOf(floatValue);
    }

    @Benchmark
    public Decimal4f valueOf_float_rounding() {
        return Decimal4f.valueOf(floatValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal4f valueOf_double() {
        return Decimal4f.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal4f valueOf_double_rounding() {
        return Decimal4f.valueOf(doubleValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal4f valueOf_BigInteger() {
        return Decimal4f.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal4f valueOf_BigDecimal() {
        return Decimal4f.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal4f valueOf_BigDecimal_rounding() {
        return Decimal4f.valueOf(bigDecimalValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal4f valueOf_String() {
        return Decimal4f.valueOf(stringValue);
    }

    @Benchmark
    public Decimal4f valueOf_String_rounding() {
        return Decimal4f.valueOf(stringValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal4f valueOf_unscaled_long() {
        return Decimal4f.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal4f valueOf_unscaled_long_scale() {
        return Decimal4f.valueOfUnscaled(unscaledValue, unscaledScale);
    }

    @Benchmark
    public Decimal4f valueOf_unscaled_long_scale_rounding() {
        return Decimal4f.valueOfUnscaled(unscaledValue, unscaledScale, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal4f valueOf_Decimal() {
        Decimal<?> dec = operandA; // Decimal4f implements Decimal
        return Decimal4f.valueOf(dec);
    }

    @Benchmark
    public Decimal4f valueOf_Decimal_rounding() {
        Decimal<?> dec = operandA;
        return Decimal4f.valueOf(dec, RoundingMode.HALF_UP);
    }

    // -------------------------------------------------------------------------
    // Instance arithmetic methods (read‑only)
    // -------------------------------------------------------------------------

    @Benchmark
    public Decimal4f add() {
        return operandA.add(operandB);
    }

    @Benchmark
    public Decimal4f subtract() {
        return operandA.subtract(operandB);
    }

    @Benchmark
    public Decimal4f multiply() {
        return operandA.multiply(operandB);
    }

    @Benchmark
    public Decimal4f divide() {
        return operandA.divide(operandB);
    }

    @Benchmark
    public Decimal4f remainder() {
        return operandA.remainder(operandB);
    }

    @Benchmark
    public Decimal4f negate() {
        return operandA.negate();
    }

    @Benchmark
    public Decimal4f abs() {
        return operandA.abs();
    }

    @Benchmark
    public Decimal4f invert() {
        return operandA.invert();
    }

    @Benchmark
    public Decimal4f square() {
        return operandA.square();
    }

    @Benchmark
    public Decimal4f sqrt() {
        return operandA.sqrt();
    }

    @Benchmark
    public Decimal4f pow() {
        return operandA.pow(3);
    }

    @Benchmark
    public Decimal4f avg() {
        return operandA.avg(operandB);
    }

    @Benchmark
    public Decimal4f shiftLeft() {
        return operandA.shiftLeft(2);
    }

    @Benchmark
    public Decimal4f shiftRight() {
        return operandA.shiftRight(2);
    }

    @Benchmark
    public Decimal4f round() {
        return operandA.round(2);
    }

    // -------------------------------------------------------------------------
    // Conversion / utility methods
    // -------------------------------------------------------------------------

    @Benchmark
    public long toLong() {
        return operandA.longValue();
    }

    @Benchmark
    public double toDouble() {
        return operandA.doubleValue();
    }

    @Benchmark
    public BigDecimal toBigDecimal() {
        return operandA.toBigDecimal();
    }

    @Benchmark
    public String toStringBenchmark() {
        return operandA.toString();
    }

    // -------------------------------------------------------------------------
    // Mutating related methods (produce new objects)
    // -------------------------------------------------------------------------

    @Benchmark
    public Multipliable4f multiplyExact() {
        return operandA.multiplyExact();
    }

    @Benchmark
    public MutableDecimal4f toMutableDecimal() {
        return operandA.toMutableDecimal();
    }

    @Benchmark
    public Decimal4f toImmutableDecimal() {
        return operandA.toImmutableDecimal();
    }

    // -------------------------------------------------------------------------
    // Blackhole consumption variant for void‑like operation
    // -------------------------------------------------------------------------

    @Benchmark
    public void consumeLongValue(Blackhole bh) {
        bh.consume(operandA.unscaledValue());
    }
}
