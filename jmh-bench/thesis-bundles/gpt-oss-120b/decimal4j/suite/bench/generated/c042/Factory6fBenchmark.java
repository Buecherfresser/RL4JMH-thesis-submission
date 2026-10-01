package bench.generated.c042;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory6f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.scale.Scale6f;
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
public class Factory6fBenchmark {

    private Factory6f factory;

    private long longVal;
    private float floatVal;
    private double doubleVal;
    private BigInteger bigIntVal;
    private BigDecimal bigDecVal;
    private Decimal6f decimalVal;
    private String stringVal;
    private RoundingMode roundingMode;
    private int arrayLen;
    private Scale6f scaleMetrics;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory6f.INSTANCE;
        longVal = 123456789L;
        floatVal = 12345.678f;
        doubleVal = 1234567.890123;
        bigIntVal = new BigInteger("12345678901234567890");
        bigDecVal = new BigDecimal("1234567.890123456789");
        decimalVal = Decimal6f.valueOf(12345L);
        stringVal = "1234567.890123";
        roundingMode = RoundingMode.HALF_UP;
        arrayLen = 16;
        scaleMetrics = Scale6f.INSTANCE;
    }

    @Benchmark
    public Decimal6f benchmarkValueOfLong() {
        return factory.valueOf(longVal);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfFloat() {
        return factory.valueOf(floatVal);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatVal, roundingMode);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfDouble() {
        return factory.valueOf(doubleVal);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleVal, roundingMode);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntVal);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecVal);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecVal, roundingMode);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfDecimal() {
        return factory.valueOf(decimalVal);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfDecimalRounding() {
        return factory.valueOf(decimalVal, roundingMode);
    }

    @Benchmark
    public Decimal6f benchmarkParse() {
        return factory.parse(stringVal);
    }

    @Benchmark
    public Decimal6f benchmarkParseRounding() {
        return factory.parse(stringVal, roundingMode);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfUnscaled() {
        return factory.valueOfUnscaled(longVal);
    }

    @Benchmark
    public Decimal6f benchmarkValueOfUnscaledWithScale() {
        return factory.valueOfUnscaled(longVal, factory.getScale());
    }

    @Benchmark
    public Decimal6f benchmarkValueOfUnscaledWithScaleRounding() {
        return factory.valueOfUnscaled(longVal, factory.getScale(), roundingMode);
    }

    @Benchmark
    public Decimal6f[] benchmarkNewArray() {
        return factory.newArray(arrayLen);
    }

    @Benchmark
    public MutableDecimal6f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal6f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(arrayLen);
    }

    @Benchmark
    public Object benchmarkDeriveFactoryByInt() {
        return factory.deriveFactory(5);
    }

    @Benchmark
    public Object benchmarkDeriveFactoryByScaleMetrics() {
        return factory.deriveFactory(scaleMetrics);
    }

    @Benchmark
    public Scale6f benchmarkGetScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal6f> benchmarkImmutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal6f> benchmarkMutableType() {
        return factory.mutableType();
    }
}
