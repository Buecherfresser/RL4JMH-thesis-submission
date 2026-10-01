package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.generic.GenericDecimalFactory;
import org.decimal4j.generic.GenericImmutableDecimal;
import org.decimal4j.generic.GenericMutableDecimal;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal5f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GenericDecimalFactoryBenchmark {

    private GenericDecimalFactory<ScaleMetrics> factory;

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal<?> decimalValue;
    private String parseString;
    private RoundingMode roundingMode;
    private long unscaledValue;
    private int unscaledScale;

    @Setup(Level.Trial)
    public void setUp() {
        // Use Scale5f (scale = 5) as a representative scale
        factory = new GenericDecimalFactory<>(Scale5f.INSTANCE);

        longValue = 123456789L;
        floatValue = 12345.678f;
        doubleValue = 12345678.90123;
        bigIntegerValue = new BigInteger("9876543210123456789");
        bigDecimalValue = new BigDecimal("1234567890.123456789");
        decimalValue = Decimal5f.ONE; // immutable decimal constant
        parseString = "98765.43210";
        roundingMode = RoundingMode.HALF_UP;
        unscaledValue = 123456789012345L;
        unscaledScale = 5;
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntegerValue);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecimalValue);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkParse() {
        return factory.parse(parseString);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkParseRounding() {
        return factory.parse(parseString, roundingMode);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfUnscaledLong() {
        return factory.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(unscaledValue, unscaledScale);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics> benchmarkValueOfUnscaledLongScaleRounding() {
        return factory.valueOfUnscaled(unscaledValue, unscaledScale, roundingMode);
    }

    @Benchmark
    public GenericImmutableDecimal<ScaleMetrics>[] benchmarkNewArray() {
        return factory.newArray(10);
    }

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics> benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public GenericMutableDecimal<ScaleMetrics>[] benchmarkNewMutableArray() {
        return factory.newMutableArray(10);
    }
}
