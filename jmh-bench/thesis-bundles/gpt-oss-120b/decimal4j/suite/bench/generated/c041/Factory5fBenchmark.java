package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory5f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.mutable.MutableDecimal5f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.factory.DecimalFactory;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory5fBenchmark {

    private Factory5f factory;

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal5f decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private int arrayLength;
    private long unscaledValue;
    private int unscaledScale;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory5f.INSTANCE;
        longValue = 123456789L;
        floatValue = 12345.678f;
        doubleValue = 12345.678901;
        bigIntegerValue = new BigInteger("12345678901234567890");
        bigDecimalValue = new BigDecimal("12345.6789012345");
        decimalValue = Decimal5f.valueOf(12345L);
        stringValue = "12345.67890";
        roundingMode = RoundingMode.HALF_UP;
        arrayLength = 10;
        unscaledValue = 123456789L;
        unscaledScale = 5;
    }

    @Benchmark
    public Scale5f benchmarkGetScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal5f> benchmarkImmutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal5f> benchmarkMutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> benchmarkDeriveFactoryInt() {
        return factory.deriveFactory(6);
    }

    @Benchmark
    public DecimalFactory<Scale5f> benchmarkDeriveFactoryScale() {
        return factory.deriveFactory(Scale5f.INSTANCE);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal5f benchmarkParseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal5f benchmarkParseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfUnscaled() {
        return factory.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfUnscaledWithScale() {
        return factory.valueOfUnscaled(unscaledValue, unscaledScale);
    }

    @Benchmark
    public Decimal5f benchmarkValueOfUnscaledWithScaleRounding() {
        return factory.valueOfUnscaled(unscaledValue, unscaledScale, roundingMode);
    }

    @Benchmark
    public Decimal5f[] benchmarkNewArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal5f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal5f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
