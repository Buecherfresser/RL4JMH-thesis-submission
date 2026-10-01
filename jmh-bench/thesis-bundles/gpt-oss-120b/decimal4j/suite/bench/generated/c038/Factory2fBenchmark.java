package bench.generated.c038;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory2f;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scale2f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory2fBenchmark {

    private Factory2f factory;

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal<?> decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private ScaleMetrics scaleMetrics;
    private int arrayLength;

    @Setup(Level.Trial)
    public void setUp() {
        factory = Factory2f.INSTANCE;
        longValue = 123456L;
        floatValue = 12345.67f;
        doubleValue = 12345.6789;
        bigIntegerValue = new BigInteger("12345678901234567890");
        bigDecimalValue = new BigDecimal("1234567890.12345");
        decimalValue = Decimal2f.valueOf(12345L);
        stringValue = "12345.67";
        roundingMode = RoundingMode.HALF_UP;
        scaleMetrics = Scale2f.INSTANCE;
        arrayLength = 10;
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
    public Class<Decimal2f> benchmarkImmutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal2f> benchmarkMutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> benchmarkDeriveFactoryInt() {
        return factory.deriveFactory(3);
    }

    @Benchmark
    public DecimalFactory<?> benchmarkDeriveFactoryMetrics() {
        return factory.deriveFactory(scaleMetrics);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal2f benchmarkParseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal2f benchmarkParseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfUnscaledLong() {
        return factory.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(longValue, 2);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfUnscaledLongScaleRounding() {
        return factory.valueOfUnscaled(longValue, 2, roundingMode);
    }

    @Benchmark
    public Decimal2f[] benchmarkNewArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal2f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal2f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
