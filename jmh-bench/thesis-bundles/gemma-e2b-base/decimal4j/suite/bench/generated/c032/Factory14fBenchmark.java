package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory14f;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.mutable.MutableDecimal14f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Factory14fBenchmark {

    private Decimal14f immutableDecimal;
    private MutableDecimal14f mutableDecimal;
    private final long unscaledValue = 123456789012345L;
    private final BigDecimal bdInput = new BigDecimal("123456789012345.6789");

    @Setup
    public void setup() {
        // Setup immutable decimal
        this.immutableDecimal = Factory14f.INSTANCE.valueOf(unscaledValue);

        // Setup mutable decimal
        this.mutableDecimal = Factory14f.INSTANCE.newMutable();
        this.mutableDecimal.setUnscaled(unscaledValue);
    }

    // --- ValueOf Benchmarks ---

    @Benchmark
    public Decimal14f benchmarkValueOfLong() {
        return Factory14f.INSTANCE.valueOf(123456789012345L);
    }

    @Benchmark
    public Decimal14f benchmarkValueOfDouble() {
        return Factory14f.INSTANCE.valueOf(3.1415926535);
    }

    @Benchmark
    public Decimal14f benchmarkValueOfBigDecimal() {
        return Factory14f.INSTANCE.valueOf(bdInput);
    }

    @Benchmark
    public Decimal14f benchmarkValueOfFloat() {
        return Factory14f.INSTANCE.valueOf(1.2345f);
    }

    @Benchmark
    public Decimal14f benchmarkValueOfDoubleWithRounding() {
        return Factory14f.INSTANCE.valueOf(1.23456789, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal14f benchmarkValueOfBigDecimalWithRounding() {
        return Factory14f.INSTANCE.valueOf(bdInput, RoundingMode.HALF_DOWN);
    }

    @Benchmark
    public Decimal14f benchmarkValueOfDecimal() {
        Decimal14f input = Factory14f.INSTANCE.valueOf(100L);
        return Factory14f.INSTANCE.valueOf(input);
    }

    @Benchmark
    public Decimal14f benchmarkValueOfDecimalWithRounding() {
        Decimal14f input = Factory14f.INSTANCE.valueOf(100L, RoundingMode.CEILING);
        return Factory14f.INSTANCE.valueOf(input, RoundingMode.UP);
    }

    @Benchmark
    public Decimal14f benchmarkValueOfUnscaledLong() {
        return Factory14f.INSTANCE.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal14f benchmarkValueOfUnscaledLongWithScale() {
        return Factory14f.INSTANCE.valueOfUnscaled(unscaledValue, 14);
    }

    @Benchmark
    public Decimal14f benchmarkValueOfUnscaledLongWithScaleAndRounding() {
        return Factory14f.INSTANCE.valueOfUnscaled(unscaledValue, 14, RoundingMode.HALF_EVEN);
    }

    // --- Mutable Decimal Benchmarks ---

    @Benchmark
    public MutableDecimal14f benchmarkNewMutable() {
        return Factory14f.INSTANCE.newMutable();
    }

    @Benchmark
    public MutableDecimal14f benchmarkSetUnscaled() {
        MutableDecimal14f m = Factory14f.INSTANCE.newMutable();
        m.setUnscaled(unscaledValue);
        return m;
    }

    @Benchmark
    public MutableDecimal14f benchmarkSetZero() {
        MutableDecimal14f m = Factory14f.INSTANCE.newMutable();
        m.setZero();
        return m;
    }

    @Benchmark
    public MutableDecimal14f benchmarkSetOne() {
        MutableDecimal14f m = Factory14f.INSTANCE.newMutable();
        m.setOne();
        return m;
    }

    @Benchmark
    public MutableDecimal14f benchmarkSetMinusOne() {
        MutableDecimal14f m = Factory14f.INSTANCE.newMutable();
        m.setMinusOne();
        return m;
    }
}
