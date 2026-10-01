package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factory18f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal18f;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.scale.ScaleMetrics;

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
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal<?> decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private int scale;
    private ScaleMetrics scaleMetrics;
    private int arrayLength;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory18f.INSTANCE;
        longValue = 123456789L;
        floatValue = 123.456f;
        doubleValue = 123.4567890123456789;
        bigIntegerValue = new BigInteger("123456789012345678901234567890");
        bigDecimalValue = new BigDecimal("12345678901234567890.123456789012345678");
        decimalValue = Decimal18f.valueOf("12345678901234567890.123456789012345678");
        stringValue = "12345678901234567890.123456789012345678";
        roundingMode = RoundingMode.HALF_UP;
        scale = 5;
        scaleMetrics = Scale5f.INSTANCE;
        arrayLength = 10;
    }

    @Benchmark
    public Scale18f getScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int getScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal18f> immutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal18f> mutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryInt() {
        return factory.deriveFactory(scale);
    }

    @Benchmark
    public DecimalFactory<ScaleMetrics> deriveFactoryScaleMetrics() {
        return factory.deriveFactory(scaleMetrics);
    }

    @Benchmark
    public Decimal18f valueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal18f valueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal18f valueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal18f valueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal18f valueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal18f valueOfBigInteger() {
        return factory.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal18f valueOfBigDecimal() {
        return factory.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal18f valueOfBigDecimalRounding() {
        return factory.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal18f valueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal18f valueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal18f parseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal18f parseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal18f valueOfUnscaledLong() {
        return factory.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal18f valueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(longValue, scale);
    }

    @Benchmark
    public Decimal18f valueOfUnscaledLongScaleRounding() {
        return factory.valueOfUnscaled(longValue, scale, roundingMode);
    }

    @Benchmark
    public Decimal18f[] newArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal18f newMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal18f[] newMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
