package bench.generated.c034;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory16f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory16fBenchmark {

    // Since Factory16f is an enum singleton, we don't need a @State field for it.

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Test immutable valueOf(long)
        Factory16f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Test immutable valueOf(double)
        Factory16f.INSTANCE.valueOf(3.1415926535);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        // Test immutable valueOf(BigDecimal)
        Factory16f.INSTANCE.valueOf(BigDecimal.TEN);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfFloat(Blackhole bh) {
        // Test immutable valueOf(float)
        Factory16f.INSTANCE.valueOf(1.2345f);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDoubleWithRounding(Blackhole bh) {
        // Test immutable valueOf(double, RoundingMode)
        Factory16f.INSTANCE.valueOf(1.0 / 3.0, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Test parse(String)
        Factory16f.INSTANCE.parse("12345678901234567890");
        bh.consume(null);
    }

    @Benchmark
    public void testParseStringWithRounding(Blackhole bh) {
        // Test parse(String, RoundingMode)
        Factory16f.INSTANCE.parse("1.234567890123456789", RoundingMode.HALF_EVEN);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaled(Blackhole bh) {
        // Test valueOfUnscaled(long)
        Factory16f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledWithScale(Blackhole bh) {
        // Test valueOfUnscaled(long, int scale)
        Factory16f.INSTANCE.valueOfUnscaled(12345L, 10);
        bh.consume(null);
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        // Test newMutable()
        Factory16f.INSTANCE.newMutable();
        bh.consume(null);
    }

    @Benchmark
    public void testNewArray(Blackhole bh) {
        // Test newArray(int length)
        Factory16f.INSTANCE.newArray(10);
        bh.consume(null);
    }
}
