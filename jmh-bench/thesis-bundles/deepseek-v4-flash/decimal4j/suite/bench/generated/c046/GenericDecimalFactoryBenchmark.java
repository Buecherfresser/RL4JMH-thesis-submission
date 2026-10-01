package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.generic.GenericDecimalFactory;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.immutable.Decimal5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GenericDecimalFactoryBenchmark {

    private GenericDecimalFactory<Scale5f> factory;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private Decimal5f decimalValue;
    private RoundingMode roundingMode;
    private int scale;
    private long unscaledValue;
    private int arrayLength;

    @Setup(Level.Trial)
    public void setup() {
        factory = new GenericDecimalFactory<>(Scale5f.INSTANCE);
        longValue = 123456789L;
        floatValue = 123.456f;
        doubleValue = 123.456789;
        bigIntegerValue = new BigInteger("12345678901234567890");
        bigDecimalValue = new BigDecimal("1234567890.123456789");
        stringValue = "1234567890.123456789";
        decimalValue = Decimal5f.valueOf(123.456);
        roundingMode = RoundingMode.HALF_UP;
        scale = 5;
        unscaledValue = 123456789L;
        arrayLength = 10;
    }

    @Benchmark
    public GenericDecimalFactory<Scale5f> constructor() {
        return new GenericDecimalFactory<>(Scale5f.INSTANCE);
    }

    @Benchmark
    public int getScale() {
        return factory.getScale();
    }

    @Benchmark
    public Scale5f getScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public Class<?> immutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<?> mutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public GenericDecimalFactory<?> deriveFactoryInt() {
        return factory.deriveFactory(5);
    }

    @Benchmark
    public GenericDecimalFactory<Scale5f> deriveFactoryScaleMetrics() {
        return factory.deriveFactory(Scale5f.INSTANCE);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfBigInteger() {
        return factory.valueOf(bigIntegerValue);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfBigDecimal() {
        return factory.valueOf(bigDecimalValue);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfBigDecimalRounding() {
        return factory.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> parseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> parseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfUnscaledLong() {
        return factory.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(unscaledValue, scale);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f> valueOfUnscaledLongScaleRounding() {
        return factory.valueOfUnscaled(unscaledValue, scale, roundingMode);
    }

    @Benchmark
    public org.decimal4j.generic.GenericImmutableDecimal<Scale5f>[] newArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public org.decimal4j.generic.GenericMutableDecimal<Scale5f> newMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public org.decimal4j.generic.GenericMutableDecimal<Scale5f>[] newMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
