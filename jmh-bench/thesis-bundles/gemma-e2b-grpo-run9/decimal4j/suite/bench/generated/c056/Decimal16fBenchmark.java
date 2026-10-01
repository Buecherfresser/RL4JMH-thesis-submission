package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale16f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Decimal16fBenchmark {

    // Since Decimal16f is immutable and static methods are used,
    // no instance state is strictly required for these benchmarks.

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Test conversion from long
        Decimal16f result = Decimal16f.valueOf(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Test conversion from double (uses default checked arithmetic)
        Decimal16f result = Decimal16f.valueOf(123.456789);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Test conversion from BigDecimal (uses default checked arithmetic)
        BigDecimal bd = new BigDecimal("987654321.1234567890123456");
        Decimal16f result = Decimal16f.valueOf(bd);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        // Test conversion from String
        Decimal16f result = Decimal16f.valueOf("12345678901234567890123456");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaled(Blackhole bh) {
        // Test internal unscaled conversion path
        Decimal16f result = Decimal16f.valueOfUnscaled(1234567890123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        // Test method that returns a Multipliable object
        Decimal16f d1 = Decimal16f.ONE;
        try {
            d1.multiplyExact();
        } catch (Exception e) {
            // Expected to fail if multiplication overflows, but we just measure the call path
        }
        bh.consume(null); // Consume null as the method returns a Multipliable16f
    }
}
