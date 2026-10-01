package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory11f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory11fBenchmark {

    // Since Factory11f is an enum with a static INSTANCE, we rely on static calls.

    @Setup
    public void setup() {
        // No mutable state needed for this read-only benchmark setup.
    }

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Test immutable valueOf with a long
        Factory11f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Test immutable valueOf with a double
        Factory11f.INSTANCE.valueOf(3.1415926535);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Test immutable valueOf with BigDecimal
        Factory11f.INSTANCE.valueOf(new BigDecimal("123.456"));
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        // Test immutable valueOf with a float
        Factory11f.INSTANCE.valueOf(1.0f);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        // Test immutable valueOf with double and RoundingMode
        Factory11f.INSTANCE.valueOf(1.23456, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        // Test immutable valueOf with BigInteger
        Factory11f.INSTANCE.valueOf(new BigInteger("9876543210"));
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        // Test parse method
        Factory11f.INSTANCE.parse("123.456");
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        // Test valueOfUnscaled(long)
        Factory11f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongWithScale(Blackhole bh) {
        // Test valueOfUnscaled(long, int scale)
        Factory11f.INSTANCE.valueOfUnscaled(12345L, 5);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        // Test mutable type creation
        Factory11f.INSTANCE.newMutable();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNewArray(Blackhole bh) {
        // Test array creation
        Factory11f.INSTANCE.newArray(10);
        bh.consume(null);
    }
}
