package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory13f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.mutable.MutableDecimal13f;
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale13f;
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
public class Factory13fBenchmark {

    private Factory13f factory = Factory13f.INSTANCE;

    private long longVal;
    private float floatVal;
    private double doubleVal;
    private BigInteger bigIntVal;
    private BigDecimal bigDecVal;
    private Decimal13f decimalVal;
    private String stringVal;
    private int arrayLen;
    private ScaleMetrics scaleMetrics;

    @Setup(Level.Trial)
    public void setUp() {
        longVal = 1234567890123L;
        floatVal = 12345.678f;
        doubleVal = 12345678.90123;
        bigIntVal = new BigInteger("12345678901234567890");
        bigDecVal = new BigDecimal("1234567890.1234567890123");
        decimalVal = Decimal13f.valueOf(42L);
        stringVal = "1234567.8901234567890";
        arrayLen = 16;
        scaleMetrics = Scale13f.INSTANCE;
    }

    @Benchmark
    public Decimal13f benchmarkValueOfLong() {
        return factory.valueOf(longVal);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfFloat() {
        return factory.valueOf(floatVal);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatVal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfDouble() {
        return factory.valueOf(doubleVal);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleVal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntVal);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecVal);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecVal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfDecimal() {
        return factory.valueOf((Decimal<?>) decimalVal);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfDecimalRounding() {
        return factory.valueOf((Decimal<?>) decimalVal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal13f benchmarkParseString() {
        return factory.parse(stringVal);
    }

    @Benchmark
    public Decimal13f benchmarkParseStringRounding() {
        return factory.parse(stringVal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfUnscaledLong() {
        return factory.valueOfUnscaled(longVal);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(longVal, 13);
    }

    @Benchmark
    public Decimal13f benchmarkValueOfUnscaledLongScaleRounding() {
        return factory.valueOfUnscaled(longVal, 13, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal13f[] benchmarkNewArray() {
        return factory.newArray(arrayLen);
    }

    @Benchmark
    public MutableDecimal13f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal13f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(arrayLen);
    }

    @Benchmark
    public Object benchmarkDeriveFactoryByInt() {
        return factory.deriveFactory(13);
    }

    @Benchmark
    public Object benchmarkDeriveFactoryByScaleMetrics() {
        return factory.deriveFactory(scaleMetrics);
    }
}
