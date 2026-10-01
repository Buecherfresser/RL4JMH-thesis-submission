package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.decimal4j.factory.Factory5f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.api.Decimal;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory5fBenchmark {

    private long longValue;
    private double doubleValue;
    private float floatValue;
    private BigInteger bigIntegerValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private Decimal<?> decimalValue;
    private int scale;
    private int arraySize;

    @Setup(Level.Trial)
    public void setup() {
        longValue = 123456789L;
        doubleValue = 12345.67891;
        floatValue = 12345.678f;
        bigIntegerValue = BigInteger.valueOf(123456789L);
        bigDecimalValue = new BigDecimal("12345.67891");
        stringValue = "12345.67891";
        decimalValue = Decimal5f.valueOf("12345.67891");
        scale = 3;
        arraySize = 32;
    }

    @Benchmark
    public Decimal5f valueOfLong() {
        return Factory5f.INSTANCE.valueOf(longValue);
    }

    @Benchmark
    public Decimal5f valueOfFloat() {
        return Factory5f.INSTANCE.valueOf(floatValue);
    }

    @Benchmark
    public Decimal5f valueOfFloatRounding() {
        return Factory5f.INSTANCE.valueOf(floatValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal5f valueOfDouble() {
        return Factory5f.INSTANCE.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal5f valueOfDoubleRounding() {
        return Factory5f.INSTANCE.valueOf(doubleValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal5f valueOfBigInteger() {
        return Factory5f.INSTANCE.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal5f valueOfBigDecimal() {
        return Factory5f.INSTANCE.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal5f valueOfBigDecimalRounding() {
        return Factory5f.INSTANCE.valueOf(bigDecimalValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal5f valueOfDecimal() {
        return Factory5f.INSTANCE.valueOf(decimalValue);
    }

    @Benchmark
    public Decimal5f valueOfDecimalRounding() {
        return Factory5f.INSTANCE.valueOf(decimalValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal5f parse() {
        return Factory5f.INSTANCE.parse(stringValue);
    }

    @Benchmark
    public Decimal5f parseRounding() {
        return Factory5f.INSTANCE.parse(stringValue, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal5f valueOfUnscaled() {
        return Factory5f.INSTANCE.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal5f valueOfUnscaledWithScale() {
        return Factory5f.INSTANCE.valueOfUnscaled(longValue, scale);
    }

    @Benchmark
    public Decimal5f valueOfUnscaledWithScaleRounding() {
        return Factory5f.INSTANCE.valueOfUnscaled(longValue, scale, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal5f[] newArray() {
        return Factory5f.INSTANCE.newArray(arraySize);
    }

    @Benchmark
    public MutableDecimal5f newMutable() {
        return Factory5f.INSTANCE.newMutable();
    }

    @Benchmark
    public MutableDecimal5f[] newMutableArray() {
        return Factory5f.INSTANCE.newMutableArray(arraySize);
    }
}
