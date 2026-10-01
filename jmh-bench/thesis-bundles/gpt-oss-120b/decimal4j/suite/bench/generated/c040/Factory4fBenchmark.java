package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory4f;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.scale.Scale4f;
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
public class Factory4fBenchmark {

    private Factory4f factory;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal<?> decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private int scale;

    @Setup(Level.Trial)
    public void setUp() {
        factory = Factory4f.INSTANCE;
        longValue = 123456789L;
        floatValue = 12345.6789f;
        doubleValue = 12345.6789d;
        bigIntegerValue = new BigInteger("12345678901234567890");
        bigDecimalValue = new BigDecimal("12345.6789012345");
        decimalValue = Decimal4f.valueOf(98765L);
        stringValue = "12345.6789";
        roundingMode = RoundingMode.HALF_UP;
        scale = 4;
    }

    @Benchmark
    public Scale4f benchmarkGetScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return factory.getScale();
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
    public DecimalFactory<?> benchmarkDeriveFactoryByInt() {
        return factory.deriveFactory(scale);
    }

    @Benchmark
    public DecimalFactory<ScaleMetrics> benchmarkDeriveFactoryByMetrics() {
        ScaleMetrics metrics = factory.getScaleMetrics();
        return factory.deriveFactory(metrics);
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
    public Decimal4f benchmarkParseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal4f benchmarkParseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfUnscaledLong() {
        return factory.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(longValue, scale);
    }

    @Benchmark
    public Decimal4f benchmarkValueOfUnscaledLongScaleRounding() {
        return factory.valueOfUnscaled(longValue, scale, roundingMode);
    }

    @Benchmark
    public Decimal4f[] benchmarkNewArray() {
        return factory.newArray(10);
    }

    @Benchmark
    public MutableDecimal4f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal4f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(10);
    }
}
