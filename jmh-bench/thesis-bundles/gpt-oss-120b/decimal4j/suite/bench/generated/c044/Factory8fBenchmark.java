package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory8f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.scale.Scale8f;
import org.decimal4j.scale.ScaleMetrics;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory8fBenchmark {

    private Factory8f factory;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigInt;
    private BigDecimal bigDec;
    private Decimal<?> decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private ScaleMetrics scaleMetrics;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory8f.INSTANCE;
        longValue = 123456789L;
        floatValue = 12345.6789f;
        doubleValue = 12345.67890123;
        bigInt = new BigInteger("123456789012345678901234567890");
        bigDec = new BigDecimal("1234567890.12345678");
        decimalValue = Decimal8f.valueOf(longValue);
        stringValue = "12345.67890123";
        roundingMode = RoundingMode.HALF_UP;
        scaleMetrics = Scale8f.INSTANCE;
    }

    @Benchmark
    public Decimal8f benchValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal8f benchValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal8f benchValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal8f benchValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal8f benchValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal8f benchValueOfBigInteger() {
        return factory.valueOf(bigInt);
    }

    @Benchmark
    public Decimal8f benchValueOfBigDecimal() {
        return factory.valueOf(bigDec);
    }

    @Benchmark
    public Decimal8f benchValueOfBigDecimalRounding() {
        return factory.valueOf(bigDec, roundingMode);
    }

    @Benchmark
    public Decimal8f benchValueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal8f benchValueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal8f benchParseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal8f benchParseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal8f benchValueOfUnscaledLong() {
        return factory.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal8f benchValueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(longValue, factory.getScale());
    }

    @Benchmark
    public Decimal8f benchValueOfUnscaledLongScaleRounding() {
        return factory.valueOfUnscaled(longValue, factory.getScale(), roundingMode);
    }

    @Benchmark
    public Scale8f benchGetScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int benchGetScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal8f> benchImmutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal8f> benchMutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public Object benchDeriveFactoryInt() {
        return factory.deriveFactory(8);
    }

    @Benchmark
    public Object benchDeriveFactoryScaleMetrics() {
        return factory.deriveFactory(scaleMetrics);
    }

    @Benchmark
    public Decimal8f[] benchNewArray() {
        return factory.newArray(10);
    }

    @Benchmark
    public MutableDecimal8f benchNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal8f[] benchNewMutableArray() {
        return factory.newMutableArray(10);
    }
}
