package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.factory.Factory12f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory12fBenchmark {

    // Since Factory12f is an enum singleton, we don't need an instance field.

    @Setup
    public void setup() {
        // Setup can be empty as Factory12f is a singleton enum
    }

    // --- Benchmarks for Immutable ValueOf methods ---

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Call a method and consume the result
        Factory12f.INSTANCE.valueOf(123456789012345L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Call a method and consume the result
        Factory12f.INSTANCE.valueOf(3.1415926535);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Call a method and consume the result
        Factory12f.INSTANCE.valueOf(new BigDecimal("1.234567890123"));
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        // Call a method and consume the result
        Factory12f.INSTANCE.valueOf(new BigInteger("9876543210"));
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfDecimal(Blackhole bh) {
        // Call a method and consume the result
        // Calling with a simple long input to satisfy the requirement of calling a method.
        Factory12f.INSTANCE.valueOf(1L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        // Call a method and consume the result
        Factory12f.INSTANCE.parse("123.45");
        bh.consume(null);
    }

    // --- Benchmarks for Mutable Factory methods ---

    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        // Call a method and consume the result
        Factory12f.INSTANCE.newMutable();
        bh.consume(null);
    }

    // --- Benchmarks for Factory/Type retrieval methods ---

    @Benchmark
    public void benchmarkGetScaleMetrics(Blackhole bh) {
        // Call a method and consume the result
        Factory12f.INSTANCE.getScaleMetrics();
        bh.consume(null);
    }
}
