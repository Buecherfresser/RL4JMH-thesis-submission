package bench.generated.c027;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory0f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory0fBenchmark {

    // Since Factory0f is an enum singleton, we rely on Factory0f.INSTANCE

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Test immutable valueOf(long)
        Factory0f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Test immutable valueOf(double)
        Factory0f.INSTANCE.valueOf(3.14159);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfBigInteger(Blackhole bh) {
        // Test immutable valueOf(BigInteger)
        Factory0f.INSTANCE.valueOf(new BigInteger("1234567890123456789"));
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        // Test immutable valueOf(BigDecimal)
        Factory0f.INSTANCE.valueOf(new BigDecimal("123.45"));
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfLongWithRounding(Blackhole bh) {
        // Test immutable valueOf(long) with RoundingMode
        Factory0f.INSTANCE.valueOf(100L, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Test immutable parse(String)
        Factory0f.INSTANCE.parse("12345");
        bh.consume(null);
    }

    @Benchmark
    public void testParseStringWithRounding(Blackhole bh) {
        // Test immutable parse(String, RoundingMode)
        Factory0f.INSTANCE.parse("123.45", RoundingMode.HALF_EVEN);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaled(Blackhole bh) {
        // Test immutable valueOfUnscaled(long)
        Factory0f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledWithScale(Blackhole bh) {
        // Test immutable valueOfUnscaled(long, int scale)
        Factory0f.INSTANCE.valueOfUnscaled(12345L, 5);
        bh.consume(null);
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        // Test mutable newMutable()
        Factory0f.INSTANCE.newMutable();
        bh.consume(null);
    }

    @Benchmark
    public void testNewArray(Blackhole bh) {
        // Test immutable newArray(int length)
        Factory0f.INSTANCE.newArray(10);
        bh.consume(null);
    }
}
