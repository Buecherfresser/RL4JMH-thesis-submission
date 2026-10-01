package bench.generated.c027;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory0f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.mutable.MutableDecimal0f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory0fBenchmark {

    private Factory0f factory;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecValue;
    private Decimal0f decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private int arrayLength;
    private int unscaledScale;
    private long unscaledValue;

    @Setup(Level.Trial)
    public void setUp() {
        factory = Factory0f.INSTANCE;
        longValue = 123456789L;
        floatValue = 12345.67f;
        doubleValue = 1234567.89;
        bigIntValue = new BigInteger("12345678901234567890");
        bigDecValue = new BigDecimal("1234567890.12345");
        decimalValue = factory.valueOf(1L);
        stringValue = "12345";
        roundingMode = RoundingMode.HALF_UP;
        arrayLength = 10;
        unscaledScale = 0;
        unscaledValue = 987654321L;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal0f benchmarkValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal0f benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal0f benchmarkValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal0f benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal0f benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntValue);
    }

    @Benchmark
    public Decimal0f benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecValue);
    }

    @Benchmark
    public Decimal0f benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecValue, roundingMode);
    }

    @Benchmark
    public Decimal0f benchmarkValueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal0f benchmarkValueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal0f benchmarkParse() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal0f benchmarkParseRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal0f benchmarkValueOfUnscaled() {
        return factory.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal0f benchmarkValueOfUnscaledWithScale() {
        return factory.valueOfUnscaled(unscaledValue, unscaledScale);
    }

    @Benchmark
    public Decimal0f benchmarkValueOfUnscaledWithScaleRounding() {
        return factory.valueOfUnscaled(unscaledValue, unscaledScale, roundingMode);
    }

    @Benchmark
    public Decimal0f[] benchmarkNewArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal0f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal0f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
