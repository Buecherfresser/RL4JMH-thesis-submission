package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory17f;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.mutable.MutableDecimal17f;
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale17f;
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
public class Factory17fBenchmark {

    private Factory17f factory;

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecValue;
    private Decimal<?> decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private ScaleMetrics scaleMetrics;
    private int arrayLength;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory17f.INSTANCE;
        longValue = 123456789012345L;
        floatValue = 12345.678f;
        doubleValue = 12345.6789012345;
        bigIntValue = new BigInteger("1234567890123456789012345");
        bigDecValue = new BigDecimal("1234567890.1234567890123");
        decimalValue = Decimal17f.valueOf(longValue);
        stringValue = "12345.6789012345678";
        roundingMode = RoundingMode.HALF_UP;
        scaleMetrics = Scale17f.INSTANCE;
        arrayLength = 10;
    }

    @Benchmark
    public int benchmarkGetScale() {
        return factory.getScale();
    }

    @Benchmark
    public Scale17f benchmarkGetScaleMetrics() {
        return (Scale17f) factory.getScaleMetrics();
    }

    @Benchmark
    public Class<Decimal17f> benchmarkImmutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal17f> benchmarkMutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> benchmarkDeriveFactoryByInt() {
        return factory.deriveFactory(5);
    }

    @Benchmark
    public DecimalFactory<?> benchmarkDeriveFactoryByMetrics() {
        return factory.deriveFactory(scaleMetrics);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntValue);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecValue);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecValue, roundingMode);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal17f benchmarkParseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal17f benchmarkParseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfUnscaled() {
        return factory.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfUnscaledWithScale() {
        return factory.valueOfUnscaled(longValue, factory.getScale());
    }

    @Benchmark
    public Decimal17f benchmarkValueOfUnscaledWithScaleRounding() {
        return factory.valueOfUnscaled(longValue, factory.getScale(), roundingMode);
    }

    @Benchmark
    public Decimal17f[] benchmarkNewArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal17f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal17f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
