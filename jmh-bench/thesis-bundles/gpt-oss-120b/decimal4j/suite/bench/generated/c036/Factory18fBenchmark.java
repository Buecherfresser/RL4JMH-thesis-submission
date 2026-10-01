package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory18f;
import org.decimal4j.factory.Factories;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal18f;
import org.decimal4j.scale.Scale18f;
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
public class Factory18fBenchmark {

    private Factory18f factory;

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecValue;
    private Decimal18f decimalValue;
    private String stringValue;
    private int arrayLength;
    private long unscaledValue;
    private int scale;
    private RoundingMode roundingMode;
    private Scale18f scaleMetrics;

    @Setup(Level.Trial)
    public void setUp() {
        factory = Factory18f.INSTANCE;
        longValue = 123456789L;
        floatValue = 12345.678f;
        doubleValue = 12345678.901234567;
        bigIntValue = new BigInteger("12345678901234567890");
        bigDecValue = new BigDecimal("1234567890.123456789012345678");
        decimalValue = Decimal18f.ONE;
        stringValue = "9876543210.123456789012345678";
        arrayLength = 10;
        unscaledValue = 123456789012345678L;
        scale = 18;
        roundingMode = RoundingMode.HALF_UP;
        scaleMetrics = Scale18f.INSTANCE;
    }

    @Benchmark
    public ScaleMetrics benchmarkGetScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal18f> benchmarkImmutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal18f> benchmarkMutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public Object benchmarkDeriveFactoryInt() {
        return factory.deriveFactory(18);
    }

    @Benchmark
    public Object benchmarkDeriveFactoryScaleMetrics() {
        return factory.deriveFactory(scaleMetrics);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntValue);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecValue);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecValue, roundingMode);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal18f benchmarkParseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal18f benchmarkParseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfUnscaled() {
        return factory.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfUnscaledScale() {
        return factory.valueOfUnscaled(unscaledValue, scale);
    }

    @Benchmark
    public Decimal18f benchmarkValueOfUnscaledScaleRounding() {
        return factory.valueOfUnscaled(unscaledValue, scale, roundingMode);
    }

    @Benchmark
    public Decimal18f[] benchmarkNewArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal18f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal18f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
