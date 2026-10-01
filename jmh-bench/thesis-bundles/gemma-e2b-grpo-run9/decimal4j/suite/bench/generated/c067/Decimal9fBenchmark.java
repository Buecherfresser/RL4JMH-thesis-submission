package bench.generated.c067;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale9f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Decimal9fBenchmark {

    // Since Decimal9f is immutable and we are testing static methods,
    // no instance state is strictly required.

    @Benchmark
    public void valueOfLong(Blackhole bh) {
        // Test conversion from long
        Decimal9f result = Decimal9f.valueOf(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDouble(Blackhole bh) {
        // Test conversion from double
        Decimal9f result = Decimal9f.valueOf(3.1415926535);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigDecimal(Blackhole bh) {
        // Test conversion from BigDecimal
        // Using a simple BigDecimal for a quick test
        Decimal9f result = Decimal9f.valueOf(new BigDecimal("123.456789012"));
        bh.consume(result);
    }

    @Benchmark
    public void valueOfString(Blackhole bh) {
        // Test conversion from String (requires parsing)
        try {
            Decimal9f result = Decimal9f.valueOf("123456789.123");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected for certain inputs
        }
    }

    @Benchmark
    public void valueOfUnscaledLong(Blackhole bh) {
        // Test internal unscaled conversion logic
        Decimal9f result = Decimal9f.valueOfUnscaled(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaledZero(Blackhole bh) {
        // Test zero case
        Decimal9f result = Decimal9f.valueOfUnscaled(0L);
        bh.consume(result);
    }
    
    @Benchmark
    public void valueOfUnscaledOne(Blackhole bh) {
        // Test one case
        Decimal9f result = Decimal9f.valueOfUnscaled(1L);
        bh.consume(result);
    }
}
