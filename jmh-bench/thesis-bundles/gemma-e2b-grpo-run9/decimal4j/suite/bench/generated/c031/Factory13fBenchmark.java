package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory13f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory13fBenchmark {

    // Since Factory13f is an enum singleton, we don't need a @State field for it.

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Test immutable valueOf(long)
        Factory13f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Test immutable valueOf(double)
        Factory13f.INSTANCE.valueOf(3.1415926535);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        // Test immutable valueOf(BigDecimal)
        Factory13f.INSTANCE.valueOf(new BigDecimal("123.45"));
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfFloat(Blackhole bh) {
        // Test immutable valueOf(float)
        Factory13f.INSTANCE.valueOf(1.2345f);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDoubleWithRounding(Blackhole bh) {
        // Test immutable valueOf(double, RoundingMode)
        Factory13f.INSTANCE.valueOf(1.0 / 3.0, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Test immutable parse(String)
        Factory13f.INSTANCE.parse("1234567890123");
        bh.consume(null);
    }

    @Benchmark
    public void testParseStringWithRounding(Blackhole bh) {
        // Test immutable parse(String, RoundingMode)
        Factory13f.INSTANCE.parse("1.0", RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledLong(Blackhole bh) {
        // Test immutable valueOfUnscaled(long)
        Factory13f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledLongWithScale(Blackhole bh) {
        // Test immutable valueOfUnscaled(long, int scale)
        Factory13f.INSTANCE.valueOfUnscaled(12345L, 5);
        bh.consume(null);
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        // Test mutableType() indirectly via newMutable()
        Factory13f.INSTANCE.newMutable();
        bh.consume(null);
    }
}
