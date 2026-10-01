package bench.generated.c096;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.RoundingMode;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.truncate.UncheckedRounding;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale18fBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        long value1 = 123456789012345678L;
        long value2 = -987654321098765432L;
        long value3 = Long.MAX_VALUE / 2;
        long value4 = Long.MIN_VALUE / 2;
        int intValue = 123456789;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        TruncationPolicy truncationPolicy = UncheckedRounding.HALF_UP;

        @Setup(Level.Trial)
        public void setup() {
            // values are already initialized; nothing else needed
        }
    }

    @Benchmark
    public int getScale(Data d) {
        return Scale18f.INSTANCE.getScale();
    }

    @Benchmark
    public long getScaleFactor(Data d) {
        return Scale18f.INSTANCE.getScaleFactor();
    }

    @Benchmark
    public int getScaleFactorNumberOfLeadingZeros(Data d) {
        return Scale18f.INSTANCE.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public long multiplyByScaleFactor(Data d) {
        return Scale18f.INSTANCE.multiplyByScaleFactor(d.value1);
    }

    @Benchmark
    public long multiplyByScaleFactorExact(Data d) {
        return Scale18f.INSTANCE.multiplyByScaleFactorExact(d.value1);
    }

    @Benchmark
    public long divideByScaleFactor(Data d) {
        return Scale18f.INSTANCE.divideByScaleFactor(d.value1);
    }

    @Benchmark
    public long divideUnsignedByScaleFactor(Data d) {
        return Scale18f.INSTANCE.divideUnsignedByScaleFactor(d.value1);
    }

    @Benchmark
    public long moduloByScaleFactor(Data d) {
        return Scale18f.INSTANCE.moduloByScaleFactor(d.value1);
    }

    @Benchmark
    public boolean isValidIntegerValue(Data d) {
        return Scale18f.INSTANCE.isValidIntegerValue(d.value1);
    }

    @Benchmark
    public long mulloByScaleFactor(Data d) {
        return Scale18f.INSTANCE.mulloByScaleFactor(d.intValue);
    }

    @Benchmark
    public long mulhiByScaleFactor(Data d) {
        return Scale18f.INSTANCE.mulhiByScaleFactor(d.intValue);
    }

    @Benchmark
    public String toStringValue(Data d) {
        return Scale18f.INSTANCE.toString(d.value1);
    }

    @Benchmark
    public BigInteger getScaleFactorAsBigInteger(Data d) {
        return Scale18f.INSTANCE.getScaleFactorAsBigInteger();
    }

    @Benchmark
    public BigDecimal getScaleFactorAsBigDecimal(Data d) {
        return Scale18f.INSTANCE.getScaleFactorAsBigDecimal();
    }

    @Benchmark
    public long getMaxIntegerValue(Data d) {
        return Scale18f.INSTANCE.getMaxIntegerValue();
    }

    @Benchmark
    public long getMinIntegerValue(Data d) {
        return Scale18f.INSTANCE.getMinIntegerValue();
    }

    @Benchmark
    public DecimalArithmetic getDefaultArithmetic(Data d) {
        return Scale18f.INSTANCE.getDefaultArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getDefaultCheckedArithmetic(Data d) {
        return Scale18f.INSTANCE.getDefaultCheckedArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingDownArithmetic(Data d) {
        return Scale18f.INSTANCE.getRoundingDownArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingFloorArithmetic(Data d) {
        return Scale18f.INSTANCE.getRoundingFloorArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingHalfEvenArithmetic(Data d) {
        return Scale18f.INSTANCE.getRoundingHalfEvenArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getRoundingUnnecessaryArithmetic(Data d) {
        return Scale18f.INSTANCE.getRoundingUnnecessaryArithmetic();
    }

    @Benchmark
    public DecimalArithmetic getArithmetic(Data d) {
        return Scale18f.INSTANCE.getArithmetic(d.roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getCheckedArithmetic(Data d) {
        return Scale18f.INSTANCE.getCheckedArithmetic(d.roundingMode);
    }

    @Benchmark
    public DecimalArithmetic getArithmeticTruncationPolicy(Data d) {
        return Scale18f.INSTANCE.getArithmetic(d.truncationPolicy);
    }
}
