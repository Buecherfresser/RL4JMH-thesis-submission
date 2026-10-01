package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory15fBenchmark {

    // Since Factory15f is an enum with a static INSTANCE, we rely on static calls.

    @Setup
    public void setup() {
        // Prepare a fixed payload for operations that might involve I/O or complex parsing.
    }

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Test immutable valueOf(long value)
        Factory15f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Test immutable valueOf(double value)
        Factory15f.INSTANCE.valueOf(3.1415926535);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Test immutable valueOf(BigDecimal value)
        BigDecimal bd = new BigDecimal("123.4567890123456789");
        Factory15f.INSTANCE.valueOf(bd);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        // Test immutable valueOf(float value)
        Factory15f.INSTANCE.valueOf((float) 1.0f);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        // Test immutable valueOf(double value, RoundingMode roundingMode)
        Factory15f.INSTANCE.valueOf(1.23456789, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        // Test immutable parse(String value)
        Factory15f.INSTANCE.parse("1234567890123456789");
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkParseStringWithRounding(Blackhole bh) {
        // Test immutable parse(String value, RoundingMode roundingMode)
        Factory15f.INSTANCE.parse("123.4567890123456789", RoundingMode.HALF_EVEN);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        // Test immutable valueOfUnscaled(long unscaledValue)
        Factory15f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongWithScale(Blackhole bh) {
        // Test immutable valueOfUnscaled(long unscaledValue, int scale)
        Factory15f.INSTANCE.valueOfUnscaled(12345L, 10);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        // Test mutableType() and newMutable()
        Factory15f.INSTANCE.newMutable();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNewArray(Blackhole bh) {
        // Test newArray(int length)
        Factory15f.INSTANCE.newArray(10);
        bh.consume(null);
    }
}
