package bench.generated.c049;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.exact.Multipliable0f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.factory.Factory0f;
import org.decimal4j.scale.Scale0f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal0fBenchmark {

    private long longValue;
    private double doubleValue;
    private BigDecimal bigDecimalValue;
    private BigInteger bigIntegerValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private int scale;
    private Decimal0f sample;

    @Setup(Level.Trial)
    public void setUp() {
        longValue = 123456789L;
        doubleValue = 123456789.987;
        bigDecimalValue = new BigDecimal("123456789.987654321");
        bigIntegerValue = new BigInteger("12345678901234567890");
        stringValue = "123456789.987654321";
        roundingMode = RoundingMode.HALF_UP;
        scale = 2;
        sample = Decimal0f.valueOf(42L);
    }

    @Benchmark
    public Decimal0f benchValueOfLong() {
        return Decimal0f.valueOf(longValue);
    }

    @Benchmark
    public Decimal0f benchValueOfDouble() {
        return Decimal0f.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal0f benchValueOfBigDecimal() {
        return Decimal0f.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal0f benchValueOfBigInteger() {
        return Decimal0f.valueOf(bigIntegerValue);
    }

    @Benchmark
    public Decimal0f benchValueOfString() {
        return Decimal0f.valueOf(stringValue);
    }

    @Benchmark
    public Decimal0f benchValueOfLongWithRounding() {
        return Decimal0f.valueOf(longValue, roundingMode);
    }

    @Benchmark
    public Decimal0f benchValueOfDoubleWithRounding() {
        return Decimal0f.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal0f benchValueOfBigDecimalWithRounding() {
        return Decimal0f.valueOf(bigDecimalValue, roundingMode);
    }

    @Benchmark
    public Decimal0f benchValueOfStringWithRounding() {
        return Decimal0f.valueOf(stringValue, roundingMode);
    }

    @Benchmark
    public Decimal0f benchValueOfUnscaled() {
        return Decimal0f.valueOfUnscaled(longValue);
    }

    @Benchmark
    public Decimal0f benchValueOfUnscaledWithScale() {
        return Decimal0f.valueOfUnscaled(longValue, scale);
    }

    @Benchmark
    public Decimal0f benchValueOfUnscaledWithRounding() {
        return Decimal0f.valueOfUnscaled(longValue, scale, roundingMode);
    }

    @Benchmark
    public Multipliable0f benchMultiplyExact() {
        return sample.multiplyExact();
    }

    @Benchmark
    public MutableDecimal0f benchToMutableDecimal() {
        return sample.toMutableDecimal();
    }

    @Benchmark
    public Decimal0f benchToImmutableDecimal() {
        return sample.toImmutableDecimal();
    }

    @Benchmark
    public int benchGetScale() {
        return sample.getScale();
    }

    @Benchmark
    public Scale0f benchGetScaleMetrics() {
        return sample.getScaleMetrics();
    }

    @Benchmark
    public Factory0f benchGetFactory() {
        return sample.getFactory();
    }
}
