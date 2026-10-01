package bench.generated.c038;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory2f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory2fBenchmark {

    // Since Factory2f is an enum with a singleton INSTANCE, no @State field is required.

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Call the singleton instance method
        Factory2f.INSTANCE.valueOf(123L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        Factory2f.INSTANCE.valueOf(3.14159);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Using a constant BigDecimal for input, which is safe as it's immutable
        Factory2f.INSTANCE.valueOf(BigDecimal.TEN);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        // Using a constant BigInteger for input
        Factory2f.INSTANCE.valueOf(BigInteger.ONE);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        Factory2f.INSTANCE.valueOf(1.0f);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        // Benchmarking a method that takes a RoundingMode
        Factory2f.INSTANCE.valueOf(1.0, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        // Benchmarking parsing a string
        Factory2f.INSTANCE.parse("12345");
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        // Benchmarking valueOfUnscaled(long)
        Factory2f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongWithScale(Blackhole bh) {
        // Benchmarking valueOfUnscaled(long, int)
        Factory2f.INSTANCE.valueOfUnscaled(12345L, 2);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        // Benchmarking method that returns a mutable object
        Factory2f.INSTANCE.newMutable();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNewArray(Blackhole bh) {
        // Benchmarking method that returns an array
        Factory2f.INSTANCE.newArray(10);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkDeriveFactory(Blackhole bh) {
        try {
            // Attempting to derive a factory
            Factory2f.INSTANCE.deriveFactory(0);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }
}
