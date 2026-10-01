package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory8f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory8fBenchmark {

    // Since Factory8f is an enum singleton, we don't need a @State field for it.

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Call a method that returns a value (immutable type)
        Factory8f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Call a method that returns a value (immutable type)
        Factory8f.INSTANCE.valueOf(3.14159);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        // Call a method that returns a value (immutable type)
        Factory8f.INSTANCE.valueOf(new BigDecimal("123.45"));
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfLongWithRounding(Blackhole bh) {
        // Call a method that returns a value with rounding
        Factory8f.INSTANCE.valueOf(100L, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDoubleWithRounding(Blackhole bh) {
        // Call a method that returns a value with rounding
        Factory8f.INSTANCE.valueOf(1.23456789, RoundingMode.HALF_EVEN);
        bh.consume(null);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Call a method that parses a string
        Factory8f.INSTANCE.parse("12345");
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaled(Blackhole bh) {
        // Call a method that returns a value from unscaled long
        Factory8f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledWithScale(Blackhole bh) {
        // Call a method that returns a value from unscaled long with scale
        Factory8f.INSTANCE.valueOfUnscaled(12345L, 8);
        bh.consume(null);
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        // Call a method that returns a mutable instance
        Factory8f.INSTANCE.newMutable();
        bh.consume(null);
    }

    @Benchmark
    public void testNewArray(Blackhole bh) {
        // Call a method that returns an array
        Factory8f.INSTANCE.newArray(5);
        bh.consume(null);
    }
}
