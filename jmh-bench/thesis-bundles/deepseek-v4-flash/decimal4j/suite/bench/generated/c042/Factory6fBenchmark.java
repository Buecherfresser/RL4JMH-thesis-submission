package bench.generated.c042;

import org.openjdk.jmh.annotations.*;
import org.decimal4j.api.Decimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factory6f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.scale.Scale6f;
import org.decimal4j.scale.ScaleMetrics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory6fBenchmark {

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal6f decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private int scale;
    private Scale6f scaleMetrics;
    private int arrayLength;
    private long unscaledValue;
    private int unscaledScale;
    private RoundingMode unscaledRoundingMode;

    @Setup(Level.Trial)
    public void setup() {
        longValue = 123456789L;
        floatValue = 123.456f;
        doubleValue = 123456.789;
        bigIntegerValue = new BigInteger("12345678901234567890");
        bigDecimalValue = new BigDecimal("123456.789012");
        decimalValue = Decimal6f.valueOf("123.456789");
        stringValue = "123.456789";
        roundingMode = RoundingMode.HALF_UP;
        scale = 3;
        scaleMetrics = Scale6f.INSTANCE;
        arrayLength = 10;
        unscaledValue = 123456789L;
        unscaledScale = 2;
        unscaledRoundingMode = RoundingMode.HALF_UP;
    }

    @Benchmark
    public Scale6f getScaleMetrics() {
        return Factory6f.INSTANCE.getScaleMetrics();
    }

    @Benchmark
    public int getScale() {
        return Factory6f.INSTANCE.getScale();
    }

    @Benchmark
    public Class<Decimal6f> immutableType() {
        return Factory6f.INSTANCE.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal6f> mutableType() {
        return Factory6f.INSTANCE.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryInt() {
        return Factory6f.INSTANCE.deriveFactory(scale);
    }

    @Benchmark
    public DecimalFactory<Scale6f> deriveFactoryScaleMetrics() {
        return Factory6f.INSTANCE.deriveFactory(scaleMetrics);
    }

    @Benchmark
    public Decimal6f valueOfLong() {
        return Factory6f.INSTANCE.valueOf(longValue);
    }

    @Benchmark
    public Decimal6f valueOfFloat() {
        return Factory6f.INSTANCE.valueOf(floatValue);
    }

    @Benchmark
    public Decimal6f valueOfFloatRounding() {
        return Factory6f.INSTANCE.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal6f valueOfDouble() {
        return Factory6f.INSTANCE.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal6f valueOfDoubleRounding() {
        return Factory6f.INSTANCE.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal6f valueOfBigInteger() {
        return Factory6f.INSTANCE.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal6f valueOfBigDecimal() {
        return Factory6f.INSTANCE.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal6f valueOfBigDecimalRounding() {
        return Factory6f.INSTANCE.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal6f valueOfDecimal() {
        return Factory6f.INSTANCE.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal6f valueOfDecimalRounding() {
        return Factory6f.INSTANCE.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal6f parseString() {
        return Factory6f.INSTANCE.parse(stringValue);
    }

    @Benchmark
    public Decimal6f parseStringRounding() {
        return Factory6f.INSTANCE.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal6f valueOfUnscaled() {
        return Factory6f.INSTANCE.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal6f valueOfUnscaledScale() {
        return Factory6f.INSTANCE.valueOfUnscaled(unscaledValue, unscaledScale);
    }

    @Benchmark
    public Decimal6f valueOfUnscaledScaleRounding() {
        return Factory6f.INSTANCE.valueOfUnscaled(unscaledValue, unscaledScale, unscaledRoundingMode);
    }

    @Benchmark
    public Decimal6f[] newArray() {
        return Factory6f.INSTANCE.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal6f newMutable() {
        return Factory6f.INSTANCE.newMutable();
    }

    @Benchmark
    public MutableDecimal6f[] newMutableArray() {
        return Factory6f.INSTANCE.newMutableArray(arrayLength);
    }
}
