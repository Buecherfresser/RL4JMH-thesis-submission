package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory17f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory17fBenchmark {

    // Since Factory17f is an enum with a static INSTANCE, we rely on calling methods directly.

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Test a basic valueOf method
        Factory17f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Test a valueOf method with double
        Factory17f.INSTANCE.valueOf(3.14159);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        // Test a valueOf method with BigDecimal
        Factory17f.INSTANCE.valueOf(new BigDecimal("123.45"));
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfFloat(Blackhole bh) {
        // Test a valueOf method with float
        Factory17f.INSTANCE.valueOf((float) 1.0f);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDoubleWithRounding(Blackhole bh) {
        // Test a valueOf method with double and RoundingMode
        Factory17f.INSTANCE.valueOf(1.23456789, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Test the parse method
        Factory17f.INSTANCE.parse("123.45");
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledLong(Blackhole bh) {
        // Test valueOfUnscaled(long)
        Factory17f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledWithScale(Blackhole bh) {
        // Test valueOfUnscaled(long, int)
        Factory17f.INSTANCE.valueOfUnscaled(12345L, 5);
        bh.consume(null);
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        // Test the mutable factory method
        Factory17f.INSTANCE.newMutable();
        bh.consume(null);
    }

    @Benchmark
    public void testNewArray(Blackhole bh) {
        // Test the immutable array factory method
        Factory17f.INSTANCE.newArray(10);
        bh.consume(null);
    }
}
