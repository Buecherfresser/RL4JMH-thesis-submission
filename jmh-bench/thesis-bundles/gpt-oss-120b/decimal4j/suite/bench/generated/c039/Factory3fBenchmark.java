package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.factory.Factory3f;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.scale.Scale3f;
import org.decimal4j.scale.ScaleMetrics;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory3fBenchmark {

    private Factory3f factory;

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal<?> decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private int unscaledScale;
    private int arrayLength;
    private ScaleMetrics scaleMetrics;

    @Setup(Level.Trial)
    public void setUp() {
        factory = Factory3f.INSTANCE;

        longValue = 123456L;
        floatValue = 12345.678f;
        doubleValue = 12345.678901;
        bigIntegerValue = new BigInteger("12345678901234567890");
        bigDecimalValue = new BigDecimal("12345.67890123456789");
        decimalValue = Decimal3f.ONE;
        stringValue = "12345.678";
        roundingMode = RoundingMode.HALF_UP;
        unscaledScale = 3;
        arrayLength = 10;
        scaleMetrics = Scale3f.INSTANCE;
    }

    @Benchmark
    public Scale3f benchmarkGetScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal3f> benchmarkImmutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal3f> benchmarkMutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public Object benchmarkDeriveFactoryByInt() {
        return factory.deriveFactory(unscaledScale);
    }

    @Benchmark
    public Object benchmarkDeriveFactoryByMetrics() {
        return factory.deriveFactory(scaleMetrics);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal3f benchmarkParseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal3f benchmarkParseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfUnscaledLong() {
        return factory.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(longValue, unscaledScale);
    }

    @Benchmark
    public Decimal3f benchmarkValueOfUnscaledLongScaleRounding() {
        return factory.valueOfUnscaled(longValue, unscaledScale, roundingMode);
    }

    @Benchmark
    public Decimal3f[] benchmarkNewArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal3f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal3f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
