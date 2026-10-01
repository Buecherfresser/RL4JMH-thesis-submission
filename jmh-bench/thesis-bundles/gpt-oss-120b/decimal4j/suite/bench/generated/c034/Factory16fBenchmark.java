package bench.generated.c034;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory16f;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.mutable.MutableDecimal16f;
import org.decimal4j.scale.Scale16f;
import org.decimal4j.scale.ScaleMetrics;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.openjdk.jmh.annotations.Scope;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory16fBenchmark {

    private Factory16f factory = Factory16f.INSTANCE;

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal<?> decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private long unscaledValue;
    private int unscaledScale;
    private int arrayLength;
    private int otherScale;
    private ScaleMetrics otherScaleMetrics;

    @Setup
    public void setup() {
        longValue = 1234567890123456L;
        floatValue = 12345.6789f;
        doubleValue = 12345678.901234567;
        bigIntegerValue = new BigInteger("123456789012345678901234567890");
        bigDecimalValue = new BigDecimal("1234567890.1234567890123456");
        decimalValue = Decimal16f.valueOf(12345L);
        stringValue = "12345.67890123456789";
        roundingMode = RoundingMode.HALF_UP;
        unscaledValue = 1234567890123456L;
        unscaledScale = 16;
        arrayLength = 10;
        otherScale = 8;
        otherScaleMetrics = Scale16f.INSTANCE;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal16f benchmarkParseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal16f benchmarkParseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfUnscaled() {
        return factory.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfUnscaledWithScale() {
        return factory.valueOfUnscaled(unscaledValue, unscaledScale);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfUnscaledWithScaleRounding() {
        return factory.valueOfUnscaled(unscaledValue, unscaledScale, roundingMode);
    }

    @Benchmark
    public Decimal16f[] benchmarkNewArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal16f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal16f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(arrayLength);
    }

    @Benchmark
    public Object benchmarkDeriveFactoryInt() {
        return factory.deriveFactory(otherScale);
    }

    @Benchmark
    public Object benchmarkDeriveFactoryMetrics() {
        return factory.deriveFactory(otherScaleMetrics);
    }

    @Benchmark
    public Scale16f benchmarkGetScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal16f> benchmarkImmutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal16f> benchmarkMutableType() {
        return factory.mutableType();
    }
}
