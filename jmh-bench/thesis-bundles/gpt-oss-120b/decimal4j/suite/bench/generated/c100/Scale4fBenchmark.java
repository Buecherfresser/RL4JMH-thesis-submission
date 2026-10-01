package bench.generated.c100;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale4f;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;
import java.math.RoundingMode;
import java.math.BigInteger;
import java.math.BigDecimal;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale4fBenchmark {

    // Input values prepared in @Setup
    private long factor;
    private long exactFactor;
    private int intFactor;
    private long dividend;
    private long unsignedDividend;
    private long valueForString;
    private long valueForValidity;
    private TruncationPolicy truncPolicy;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setUp() {
        // Choose values that stay within valid ranges for the scale
        this.factor = 12345L;                 // arbitrary long
        this.exactFactor = 100L;               // within integer range for exact multiply
        this.intFactor = 12345;                // arbitrary int
        this.dividend = 987654321L;            // arbitrary dividend
        this.unsignedDividend = 0x7FFFFFFFFFFFFFFFL; // positive long for unsigned test
        this.valueForString = 54321L;          // arbitrary value to format
        this.valueForValidity = 5000L;         // within valid integer range
        this.truncPolicy = UncheckedRounding.HALF_UP; // a TruncationPolicy instance
    }

    @Benchmark
    public int benchmarkGetScale() {
        return Scale4f.INSTANCE.getScale();
    }

    @Benchmark
    public long benchmarkGetScaleFactor() {
        return Scale4f.INSTANCE.getScaleFactor();
    }

    @Benchmark
    public int benchmarkGetScaleFactorNumberOfLeadingZeros() {
        return Scale4f.INSTANCE.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public long benchmarkMultiplyByScaleFactor() {
        return Scale4f.INSTANCE.multiplyByScaleFactor(factor);
    }

    @Benchmark
    public long benchmarkMultiplyByScaleFactorExact() {
        return Scale4f.INSTANCE.multiplyByScaleFactorExact(exactFactor);
    }

    @Benchmark
    public long benchmarkMulloByScaleFactor() {
        return Scale4f.INSTANCE.mulloByScaleFactor(intFactor);
    }

    @Benchmark
    public long benchmarkMulhiByScaleFactor() {
        return Scale4f.INSTANCE.mulhiByScaleFactor(intFactor);
    }

    @Benchmark
    public long benchmarkDivideByScaleFactor() {
        return Scale4f.INSTANCE.divideByScaleFactor(dividend);
    }

    @Benchmark
    public long benchmarkDivideUnsignedByScaleFactor() {
        return Scale4f.INSTANCE.divideUnsignedByScaleFactor(unsignedDividend);
    }

    @Benchmark
    public long benchmarkModuloByScaleFactor() {
        return Scale4f.INSTANCE.moduloByScaleFactor(dividend);
    }

    @Benchmark
    public String benchmarkToStringLong() {
        return Scale4f.INSTANCE.toString(valueForString);
    }

    @Benchmark
    public boolean benchmarkIsValidIntegerValue() {
        return Scale4f.INSTANCE.isValidIntegerValue(valueForValidity);
    }

    @Benchmark
    public BigInteger benchmarkGetScaleFactorAsBigInteger() {
        return Scale4f.INSTANCE.getScaleFactorAsBigInteger();
    }

    @Benchmark
    public BigDecimal benchmarkGetScaleFactorAsBigDecimal() {
        return Scale4f.INSTANCE.getScaleFactorAsBigDecimal();
    }

    @Benchmark
    public long benchmarkGetMaxIntegerValue() {
        return Scale4f.INSTANCE.getMaxIntegerValue();
    }

    @Benchmark
    public long benchmarkGetMinIntegerValue() {
        return Scale4f.INSTANCE.getMinIntegerValue();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetDefaultArithmetic() {
        return Scale4f.INSTANCE.getDefaultArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetDefaultCheckedArithmetic() {
        return Scale4f.INSTANCE.getDefaultCheckedArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingDownArithmetic() {
        return Scale4f.INSTANCE.getRoundingDownArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingFloorArithmetic() {
        return Scale4f.INSTANCE.getRoundingFloorArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingHalfEvenArithmetic() {
        return Scale4f.INSTANCE.getRoundingHalfEvenArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingUnnecessaryArithmetic() {
        return Scale4f.INSTANCE.getRoundingUnnecessaryArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticRoundingMode() {
        return Scale4f.INSTANCE.getArithmetic(RoundingMode.HALF_UP);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetCheckedArithmeticRoundingMode() {
        return Scale4f.INSTANCE.getCheckedArithmetic(RoundingMode.HALF_UP);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticTruncationPolicy() {
        return Scale4f.INSTANCE.getArithmetic(truncPolicy);
    }

    @Benchmark
    public String benchmarkToString() {
        return Scale4f.INSTANCE.toString();
    }
}
