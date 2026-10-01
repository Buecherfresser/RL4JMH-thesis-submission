package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.factory.Factory4f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.scale.Scale4f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory4fBenchmark {

    private Factory4f factory;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal4f decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private int length;
    private int scaleArg;
    private int scaleForUnscaled;
    private long unscaledLong;
    private Scale4f scaleMetrics;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory4f.INSTANCE;
        longValue = 123456789L;
        floatValue = 12345.6789f;
        doubleValue = 123456789.123456789;
        bigIntegerValue = new BigInteger("123456789012345678901234567890");
        bigDecimalValue = new BigDecimal("123456789.123456789");
        decimalValue = Decimal4f.valueOf(123456);
        stringValue = "123456789.1234";
        roundingMode = RoundingMode.HALF_UP;
        length = 10;
        scaleArg = 5;
        scaleForUnscaled = 2;
        unscaledLong = 1234567890L;
        scaleMetrics = Scale4f.INSTANCE;
    }

    @Benchmark
    public int benchmarkGetScale() {
        return factory.getScale();
    }

    @Benchmark
    public Scale4f benchmarkGetScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public Class<Decimal4f> benchmarkImmutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal4f> benchmarkMutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<?> benchmarkDeriveFactoryInt() {
        return factory.deriveFactory(scaleArg);
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<Scale4f> benchmarkDeriveFactoryScaleMetrics() {
        return factory.deriveFactory(scaleMetrics);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal4f benchmarkParse() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal4f benchmarkParseRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfUnscaledLong() {
        return factory.valueOfUnscaled(unscaledLong);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(unscaledLong, scaleForUnscaled);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfUnscaledLongScaleRounding() {
        return factory.valueOfUnscaled(unscaledLong, scaleForUnscaled, roundingMode);
    }

    @Benchmark
    public Decimal4f[] benchmarkNewArray() {
        return factory.newArray(length);
    }

    @Benchmark
    public MutableDecimal4f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal4f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(length);
    }
}
