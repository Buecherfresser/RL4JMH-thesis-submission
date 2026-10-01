package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory9f;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.api.Decimal;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory9fBenchmark {

    private Factory9f factory;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecValue;
    private Decimal<?> decimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private int arrayLength;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory9f.INSTANCE;
        longValue = 123456789L;
        floatValue = 12345.678f;
        doubleValue = 12345678.9012345;
        bigIntValue = new BigInteger("12345678901234567890");
        bigDecValue = new BigDecimal("1234567890.123456789");
        decimalValue = factory.valueOf(longValue);
        stringValue = "1234567.890123456";
        roundingMode = RoundingMode.HALF_UP;
        arrayLength = 10;
    }

    @Benchmark
    public Decimal9f benchValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal9f benchValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal9f benchValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal9f benchValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal9f benchValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal9f benchValueOfBigInteger() {
        return factory.valueOf(bigIntValue);
    }

    @Benchmark
    public Decimal9f benchValueOfBigDecimal() {
        return factory.valueOf(bigDecValue);
    }

    @Benchmark
    public Decimal9f benchValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecValue, roundingMode);
    }

    @Benchmark
    public Decimal9f benchValueOfDecimal() {
        return factory.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal9f benchValueOfDecimalRounding() {
        return factory.valueOf(decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal9f benchParseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal9f benchParseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal9f benchValueOfUnscaledLong() {
        return factory.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal9f benchValueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(longValue, factory.getScale());
    }

    @Benchmark
    public Decimal9f benchValueOfUnscaledLongScaleRounding() {
        return factory.valueOfUnscaled(longValue, factory.getScale(), roundingMode);
    }

    @Benchmark
    public Decimal9f[] benchNewArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal9f benchNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal9f[] benchNewMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}
