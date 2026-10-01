package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.factory.Factory11f;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal11f;
import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.scale.Scale11f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory11fBenchmark {

    private Factory11f factory;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal<?> decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private int scale;
    private Scale11f scaleMetrics;
    private int arrayLength;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory11f.INSTANCE;
        longValue = 123456789L;
        floatValue = 123.456f;
        doubleValue = 123456789.123456789;
        bigIntegerValue = new BigInteger("123456789012345678901234567890");
        bigDecimalValue = new BigDecimal("12345678901234567890.12345678901");
        decimalValue = Decimal11f.valueOf(123.456);
        stringValue = "12345678901234567890.12345678901";
        roundingMode = RoundingMode.HALF_UP;
        scale = 5;
        scaleMetrics = Scale11f.INSTANCE;
        arrayLength = 16;
    }

    @Benchmark
    public int getScale() {
        return factory.getScale();
    }

    @Benchmark
    public Scale11f getScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public Class<Decimal11f> immutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal11f> mutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryByInt() {
        return factory.deriveFactory(scale);
    }

    @Benchmark
    public DecimalFactory<Scale11f> deriveFactoryByScaleMetrics() {
        return factory.deriveFactory(scaleMetrics);
    }

    @Benchmark
    public Decimal11f valueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal11f valueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal11f valueOfFloatWithRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal11f valueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal11f valueOfDoubleWithRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal11f valueOfBigInteger() {
        return factory.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal11f valueOfBigDecimal() {
        return factory.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal11f valueOfBigDecimalWithRounding() {
        return factory.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal11f valueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal11f valueOfDecimalWithRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal11f parseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal11f parseStringWithRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal11f valueOfUnscaled() {
        return factory.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal11f valueOfUnscaledWithScale() {
        return factory.valueOfUnscaled(longValue, scale);
    }

    @Benchmark
    public Decimal11f valueOfUnscaledWithScaleAndRounding() {
        return factory.valueOfUnscaled(longValue, scale, roundingMode);
    }

    @Benchmark
    public Decimal11f[] newArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal11f newMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal11f[] newMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
