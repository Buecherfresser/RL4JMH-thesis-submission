package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory3f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory3fBenchmark {

    // Since Factory3f is an enum singleton, we don't need a @State field for it,
    // but we can use it directly.

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Call a method that returns a new immutable object
        Factory3f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Call a method that returns a new immutable object
        Factory3f.INSTANCE.valueOf(3.14159);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        // Call a method that returns a new immutable object
        Factory3f.INSTANCE.valueOf(new BigDecimal("1.2345"));
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDoubleWithRounding(Blackhole bh) {
        // Call a method that returns a new immutable object with rounding
        Factory3f.INSTANCE.valueOf(1.23456789, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Call a method that parses a string
        Factory3f.INSTANCE.parse("12345");
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaled(Blackhole bh) {
        // Call a method that returns a new immutable object from unscaled value
        Factory3f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledWithScale(Blackhole bh) {
        // Call a method that returns a new immutable object with scale
        Factory3f.INSTANCE.valueOfUnscaled(12345L, 3);
        bh.consume(null);
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        // Call a method that returns a new mutable object
        Factory3f.INSTANCE.newMutable();
        bh.consume(null);
    }

    @Benchmark
    public void testNewArray(Blackhole bh) {
        // Call a method that returns an array (we consume the result to prevent dead code elimination)
        Factory3f.INSTANCE.newArray(5);
        bh.consume(null);
    }
}
