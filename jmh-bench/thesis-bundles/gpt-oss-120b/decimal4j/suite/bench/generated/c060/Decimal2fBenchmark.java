package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal2f;
import java.math.RoundingMode;
import org.decimal4j.exact.Multipliable2f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.scale.Scale2f;
import org.decimal4j.factory.Factory2f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal2fBenchmark {

    private Decimal2f decimalInstance;
    private long longValue;
    private double doubleValue;
    private String stringValue;
    private long unscaledValue;
    private int unscaledScale;

    @Setup(Level.Trial)
    public void setup() {
        decimalInstance = Decimal2f.TEN;
        longValue = 12345L;
        doubleValue = 12345.67;
        stringValue = "12345.67";
        unscaledValue = 1234567L; // represents 12345.67 at scale 2
        unscaledScale = 2;
    }

    @Benchmark
    public Decimal2f benchmarkValueOfLong() {
        return Decimal2f.valueOf(longValue);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfDouble() {
        return Decimal2f.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfString() {
        return Decimal2f.valueOf(stringValue);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfUnscaled() {
        return Decimal2f.valueOfUnscaled(unscaledValue);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfUnscaledWithScale() {
        return Decimal2f.valueOfUnscaled(unscaledValue, unscaledScale);
    }

    @Benchmark
    public Decimal2f benchmarkValueOfUnscaledWithRounding() {
        return Decimal2f.valueOfUnscaled(unscaledValue, unscaledScale, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Multipliable2f benchmarkMultiplyExact() {
        return decimalInstance.multiplyExact();
    }

    @Benchmark
    public MutableDecimal2f benchmarkToMutable() {
        return decimalInstance.toMutableDecimal();
    }

    @Benchmark
    public Decimal2f benchmarkToImmutable() {
        return decimalInstance.toImmutableDecimal();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return decimalInstance.getScale();
    }

    @Benchmark
    public Scale2f benchmarkGetScaleMetrics() {
        return decimalInstance.getScaleMetrics();
    }

    @Benchmark
    public Factory2f benchmarkGetFactory() {
        return decimalInstance.getFactory();
    }
}
