package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory14f;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factories;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.mutable.MutableDecimal14f;
import org.decimal4j.scale.Scale14f;
import org.decimal4j.scale.ScaleMetrics;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory14fBenchmark {

    private Factory14f factory = Factory14f.INSTANCE;

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal14f decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private long unscaledValue;
    private int unscaledScale;
    private int arrayLength;
    private ScaleMetrics otherScaleMetrics;

    @Setup(Level.Trial)
    public void setUp() {
        longValue = 12345678901234L;
        floatValue = 12345.678f;
        doubleValue = 12345678.9012345;
        bigIntegerValue = new BigInteger("12345678901234567890");
        bigDecimalValue = new BigDecimal("12345678.90123456789");
        decimalValue = Decimal14f.valueOf(987654321L);
        stringValue = "12345.6789012345";
        roundingMode = RoundingMode.HALF_UP;
        unscaledValue = 12345678901234L;
        unscaledScale = 14;
        arrayLength = 10;
        otherScaleMetrics = Scale14f.INSTANCE; // using same scale for simplicity
    }

    @Benchmark
    public ScaleMetrics benchGetScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int benchGetScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal14f> benchImmutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal14f> benchMutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> benchDeriveFactoryInt() {
        return factory.deriveFactory(5);
    }

    @Benchmark
    public DecimalFactory<ScaleMetrics> benchDeriveFactoryScale() {
        return factory.deriveFactory(otherScaleMetrics);
    }

    @Benchmark
    public Decimal14f benchValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal14f benchValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal14f benchValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal14f benchValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal14f benchValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal14f benchValueOfBigInteger() {
        return factory.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal14f benchValueOfBigDecimal() {
        return factory.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal14f benchValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal14f benchValueOfDecimal() {
        return factory.valueOf((Decimal<?>) decimalValue);
    }

    @Benchmark
    public Decimal14f benchValueOfDecimalRounding() {
        return factory.valueOf((Decimal<?>) decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal14f benchParseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal14f benchParseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal14f benchValueOfUnscaled() {
        return factory.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal14f benchValueOfUnscaledScale() {
        return factory.valueOfUnscaled(unscaledValue, unscaledScale);
    }

    @Benchmark
    public Decimal14f benchValueOfUnscaledScaleRounding() {
        return factory.valueOfUnscaled(unscaledValue, unscaledScale, roundingMode);
    }

    @Benchmark
    public Decimal14f[] benchNewArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal14f benchNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal14f[] benchNewMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
