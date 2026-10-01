package bench.generated.c038;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factory2f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.scale.Scale2f;
import org.decimal4j.scale.ScaleMetrics;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory2fBenchmark {

    @State(Scope.Benchmark)
    public static class Inputs {
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
        ScaleMetrics scaleMetrics;
        Decimal2f decimal2f;

        @Setup(Level.Trial)
        public void setup() {
            longValue = 123456789L;
            floatValue = 123.456f;
            doubleValue = 123.456789;
            bigIntegerValue = new BigInteger("1234567890123456789");
            bigDecimalValue = new BigDecimal("1234567890.12");
            decimalValue = Decimal2f.valueOf("12345.67");
            stringValue = "12345.67";
            roundingMode = RoundingMode.HALF_UP;
            scale = 2;
            arrayLength = 16;
            scaleMetrics = Scale2f.INSTANCE;
            decimal2f = Decimal2f.valueOf("12345.67");
        }
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Scale2f getScaleMetrics(Inputs in) {
        return Factory2f.INSTANCE.getScaleMetrics();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public int getScale(Inputs in) {
        return Factory2f.INSTANCE.getScale();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Class<Decimal2f> immutableType(Inputs in) {
        return Factory2f.INSTANCE.immutableType();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Class<MutableDecimal2f> mutableType(Inputs in) {
        return Factory2f.INSTANCE.mutableType();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public org.decimal4j.factory.DecimalFactory<?> deriveFactoryInt(Inputs in) {
        return Factory2f.INSTANCE.deriveFactory(in.scale);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public <S extends ScaleMetrics> org.decimal4j.factory.DecimalFactory<S> deriveFactoryScaleMetrics(Inputs in) {
        @SuppressWarnings("unchecked")
        S sm = (S) in.scaleMetrics;
        return Factory2f.INSTANCE.deriveFactory(sm);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfLong(Inputs in) {
        return Factory2f.INSTANCE.valueOf(in.longValue);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfFloat(Inputs in) {
        return Factory2f.INSTANCE.valueOf(in.floatValue);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfFloatRoundingMode(Inputs in) {
        return Factory2f.INSTANCE.valueOf(in.floatValue, in.roundingMode);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfDouble(Inputs in) {
        return Factory2f.INSTANCE.valueOf(in.doubleValue);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfDoubleRoundingMode(Inputs in) {
        return Factory2f.INSTANCE.valueOf(in.doubleValue, in.roundingMode);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfBigInteger(Inputs in) {
        return Factory2f.INSTANCE.valueOf(in.bigIntegerValue);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfBigDecimal(Inputs in) {
        return Factory2f.INSTANCE.valueOf(in.bigDecimalValue);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfBigDecimalRoundingMode(Inputs in) {
        return Factory2f.INSTANCE.valueOf(in.bigDecimalValue, in.roundingMode);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfDecimal(Inputs in) {
        return Factory2f.INSTANCE.valueOf(in.decimalValue);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfDecimalRoundingMode(Inputs in) {
        return Factory2f.INSTANCE.valueOf(in.decimalValue, in.roundingMode);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f parse(Inputs in) {
        return Factory2f.INSTANCE.parse(in.stringValue);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f parseRoundingMode(Inputs in) {
        return Factory2f.INSTANCE.parse(in.stringValue, in.roundingMode);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfUnscaled(Inputs in) {
        return Factory2f.INSTANCE.valueOfUnscaled(in.longValue);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfUnscaledScale(Inputs in) {
        return Factory2f.INSTANCE.valueOfUnscaled(in.longValue, in.scale);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f valueOfUnscaledScaleRoundingMode(Inputs in) {
        return Factory2f.INSTANCE.valueOfUnscaled(in.longValue, in.scale, in.roundingMode);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public Decimal2f[] newArray(Inputs in) {
        return Factory2f.INSTANCE.newArray(in.arrayLength);
    }

    @org.openjdk.jmh.annotations.Benchmark
    public MutableDecimal2f newMutable(Inputs in) {
        return Factory2f.INSTANCE.newMutable();
    }

    @org.openjdk.jmh.annotations.Benchmark
    public MutableDecimal2f[] newMutableArray(Inputs in) {
        return Factory2f.INSTANCE.newMutableArray(in.arrayLength);
    }
}
