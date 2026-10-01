package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale0f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal0fBenchmark {

    // Since all methods are static, we don't need instance state,
    // but we need dynamic inputs for the benchmarks.

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        try {
            // Test conversion from long
            Decimal0f result = Decimal0f.valueOf(123456789L);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for timing purposes if they occur on edge cases
        }
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        try {
            // Test conversion from double
            Decimal0f result = Decimal0f.valueOf(3.14159);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        try {
            // Test conversion from BigDecimal
            // Using a simple BigDecimal that should convert easily
            Decimal0f result = Decimal0f.valueOf(new BigDecimal("123.45"));
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        try {
            // Test conversion from BigInteger
            Decimal0f result = Decimal0f.valueOf(new BigInteger("9876543210"));
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        try {
            // Test conversion from String
            Decimal0f result = Decimal0f.valueOf("1000000");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkValueOfUnscaled(Blackhole bh) {
        try {
            // Test internal unscaled conversion
            Decimal0f result = Decimal0f.valueOfUnscaled(123456789L);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkValueOfUnscaledWithScale(Blackhole bh) {
        try {
            // Test internal unscaled conversion with scale
            Decimal0f result = Decimal0f.valueOfUnscaled(123456789L, 2);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
