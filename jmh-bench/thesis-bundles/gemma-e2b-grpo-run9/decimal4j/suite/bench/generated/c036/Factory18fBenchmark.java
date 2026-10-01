package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory18f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory18fBenchmark {

    // Since Factory18f is an enum with a singleton INSTANCE, we don't need a @State field.

    // --- Benchmarks for valueOf methods (Immutable creation) ---

    @Benchmark
    public void valueOf_long(Blackhole bh) {
        // Test immutable creation from long
        Factory18f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void valueOf_double(Blackhole bh) {
        // Test immutable creation from double
        Factory18f.INSTANCE.valueOf(3.14159);
        bh.consume(null);
    }

    @Benchmark
    public void valueOf_bigInteger(Blackhole bh) {
        // Test immutable creation from BigInteger
        Factory18f.INSTANCE.valueOf(java.math.BigInteger.valueOf(12345678987654321L));
        bh.consume(null);
    }

    @Benchmark
    public void valueOf_bigDecimal(Blackhole bh) {
        // Test immutable creation from BigDecimal
        Factory18f.INSTANCE.valueOf(new BigDecimal("123.45"));
        bh.consume(null);
    }

    @Benchmark
    public void valueOf_string(Blackhole bh) {
        // Test immutable creation from String via parse
        Factory18f.INSTANCE.parse("123.45");
        bh.consume(null);
    }

    @Benchmark
    public void valueOf_double_with_rounding(Blackhole bh) {
        // Test immutable creation with rounding mode
        Factory18f.INSTANCE.valueOf(1.23456789, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    // --- Benchmarks for Unscaled valueOf methods ---

    @Benchmark
    public void valueOfUnscaled_long(Blackhole bh) {
        // Test immutable creation from unscaled long
        Factory18f.INSTANCE.valueOfUnscaled(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void valueOfUnscaled_long_with_scale(Blackhole bh) {
        // Test immutable creation from unscaled long with scale
        Factory18f.INSTANCE.valueOfUnscaled(123456789L, 10);
        bh.consume(null);
    }

    // --- Benchmarks for Mutable creation ---

    @Benchmark
    public void newMutable(Blackhole bh) {
        // Test mutable creation
        Factory18f.INSTANCE.newMutable();
        bh.consume(null);
    }
}
