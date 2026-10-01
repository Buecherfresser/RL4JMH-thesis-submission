package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory7f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale7f;
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
public class Factory7fBenchmark {

    private Factory7f factory;
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecValue;
    private Decimal7f decimalValue;
    private String stringValue;
    private int arrayLength;
    private int unscaledScale;
    private RoundingMode roundingMode;
    private Scale7f scaleMetrics;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory7f.INSTANCE;
        longValue = 123456789L;
        floatValue = 12345.6789f;
        doubleValue = 12345.6789012;
        bigIntValue = new BigInteger("12345678901234567890");
        bigDecValue = new BigDecimal("12345.6789012");
        decimalValue = Decimal7f.valueOf(12345.6789012);
        stringValue = "12345.6789012";
        arrayLength = 16;
        unscaledScale = 7;
        roundingMode = RoundingMode.HALF_UP;
        scaleMetrics = Scale7f.INSTANCE;
    }

    @Benchmark
    public Decimal7f benchmarkValueOfLong() {
        return factory.valueOf(longValue);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfFloat() {
        return factory.valueOf(floatValue);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfFloatRounding() {
        return factory.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfDouble() {
        return factory.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfDoubleRounding() {
        return factory.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfBigInteger() {
        return factory.valueOf(bigIntValue);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfBigDecimal() {
        return factory.valueOf(bigDecValue);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfBigDecimalRounding() {
        return factory.valueOf(bigDecValue, roundingMode);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfDecimal() {
        return factory.valueOf((Decimal<?>) decimalValue);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfDecimalRounding() {
        return factory.valueOf((Decimal<?>) decimalValue, roundingMode);
    }

    @Benchmark
    public Decimal7f benchmarkParseString() {
        return factory.parse(stringValue);
    }

    @Benchmark
    public Decimal7f benchmarkParseStringRounding() {
        return factory.parse(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfUnscaled() {
        return factory.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfUnscaledWithScale() {
        return factory.valueOfUnscaled(longValue, unscaledScale);
    }

    @Benchmark
    public Decimal7f benchmarkValueOfUnscaledWithScaleRounding() {
        return factory.valueOfUnscaled(longValue, unscaledScale, roundingMode);
    }

    @Benchmark
    public Decimal7f[] benchmarkNewArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal7f benchmarkNewMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal7f[] benchmarkNewMutableArray() {
        return factory.newMutableArray(arrayLength);
    }

    @Benchmark
    public Object benchmarkDeriveFactoryInt() {
        return factory.deriveFactory(7);
    }

    @Benchmark
    public Object benchmarkDeriveFactoryScaleMetrics() {
        return factory.deriveFactory((ScaleMetrics) scaleMetrics);
    }
}
