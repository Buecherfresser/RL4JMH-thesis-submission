package bench.generated.c104;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.scale.Scale8f;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale8fBenchmark {

    private long factorLong;
    private long factorExactLong;
    private long dividendLong;
    private long unsignedDividendLong;
    private long moduloLong;
    private int factorInt;
    private int factorInt2;
    private RoundingMode roundingMode;
    private TruncationPolicy truncationPolicy;

    @Setup(Level.Trial)
    public void setUp() {
        factorLong = 12345678L;
        factorExactLong = 12345678L;
        dividendLong = 9876543210L;
        unsignedDividendLong = 9876543210L;
        moduloLong = 123456789L;
        factorInt = 12345;
        factorInt2 = 12345;
        roundingMode = RoundingMode.HALF_UP;
        truncationPolicy = UncheckedRounding.HALF_UP;
    }

    @Benchmark
    public int benchmarkGetScale() {
        return Scale8f.INSTANCE.getScale();
    }

    @Benchmark
    public long benchmarkGetScaleFactor() {
        return Scale8f.INSTANCE.getScaleFactor();
    }

    @Benchmark
    public int benchmarkGetScaleFactorNumberOfLeadingZeros() {
        return Scale8f.INSTANCE.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public long benchmarkMultiplyByScaleFactor() {
        return Scale8f.INSTANCE.multiplyByScaleFactor(factorLong);
    }

    @Benchmark
    public long benchmarkMultiplyByScaleFactorExact() {
        return Scale8f.INSTANCE.multiplyByScaleFactorExact(factorExactLong);
    }

    @Benchmark
    public long benchmarkMulloByScaleFactor() {
        return Scale8f.INSTANCE.mulloByScaleFactor(factorInt);
    }

    @Benchmark
    public long benchmarkMulhiByScaleFactor() {
        return Scale8f.INSTANCE.mulhiByScaleFactor(factorInt2);
    }

    @Benchmark
    public long benchmarkDivideByScaleFactor() {
        return Scale8f.INSTANCE.divideByScaleFactor(dividendLong);
    }

    @Benchmark
    public long benchmarkDivideUnsignedByScaleFactor() {
        return Scale8f.INSTANCE.divideUnsignedByScaleFactor(unsignedDividendLong);
    }

    @Benchmark
    public long benchmarkModuloByScaleFactor() {
        return Scale8f.INSTANCE.moduloByScaleFactor(moduloLong);
    }

    @Benchmark
    public String benchmarkToStringLong() {
        return Scale8f.INSTANCE.toString(factorLong);
    }

    @Benchmark
    public boolean benchmarkIsValidIntegerValue() {
        return Scale8f.INSTANCE.isValidIntegerValue(factorLong);
    }

    @Benchmark
    public long benchmarkGetMaxIntegerValue() {
        return Scale8f.INSTANCE.getMaxIntegerValue();
    }

    @Benchmark
    public long benchmarkGetMinIntegerValue() {
        return Scale8f.INSTANCE.getMinIntegerValue();
    }

    @Benchmark
    public BigInteger benchmarkGetScaleFactorAsBigInteger() {
        return Scale8f.INSTANCE.getScaleFactorAsBigInteger();
    }

    @Benchmark
    public BigDecimal benchmarkGetScaleFactorAsBigDecimal() {
        return Scale8f.INSTANCE.getScaleFactorAsBigDecimal();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetDefaultArithmetic() {
        return Scale8f.INSTANCE.getDefaultArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetDefaultCheckedArithmetic() {
        return Scale8f.INSTANCE.getDefaultCheckedArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingDownArithmetic() {
        return Scale8f.INSTANCE.getRoundingDownArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingFloorArithmetic() {
        return Scale8f.INSTANCE.getRoundingFloorArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingHalfEvenArithmetic() {
        return Scale8f.INSTANCE.getRoundingHalfEvenArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetRoundingUnnecessaryArithmetic() {
        return Scale8f.INSTANCE.getRoundingUnnecessaryArithmetic();
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticRoundingMode() {
        return Scale8f.INSTANCE.getArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetCheckedArithmeticRoundingMode() {
        return Scale8f.INSTANCE.getCheckedArithmetic(roundingMode);
    }

    @Benchmark
    public DecimalArithmetic benchmarkGetArithmeticTruncationPolicy() {
        return Scale8f.INSTANCE.getArithmetic(truncationPolicy);
    }

    @Benchmark
    public String benchmarkToString() {
        return Scale8f.INSTANCE.toString();
    }
}
