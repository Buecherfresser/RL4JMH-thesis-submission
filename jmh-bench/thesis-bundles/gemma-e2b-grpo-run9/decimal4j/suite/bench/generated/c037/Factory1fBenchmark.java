package bench.generated.c037;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory1f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory1fBenchmark {

    @Setup
    public void setup() {
        // Factory1f is a singleton enum, no complex setup required.
    }

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Test immutable valueOf(long value)
        Factory1f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Test immutable valueOf(double value)
        Factory1f.INSTANCE.valueOf(3.14159);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        // Test immutable valueOf(BigDecimal value)
        Factory1f.INSTANCE.valueOf(new BigDecimal("123.45"));
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfFloat(Blackhole bh) {
        // Test immutable valueOf(float value)
        Factory1f.INSTANCE.valueOf((float) 1.23f);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDoubleWithRounding(Blackhole bh) {
        // Test immutable valueOf(double value, RoundingMode roundingMode)
        Factory1f.INSTANCE.valueOf(1.23456789, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Test immutable parse(String value)
        Factory1f.INSTANCE.parse("12345");
        bh.consume(null);
    }

    @Benchmark
    public void testParseStringWithRounding(Blackhole bh) {
        // Test immutable parse(String value, RoundingMode roundingMode)
        Factory1f.INSTANCE.parse("12345", RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaled(Blackhole bh) {
        // Test immutable valueOfUnscaled(long unscaledValue)
        Factory1f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledWithScale(Blackhole bh) {
        // Test immutable valueOfUnscaled(long unscaledValue, int scale)
        Factory1f.INSTANCE.valueOfUnscaled(12345L, 5);
        bh.consume(null);
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        // Test mutable newMutable()
        Factory1f.INSTANCE.newMutable();
        bh.consume(null);
    }
}
