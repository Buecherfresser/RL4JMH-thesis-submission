package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import org.decimal4j.api.Decimal;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.factory.Factory13f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.mutable.MutableDecimal13f;
import org.decimal4j.scale.Scale13f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory13fBenchmark {

    private long longValue;
    private double doubleValue;
    private float floatValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private Decimal13f decimal13fValue;
    private Decimal<?> decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private int arrayLength;
    private int scale;
    private long unscaledValue;
    private Scale13f scale13f;

    @Setup(Level.Trial)
    public void setup() {
        longValue = 123456789L;
        doubleValue = 1.234567890123456;
        floatValue = 1.2345679f;
        bigIntegerValue = BigInteger.valueOf(123456789L);
        bigDecimalValue = new BigDecimal("123456.7890123456789");
        decimal13fValue = Decimal13f.valueOf(123456.7890123456789);
        decimalValue = decimal13fValue; // Decimal13f implements Decimal<Scale13f>
        stringValue = "123456.7890123456789";
        roundingMode = RoundingMode.HALF_UP;
        arrayLength = 8;
        scale = 2;
        unscaledValue = 123456789L;
        scale13f = Scale13f.INSTANCE;
    }

    @Benchmark
    public Decimal13f valueOfLong() {
        return Factory13f.INSTANCE.valueOf(longValue);
    }

    @Benchmark
    public Decimal13f valueOfDouble() {
        return Factory13f.INSTANCE.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal13f valueOfDoubleRounding() {
        return Factory13f.INSTANCE.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal13f valueOfFloat() {
        return Factory13f.INSTANCE.valueOf(floatValue);
    }

    @Benchmark
    public Decimal13f valueOfFloatRounding() {
        return Factory13f.INSTANCE.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal13f valueOfBigInteger() {
        return Factory13f.INSTANCE.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal13f valueOfBigDecimal() {
        return Factory13f.INSTANCE.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal13f valueOfBigDecimalRounding() {
        return Factory13f.INSTANCE.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal13f valueOfDecimal() {
        return Factory13f.INSTANCE.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal13f valueOfDecimalRounding() {
        return Factory13f.INSTANCE.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal13f parseString() {
        return Factory13f.INSTANCE.parse(stringValue);
    }

    @Benchmark
    public Decimal13f parseStringRounding() {
        return Factory13f.INSTANCE.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal13f valueOfUnscaledLong() {
        return Factory13f.INSTANCE.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal13f valueOfUnscaledLongScale() {
        return Factory13f.INSTANCE.valueOfUnscaled(unscaledValue, scale);
    }

    @Benchmark
    public Decimal13f valueOfUnscaledLongScaleRounding() {
        return Factory13f.INSTANCE.valueOfUnscaled(unscaledValue, scale, roundingMode);
    }

    @Benchmark
    public Decimal13f[] newArray() {
        return Factory13f.INSTANCE.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal13f newMutable() {
        return Factory13f.INSTANCE.newMutable();
    }

    @Benchmark
    public MutableDecimal13f[] newMutableArray() {
        return Factory13f.INSTANCE.newMutableArray(arrayLength);
    }

    @Benchmark
    public int getScale() {
        return Factory13f.INSTANCE.getScale();
    }

    @Benchmark
    public Scale13f getScaleMetrics() {
        return Factory13f.INSTANCE.getScaleMetrics();
    }

    @Benchmark
    public Class<Decimal13f> immutableType() {
        return Factory13f.INSTANCE.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal13f> mutableType() {
        return Factory13f.INSTANCE.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryInt() {
        return Factory13f.INSTANCE.deriveFactory(10);
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryScaleMetrics() {
        return Factory13f.INSTANCE.deriveFactory(scale13f);
    }
}
