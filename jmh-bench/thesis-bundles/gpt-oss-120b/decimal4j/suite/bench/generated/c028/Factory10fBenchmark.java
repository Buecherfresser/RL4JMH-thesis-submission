package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory10f;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.scale.Scale10f;
import org.decimal4j.scale.ScaleMetrics;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory10fBenchmark {

    private final Factory10f factory = Factory10f.INSTANCE;

    private long longVal;
    private float floatVal;
    private double doubleVal;
    private BigInteger bigIntVal;
    private BigDecimal bigDecimalVal;
    private Decimal10f decimalVal;
    private RoundingMode roundingMode;
    private int arrayLength;
    private Scale10f scaleMetrics;

    @Setup(Level.Trial)
    public void setUp() {
        longVal = 123456789L;
        floatVal = 12345.6789f;
        doubleVal = 123456789.012345;
        bigIntVal = new BigInteger("12345678901234567890");
        bigDecimalVal = new BigDecimal("1234567890.1234567890");
        decimalVal = Decimal10f.valueOf(longVal);
        roundingMode = RoundingMode.HALF_UP;
        arrayLength = 16;
        scaleMetrics = Scale10f.INSTANCE;
    }

    @Benchmark
    public Scale10f benchGetScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int benchGetScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal10f> benchImmutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal10f> benchMutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> benchDeriveFactoryByInt() {
        return factory.deriveFactory(5);
    }

    @Benchmark
    public DecimalFactory<Scale10f> benchDeriveFactoryByMetrics() {
        return factory.deriveFactory(scaleMetrics);
    }

    @Benchmark
    public Decimal10f benchValueOfLong() {
        return factory.valueOf(longVal);
    }

    @Benchmark
    public Decimal10f benchValueOfFloat() {
        return factory.valueOf(floatVal);
    }

    @Benchmark
    public Decimal10f benchValueOfFloatRounding() {
        return factory.valueOf(floatVal, roundingMode);
    }

    @Benchmark
    public Decimal10f benchValueOfDouble() {
        return factory.valueOf(doubleVal);
    }

    @Benchmark
    public Decimal10f benchValueOfDoubleRounding() {
        return factory.valueOf(doubleVal, roundingMode);
    }

    @Benchmark
    public Decimal10f benchValueOfBigInteger() {
        return factory.valueOf(bigIntVal);
    }

    @Benchmark
    public Decimal10f benchValueOfBigDecimal() {
        return factory.valueOf(bigDecimalVal);
    }

    @Benchmark
    public Decimal10f benchValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecimalVal, roundingMode);
    }

    @Benchmark
    public Decimal10f benchValueOfDecimal() {
        return factory.valueOf(decimalVal);
    }

    @Benchmark
    public Decimal10f benchValueOfDecimalRounding() {
        return factory.valueOf(decimalVal, roundingMode);
    }

    @Benchmark
    public Decimal10f benchParse() {
        return factory.parse(decimalVal.toString());
    }

    @Benchmark
    public Decimal10f benchParseRounding() {
        return factory.parse(decimalVal.toString(), roundingMode);
    }

    @Benchmark
    public Decimal10f benchValueOfUnscaled() {
        return factory.valueOfUnscaled(longVal);
    }

    @Benchmark
    public Decimal10f benchValueOfUnscaledWithScale() {
        return factory.valueOfUnscaled(longVal, factory.getScale());
    }

    @Benchmark
    public Decimal10f benchValueOfUnscaledWithScaleRounding() {
        return factory.valueOfUnscaled(longVal, factory.getScale(), roundingMode);
    }

    @Benchmark
    public Decimal10f[] benchNewArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal10f benchNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal10f[] benchNewMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
