package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import org.decimal4j.api.Decimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
import org.decimal4j.factory.Factory14f;
import org.decimal4j.mutable.MutableDecimal14f;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.scale.ScaleMetrics;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory14fBenchmark {

    @State(Scope.Benchmark)
    public static class Data {
        Factory14f factory;
        long longValue;
        float floatValue;
        double doubleValue;
        BigInteger bigInteger;
        BigDecimal bigDecimal;
        Decimal<?> sameScaleDecimal;
        Decimal<?> otherScaleDecimal;
        String parseString;
        RoundingMode roundingMode;
        int arrayLength;
        ScaleMetrics scaleMetrics;
        int deriveScale;

        @Setup(Level.Trial)
        public void setup() {
            factory = Factory14f.INSTANCE;
            longValue = 123456789012345L;
            floatValue = 1.2345678E7f;
            doubleValue = 1234567890.1234567d;
            bigInteger = BigInteger.valueOf(123456789012345L);
            bigDecimal = new BigDecimal("1234567890.12345678901234");
            sameScaleDecimal = factory.valueOf(doubleValue);
            otherScaleDecimal = Factories.getDecimalFactory(2).valueOf(123456.78d);
            parseString = "12345.67890123456789";
            roundingMode = RoundingMode.HALF_UP;
            arrayLength = 16;
            scaleMetrics = Scale5f.INSTANCE;
            deriveScale = 5;
        }
    }

    @Benchmark
    public Decimal<?> valueOfLong(Data d) {
        return d.factory.valueOf(d.longValue);
    }

    @Benchmark
    public Decimal<?> valueOfFloat(Data d) {
        return d.factory.valueOf(d.floatValue);
    }

    @Benchmark
    public Decimal<?> valueOfFloatRounding(Data d) {
        return d.factory.valueOf(d.floatValue, d.roundingMode);
    }

    @Benchmark
    public Decimal<?> valueOfDouble(Data d) {
        return d.factory.valueOf(d.doubleValue);
    }

    @Benchmark
    public Decimal<?> valueOfDoubleRounding(Data d) {
        return d.factory.valueOf(d.doubleValue, d.roundingMode);
    }

    @Benchmark
    public Decimal<?> valueOfBigInteger(Data d) {
        return d.factory.valueOf(d.bigInteger);
    }

    @Benchmark
    public Decimal<?> valueOfBigDecimal(Data d) {
        return d.factory.valueOf(d.bigDecimal);
    }

    @Benchmark
    public Decimal<?> valueOfBigDecimalRounding(Data d) {
        return d.factory.valueOf(d.bigDecimal, d.roundingMode);
    }

    @Benchmark
    public Decimal<?> valueOfDecimalSameScale(Data d) {
        return d.factory.valueOf(d.sameScaleDecimal);
    }

    @Benchmark
    public Decimal<?> valueOfDecimalOtherScale(Data d) {
        return d.factory.valueOf(d.otherScaleDecimal);
    }

    @Benchmark
    public Decimal<?> valueOfDecimalRounding(Data d) {
        return d.factory.valueOf(d.otherScaleDecimal, d.roundingMode);
    }

    @Benchmark
    public Decimal<?> parse(Data d) {
        return d.factory.parse(d.parseString);
    }

    @Benchmark
    public Decimal<?> parseRounding(Data d) {
        return d.factory.parse(d.parseString, d.roundingMode);
    }

    @Benchmark
    public Decimal<?> valueOfUnscaled(Data d) {
        return d.factory.valueOfUnscaled(d.longValue);
    }

    @Benchmark
    public Decimal<?> valueOfUnscaledWithScale(Data d) {
        return d.factory.valueOfUnscaled(d.longValue, d.deriveScale);
    }

    @Benchmark
    public Decimal<?> valueOfUnscaledWithScaleRounding(Data d) {
        return d.factory.valueOfUnscaled(d.longValue, d.deriveScale, d.roundingMode);
    }

    @Benchmark
    public org.decimal4j.immutable.Decimal14f[] newArray(Data d) {
        return d.factory.newArray(d.arrayLength);
    }

    @Benchmark
    public MutableDecimal14f newMutable(Data d) {
        return d.factory.newMutable();
    }

    @Benchmark
    public MutableDecimal14f[] newMutableArray(Data d) {
        return d.factory.newMutableArray(d.arrayLength);
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryInt(Data d) {
        return d.factory.deriveFactory(d.deriveScale);
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryMetrics(Data d) {
        return d.factory.deriveFactory(d.scaleMetrics);
    }

    @Benchmark
    public int getScale(Data d) {
        return d.factory.getScale();
    }

    @Benchmark
    public ScaleMetrics getScaleMetrics(Data d) {
        return d.factory.getScaleMetrics();
    }

    @Benchmark
    public Class<?> immutableType(Data d) {
        return d.factory.immutableType();
    }

    @Benchmark
    public Class<?> mutableType(Data d) {
        return d.factory.mutableType();
    }
}
