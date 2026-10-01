package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory17f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.mutable.MutableDecimal17f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory17fBenchmark {

    private Decimal17f immutableValue;
    private MutableDecimal17f mutableValue;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private long unscaledLongInput;
    private long unscaledLongForScaleInput;

    @Setup
    public void setup() {
        // Setup immutable values
        this.immutableValue = Factory17f.INSTANCE.valueOf(123.4567890123456789);
        this.bigDecimalInput = new BigDecimal("123.4567890123456789");
        this.stringInput = "test string for parsing";
        this.unscaledLongInput = 9223372036854775807L;
        this.unscaledLongForScaleInput = 123456789012345L;

        // Setup mutable value instance (we will create a fresh one per benchmark if needed, 
        // but we can initialize a base state here if the benchmark relies on it)
        this.mutableValue = Factory17f.INSTANCE.newMutable();
        this.mutableValue.setUnscaled(this.unscaledLongInput);
    }

    // --- Immutable ValueOf Benchmarks ---

    @Benchmark
    public Decimal17f benchmarkValueOfLong() {
        return Factory17f.INSTANCE.valueOf(1234567890123456789L);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfDouble() {
        return Factory17f.INSTANCE.valueOf(3.141592653589793);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfBigDecimal() {
        return Factory17f.INSTANCE.valueOf(bigDecimalInput);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfString() {
        return Factory17f.INSTANCE.parse(stringInput);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfWithRoundingMode() {
        return Factory17f.INSTANCE.valueOf(new BigDecimal("1.234567890123456789"), RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfUnscaledLong() {
        return Factory17f.INSTANCE.valueOfUnscaled(unscaledLongInput);
    }

    @Benchmark
    public Decimal17f benchmarkValueOfUnscaledWithScale() {
        return Factory17f.INSTANCE.valueOfUnscaled(unscaledLongForScaleInput, 17);
    }

    // --- Mutable Value Benchmarks ---

    @Benchmark
    public MutableDecimal17f benchmarkNewMutable() {
        return Factory17f.INSTANCE.newMutable();
    }

    @Benchmark
    public MutableDecimal17f benchmarkSetUnscaled() {
        // Create a fresh mutable object for this invocation to avoid state contamination
        MutableDecimal17f m = Factory17f.INSTANCE.newMutable();
        m.setUnscaled(unscaledLongInput);
        return m;
    }
}
