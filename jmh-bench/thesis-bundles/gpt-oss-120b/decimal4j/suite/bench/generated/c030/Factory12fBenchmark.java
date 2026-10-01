package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory12f;
import org.decimal4j.immutable.Decimal12f;
import org.decimal4j.mutable.MutableDecimal12f;
import org.decimal4j.scale.Scale12f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory12fBenchmark {

    private final Factory12f factory = Factory12f.INSTANCE;

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecValue;
    private Decimal12f decimalValue;
    private RoundingMode roundingMode;
    private Scale12f scaleMetrics;
    private String parseString;
    private long unscaledValue;
    private int arrayLength;

    @Setup(Level.Trial)
    public void setUp() {
        longValue = 123456789012L;
        floatValue = 12345.6789f;
        doubleValue = 12345.6789012345;
        bigIntValue = new BigInteger("12345678901234567890");
        bigDecValue = new BigDecimal("12345.67890123456789012345");
        decimalValue = Decimal12f.valueOf(longValue);
        roundingMode = RoundingMode.HALF_UP;
        scaleMetrics = Scale12f.INSTANCE;
        parseString = "12345.678901234567";
        unscaledValue = 12345678901234L;
        arrayLength = 10;
    }

    @Benchmark
    public int benchmarkGetScale() {
        return factory.getScale();
    }

    @Benchmark
    public Scale12f benchmarkGetScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public Class<Decimal12f> benchmarkImmutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal12f> benchmarkMutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public Object benchmarkDeriveFactoryByInt() {
        return factory.deriveFactory(12);
    }

    @Benchmark
    public Object benchmarkDeriveFactoryByMetrics() {
        return factory.deriveFactory(scaleMetrics);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntValue);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecValue);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecValue, roundingMode);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal12f benchmarkParseString() {
        return factory.parse(parseString);
    }

    @Benchmark
    public Decimal12f benchmarkParseStringRounding() {
        return factory.parse(parseString, roundingMode);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfUnscaled() {
        return factory.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfUnscaledWithScale() {
        return factory.valueOfUnscaled(unscaledValue, 12);
    }

    @Benchmark
    public Decimal12f benchmarkValueOfUnscaledWithScaleRounding() {
        return factory.valueOfUnscaled(unscaledValue, 12, roundingMode);
    }

    @Benchmark
    public Decimal12f[] benchmarkNewArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal12f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal12f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
