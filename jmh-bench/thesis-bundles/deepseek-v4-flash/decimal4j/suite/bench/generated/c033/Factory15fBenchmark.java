package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory15f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.mutable.MutableDecimal15f;
import org.decimal4j.scale.Scale15f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory15fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        long longValue;
        float floatValue;
        double doubleValue;
        BigInteger bigIntegerValue;
        BigDecimal bigDecimalValue;
        Decimal<?> decimalValue;
        String stringValue;
        RoundingMode roundingMode;
        int scale;
        int arrayLength;
        Scale15f scaleMetrics;

        @Setup(Level.Trial)
        public void setup() {
            longValue = 123456789012345678L;
            floatValue = 123.456f;
            doubleValue = 123.456789012345678;
            bigIntegerValue = new BigInteger("123456789012345678901234567890");
            bigDecimalValue = new BigDecimal("123456789012345678.123456789012345");
            decimalValue = Factory15f.INSTANCE.valueOf(longValue);
            stringValue = "123456789012345678.123456789012345";
            roundingMode = RoundingMode.HALF_UP;
            scale = 10;
            arrayLength = 10;
            scaleMetrics = Scale15f.INSTANCE;
        }
    }

    @Benchmark
    public ScaleMetrics getScaleMetrics(BenchmarkState state) {
        return Factory15f.INSTANCE.getScaleMetrics();
    }

    @Benchmark
    public int getScale(BenchmarkState state) {
        return Factory15f.INSTANCE.getScale();
    }

    @Benchmark
    public Class<Decimal15f> immutableType(BenchmarkState state) {
        return Factory15f.INSTANCE.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal15f> mutableType(BenchmarkState state) {
        return Factory15f.INSTANCE.mutableType();
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<?> deriveFactoryInt(BenchmarkState state) {
        return Factory15f.INSTANCE.deriveFactory(state.scale);
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<Scale15f> deriveFactoryScaleMetrics(BenchmarkState state) {
        return Factory15f.INSTANCE.deriveFactory(state.scaleMetrics);
    }

    @Benchmark
    public Decimal15f valueOfLong(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOf(state.longValue);
    }

    @Benchmark
    public Decimal15f valueOfFloat(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOf(state.floatValue);
    }

    @Benchmark
    public Decimal15f valueOfFloatRounding(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOf(state.floatValue, state.roundingMode);
    }

    @Benchmark
    public Decimal15f valueOfDouble(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOf(state.doubleValue);
    }

    @Benchmark
    public Decimal15f valueOfDoubleRounding(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOf(state.doubleValue, state.roundingMode);
    }

    @Benchmark
    public Decimal15f valueOfBigInteger(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOf(state.bigIntegerValue);
    }

    @Benchmark
    public Decimal15f valueOfBigDecimal(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOf(state.bigDecimalValue);
    }

    @Benchmark
    public Decimal15f valueOfBigDecimalRounding(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOf(state.bigDecimalValue, state.roundingMode);
    }

    @Benchmark
    public Decimal15f valueOfDecimal(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOf(state.decimalValue);
    }

    @Benchmark
    public Decimal15f valueOfDecimalRounding(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOf(state.decimalValue, state.roundingMode);
    }

    @Benchmark
    public Decimal15f parseString(BenchmarkState state) {
        return Factory15f.INSTANCE.parse(state.stringValue);
    }

    @Benchmark
    public Decimal15f parseStringRounding(BenchmarkState state) {
        return Factory15f.INSTANCE.parse(state.stringValue, state.roundingMode);
    }

    @Benchmark
    public Decimal15f valueOfUnscaledLong(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOfUnscaled(state.longValue);
    }

    @Benchmark
    public Decimal15f valueOfUnscaledLongScale(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOfUnscaled(state.longValue, state.scale);
    }

    @Benchmark
    public Decimal15f valueOfUnscaledLongScaleRounding(BenchmarkState state) {
        return Factory15f.INSTANCE.valueOfUnscaled(state.longValue, state.scale, state.roundingMode);
    }

    @Benchmark
    public Decimal15f[] newArray(BenchmarkState state) {
        return Factory15f.INSTANCE.newArray(state.arrayLength);
    }

    @Benchmark
    public MutableDecimal15f newMutable(BenchmarkState state) {
        return Factory15f.INSTANCE.newMutable();
    }

    @Benchmark
    public MutableDecimal15f[] newMutableArray(BenchmarkState state) {
        return Factory15f.INSTANCE.newMutableArray(state.arrayLength);
    }
}
