package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory7f;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.scale.Scale7f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory7fBenchmark {

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private BigDecimal bigDecimalRoundingValue;
    private Decimal<?> decimalValue;
    private String stringValue;
    private String stringRoundingValue;
    private RoundingMode roundingMode;
    private Scale7f scale7f;
    private int arrayLength;
    private int scaleInt;
    private long unscaledValue;
    private int targetScale;

    @Setup(Level.Trial)
    public void setup() {
        longValue = 123456789L;
        floatValue = 123.456f;
        doubleValue = 123456.789012345;
        bigIntegerValue = BigInteger.valueOf(123456789L);
        bigDecimalValue = new BigDecimal("123456789.0123456");
        bigDecimalRoundingValue = new BigDecimal("123456789.01234567");
        decimalValue = Decimal7f.valueOf(123456789L);
        stringValue = "123456789.0123456";
        stringRoundingValue = "123456789.01234567";
        roundingMode = RoundingMode.HALF_UP;
        scale7f = Scale7f.INSTANCE;
        arrayLength = 16;
        scaleInt = 3;
        unscaledValue = 123456789L;
        targetScale = 3;
    }

    @Benchmark
    public Scale7f getScaleMetrics() {
        return Factory7f.INSTANCE.getScaleMetrics();
    }

    @Benchmark
    public int getScale() {
        return Factory7f.INSTANCE.getScale();
    }

    @Benchmark
    public Class<Decimal7f> immutableType() {
        return Factory7f.INSTANCE.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal7f> mutableType() {
        return Factory7f.INSTANCE.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryInt() {
        return Factory7f.INSTANCE.deriveFactory(scaleInt);
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryScaleMetrics() {
        return Factory7f.INSTANCE.deriveFactory(scale7f);
    }

    @Benchmark
    public Decimal7f valueOfLong() {
        return Factory7f.INSTANCE.valueOf(longValue);
    }

    @Benchmark
    public Decimal7f valueOfFloat() {
        return Factory7f.INSTANCE.valueOf(floatValue);
    }

    @Benchmark
    public Decimal7f valueOfFloatRounding() {
        return Factory7f.INSTANCE.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal7f valueOfDouble() {
        return Factory7f.INSTANCE.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal7f valueOfDoubleRounding() {
        return Factory7f.INSTANCE.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal7f valueOfBigInteger() {
        return Factory7f.INSTANCE.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal7f valueOfBigDecimal() {
        return Factory7f.INSTANCE.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal7f valueOfBigDecimalRounding() {
        return Factory7f.INSTANCE.valueOf(bigDecimalRoundingValue, roundingMode);
    }

    @Benchmark
    public Decimal7f valueOfDecimal() {
        return Factory7f.INSTANCE.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal7f valueOfDecimalRounding() {
        return Factory7f.INSTANCE.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal7f parseString() {
        return Factory7f.INSTANCE.parse(stringValue);
    }

    @Benchmark
    public Decimal7f parseStringRounding() {
        return Factory7f.INSTANCE.parse(stringRoundingValue, roundingMode);
    }

    @Benchmark
    public Decimal7f valueOfUnscaled() {
        return Factory7f.INSTANCE.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal7f valueOfUnscaledScale() {
        return Factory7f.INSTANCE.valueOfUnscaled(unscaledValue, targetScale);
    }

    @Benchmark
    public Decimal7f valueOfUnscaledScaleRounding() {
        return Factory7f.INSTANCE.valueOfUnscaled(unscaledValue, targetScale, roundingMode);
    }

    @Benchmark
    public Decimal7f[] newArray() {
        return Factory7f.INSTANCE.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal7f newMutable() {
        return Factory7f.INSTANCE.newMutable();
    }

    @Benchmark
    public MutableDecimal7f[] newMutableArray() {
        return Factory7f.INSTANCE.newMutableArray(arrayLength);
    }
}
