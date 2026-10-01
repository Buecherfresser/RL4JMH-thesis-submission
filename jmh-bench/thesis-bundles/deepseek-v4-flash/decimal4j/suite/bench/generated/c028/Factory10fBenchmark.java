package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.decimal4j.factory.Factory10f;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.scale.Scale10f;

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
public class Factory10fBenchmark {

    private Factory10f factory;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal10f decimalValue;
    private String stringValue;
    private long unscaledValue;
    private int scale;
    private int arrayLength;
    private Scale10f scaleMetrics;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory10f.INSTANCE;
        longValue = 1234567890123456789L;
        floatValue = 12345.6789f;
        doubleValue = 12345.67890123456789;
        bigIntegerValue = new BigInteger("123456789012345678901234567890");
        bigDecimalValue = new BigDecimal("12345.67890123456789");
        decimalValue = Decimal10f.valueOf("12345.6789012345");
        stringValue = "12345.6789012345";
        unscaledValue = 1234567890123456789L;
        scale = 5;
        arrayLength = 16;
        scaleMetrics = Scale10f.INSTANCE;
    }

    @Benchmark
    public int getScale() {
        return factory.getScale();
    }

    @Benchmark
    public Scale10f getScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public Class<Decimal10f> immutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal10f> mutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public Decimal10f valueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal10f valueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal10f valueOfFloatRounding() {
        return factory.valueOf(floatValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal10f valueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal10f valueOfDoubleRounding() {
        return factory.valueOf(doubleValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal10f valueOfBigInteger() {
        return factory.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal10f valueOfBigDecimal() {
        return factory.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal10f valueOfBigDecimalRounding() {
        return factory.valueOf(bgValue(), RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal10f valueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal10f valueOfDecimalRounding() {
        return factory.valueOf(decimalValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal10f parseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal10f parseStringRounding() {
        return factory.parse(stringValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal10f valueOfUnscaled() {
        return factory.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal10f valueOfUnscaledScale() {
        return factory.valueOfUnscaled(unscaledValue, scale);
    }

    @Benchmark
    public Decimal10f valueOfUnscaledScaleRounding() {
        return factory.valueOfUnscaled(unscaledValue, scale, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal10f[] newArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal10f newMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal10f[] newMutableArray() {
        return factory.newMutableArray(arrayLength);
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<?> deriveFactoryInt() {
        return factory.deriveFactory(scale);
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<Scale10f> deriveFactoryScale() {
        return factory.deriveFactory(scaleMetrics);
    }

    private BigDecimal bgValue() {
        return bigDecimalValue;
    }
}
