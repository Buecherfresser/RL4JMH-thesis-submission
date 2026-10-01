package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory9f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory9fBenchmark {

    // Since Factory9f is an enum singleton, we don't need a @State field for it,
    // but we can use it directly.

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Call a method that returns a value (immutable type)
        Factory9f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Call a method that returns a value (immutable type)
        Factory9f.INSTANCE.valueOf(3.14159);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfBigInteger(Blackhole bh) {
        // Call a method that returns a value (immutable type)
        Factory9f.INSTANCE.valueOf(new BigInteger("1234567890123456789"));
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        // Call a method that returns a value (immutable type)
        Factory9f.INSTANCE.valueOf(new BigDecimal("1.234567890123456789"));
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfFloat(Blackhole bh) {
        // Call a method that returns a value (immutable type)
        Factory9f.INSTANCE.valueOf((float) 1.0f);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDoubleWithRounding(Blackhole bh) {
        // Call a method that returns a value (immutable type) with rounding
        Factory9f.INSTANCE.valueOf(1.0, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Call a method that parses a string
        Factory9f.INSTANCE.parse("123456789");
        bh.consume(null);
    }

    @Benchmark
    public void testParseStringWithRounding(Blackhole bh) {
        // Call a method that parses a string with rounding
        Factory9f.INSTANCE.parse("1.234567890123456789", RoundingMode.HALF_EVEN);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledLong(Blackhole bh) {
        // Call a method that returns a value from unscaled long
        Factory9f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledLongWithScale(Blackhole bh) {
        // Call a method that returns a value from unscaled long with scale
        Factory9f.INSTANCE.valueOfUnscaled(12345L, 9);
        bh.consume(null);
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        // Call a method that returns a mutable instance
        Factory9f.INSTANCE.newMutable();
        bh.consume(null);
    }
}
