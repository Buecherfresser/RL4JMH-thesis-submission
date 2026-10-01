package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory15f;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.scale.Scale15f;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.mutable.MutableDecimal15f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory15fBenchmark {

    private Factory15f factory;

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal15f decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private long unscaledValue;
    private int scaleParam;
    private int arrayLength;
    private ScaleMetrics otherScaleMetrics;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory15f.INSTANCE;
        longValue = 123456789012345L;
        floatValue = 12345.678f;
        doubleValue = 12345.6789012345;
        bigIntegerValue = new BigInteger("12345678901234567890");
        bigDecimalValue = new BigDecimal("1234567890.123456789012345");
        decimalValue = Decimal15f.valueOf(longValue);
        stringValue = "1234567890.123456789012345";
        roundingMode = RoundingMode.HALF_UP;
        unscaledValue = 987654321098765L;
        scaleParam = 10;
        arrayLength = 16;
        otherScaleMetrics = Scale5f.INSTANCE;
    }

    @Benchmark
    public Decimal15f valueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal15f valueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal15f valueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal15f valueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal15f valueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal15f valueOfBigInteger() {
        return factory.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal15f valueOfBigDecimal() {
        return factory.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal15f valueOfBigDecimalRounding() {
        return factory.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal15f valueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal15f valueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal15f parseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal15f parseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal15f valueOfUnscaled() {
        return factory.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal15f valueOfUnscaledWithScale() {
        return factory.valueOfUnscaled(unscaledValue, scaleParam);
    }

    @Benchmark
    public Decimal15f valueOfUnscaledWithScaleRounding() {
        return factory.valueOfUnscaled(unscaledValue, scaleParam, roundingMode);
    }

    @Benchmark
    public Decimal15f[] newArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal15f newMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal15f[] newMutableArray() {
        return factory.newMutableArray(arrayLength);
    }

    @Benchmark
    public int getScale() {
        return factory.getScale();
    }

    @Benchmark
    public Scale15f getScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public Class<Decimal15f> immutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal15f> mutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryInt() {
        return factory.deriveFactory(scaleParam);
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryMetrics() {
        return factory.deriveFactory(otherScaleMetrics);
    }

    @Benchmark
    public void consumeValueOfLong(Blackhole bh) {
        bh.consume(factory.valueOf(longValue));
    }
}
