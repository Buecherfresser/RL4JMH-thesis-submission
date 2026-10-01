package bench.generated.c057;

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
import org.decimal4j.immutable.Decimal17f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable17f;
import org.decimal4j.scale.Scale17f;
import org.decimal4j.factory.Factory17f;
import org.decimal4j.mutable.MutableDecimal17f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal17fBenchmark {

    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigInteger;
    private BigDecimal bigDecimal;
    private String decimalString;
    private long unscaledValue;
    private Decimal17f instance;
    private RoundingMode roundingMode;

    @Setup(Level.Trial)
    public void setup() {
        longValue = 12345L;
        floatValue = 12345.6789f;
        doubleValue = 12345.67890123456789d;
        bigInteger = new BigInteger("12345678901234567890");
        bigDecimal = new BigDecimal("12345.67890123456789");
        decimalString = "12345.67890123456789";
        unscaledValue = 12345678901234567L;
        instance = Decimal17f.ONE;
        roundingMode = RoundingMode.HALF_UP;
    }

    @Benchmark
    public Decimal17f benchmarkValueOfLong() {
        return Decimal17f.valueOf(longValue);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfLongCache() {
        return Decimal17f.valueOf(5L);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfFloat() {
        return Decimal17f.valueOf(floatValue);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfFloatRounding() {
        return Decimal17f.valueOf(floatValue, roundingMode);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfDouble() {
        return Decimal17f.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfDoubleRounding() {
        return Decimal17f.valueOf(doubleValue, roundingMode);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfBigInteger() {
        return Decimal17f.valueOf(bigInteger);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfBigDecimal() {
        return Decimal17f.valueOf(bigDecimal);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfBigDecimalRounding() {
        return Decimal17f.valueOf(bigDecimal, roundingMode);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfDecimal() {
        Decimal<?> dec = instance;
        return Decimal17f.valueOf(dec);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfDecimalRounding() {
        Decimal<?> dec = instance;
        return Decimal17f.valueOf(dec, roundingMode);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfString() {
        return Decimal17f.valueOf(decimalString);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfStringRounding() {
        return Decimal17f.valueOf(decimalString, roundingMode);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfUnscaled() {
        return Decimal17f.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfUnscaledWithScale() {
        return Decimal17f.valueOfUnscaled(unscaledValue, 10);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfUnscaledWithScaleRounding() {
        return Decimal17f.valueOfUnscaled(unscaledValue, 10, roundingMode);
    }

    @Benchmark
    public MutableDecimal17f benchmarkToMutable() {
        return instance.toMutableDecimal();
    }

    @Benchmark
    public Multipliable17f benchmarkMultiplyExact() {
        return instance.multiplyExact();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return instance.getScale();
    }

    @Benchmark
    public Scale17f benchmarkGetScaleMetrics() {
        return instance.getScaleMetrics();
    }

    @Benchmark
    public Factory17f benchmarkGetFactory() {
        return instance.getFactory();
    }

    @Benchmark
    public Decimal17f benchmarkToImmutable() {
        return instance.toImmutableDecimal();
    }
}
