package bench.generated.c104;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.decimal4j.scale.Scale8f;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale8fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchState {
        long factor;
        long dividend;
        int factorInt;
        long validValue;
        long invalidValue;
        RoundingMode roundingMode;
        TruncationPolicy truncationPolicy;

        @Setup(Level.Trial)
        public void setup() {
            factor = 123456789L;
            dividend = 123456789012345L;
            factorInt = 123456789;
            validValue = 100000000L;
            invalidValue = Long.MAX_VALUE;
            roundingMode = RoundingMode.HALF_UP;
            truncationPolicy = UncheckedRounding.HALF_UP;
        }
    }

    @Benchmark
    public int getScale(BenchState s) {
        return Scale8f.INSTANCE.getScale();
    }

    @Benchmark
    public long getScaleFactor(BenchState s) {
        return Scale8f.INSTANCE.getScaleFactor();
    }

    @Benchmark
    public int getScaleFactorNumberOfLeadingZeros(BenchState s) {
        return Scale8f.INSTANCE.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public long multiplyByScaleFactor(BenchState s) {
        return Scale8f.INSTANCE.multiplyByScaleFactor(s.factor);
    }

    @Benchmark
    public BigInteger getScaleFactorAsBigInteger(BenchState s) {
        return Scale8f.INSTANCE.getScaleFactorAsBigInteger();
    }

    @Benchmark
    public BigDecimal getScaleFactorAsBigDecimal(BenchState s) {
        return Scale8f.INSTANCE.getScaleFactorAsBigDecimal();
    }

    @Benchmark
    public long getMaxIntegerValue(BenchState s) {
        return Scale8f.INSTANCE.getMaxIntegerValue();
    }

    @Benchmark
    public long getMinIntegerValue(BenchState s) {
        return Scale8f.INSTANCE.getMinIntegerValue();
    }

    @Benchmark
    public boolean isValidIntegerValueTrue(BenchState s) {
        return Scale8f.INSTANCE.isValidIntegerValue(s.validValue);
    }

    @Benchmark
    public boolean isValidIntegerValueFalse(BenchState s) {
        return Scale8f.INSTANCE.isValidIntegerValue(s.invalidValue);
    }

    @Benchmark
    public long multiplyByScaleFactorExact(BenchState s) {
        return Scale8f.INSTANCE.multiplyByScaleFactorExact(s.factor);
    }

    @Benchmark
    public long mulloByScaleFactor(BenchState s) {
        return Scale8f.INSTANCE.mulloByScaleFactor(s.factorInt);
    }

    @Benchmark
    public long mulhiByScaleFactor(BenchState s) {
        return Scale8f.INSTANCE.mulhiByScaleFactor(s.factorInt);
    }

    @Benchmark
    public long divideByScaleFactor(BenchState s) {
        return Scale8f.INSTANCE.divideByScaleFactor(s.dividend);
    }

    @Benchmark
    public long divideUnsignedByScaleFactor(BenchState s) {
        return Scale8f.INSTANCE.divideUnsignedByScaleFactor(s.dividend);
    }

    @Benchmark
    public long moduloByScaleFactor(BenchState s) {
        return Scale8f.INSTANCE.moduloByScaleFactor(s.dividend);
    }

    @Benchmark
    public String toStringLong(BenchState s) {
        return Scale8f.INSTANCE.toString(s.factor);
    }

    @Benchmark
    public DecimalArithmetic getDefaultArithmetic(BenchState s) {
        return Scale8f.INSTANCE.getDefaultArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getDefaultCheckedArithmetic(BenchState s) {
        return Scale8f.INSTANCE.getDefaultCheckedArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingDownArithmetic(BenchState s) {
        return Scale8f.INSTANCE.getRoundingDownArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingFloorArithmetic(BenchState s) {
        return Scale8f.INSTANCE.getRoundingFloorArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingHalfEvenArithmetic(BenchState s) {
        return Scale8f.INSTANCE.getRoundingHalfEvenArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingUnnecessaryArithmetic(BenchState s) {
        return Scale8f.INSTANCE.getRoundingUnnecessaryArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getArithmeticRoundingMode(BenchState s) {
        return Scale8f.INSTANCE.getArithmetic(s.roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getCheckedArithmeticRoundingMode(BenchState s) {
        return Scale8f.INSTANCE.getCheckedArithmetic(s.roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getArithmeticTruncationPolicy(BenchState s) {
        return Scale8f.INSTANCE.getArithmetic(s.truncationPolicy);
    }

    @Benchmark
    public String toStringEnum(BenchState s) {
        return Scale8f.INSTANCE.toString();
    }
}
