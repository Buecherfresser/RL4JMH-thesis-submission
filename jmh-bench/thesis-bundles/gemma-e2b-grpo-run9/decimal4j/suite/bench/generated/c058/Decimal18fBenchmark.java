package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import org.decimal4j.immutable.Decimal18f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal18fBenchmark {

    // Since we are benchmarking static methods, no instance state is strictly required.

    @Setup
    public void setup() {
        // Setup logic, if any, would go here.
    }

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Test conversion from long
        Decimal18f result = Decimal18f.valueOf(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Test conversion from double
        Decimal18f result = Decimal18f.valueOf(3.1415926535);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Test conversion from BigDecimal
        // Using a simple BigDecimal for conversion test
        BigDecimal bd = new BigDecimal("12345678901234567890.123");
        Decimal18f result = Decimal18f.valueOf(bd);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        // Test conversion from String
        // Using a simple string representation
        Decimal18f result = Decimal18f.valueOf("12345678901234567890.12345");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        // Test the multiplication path (which returns a Multipliable18f)
        // We call the method and consume the result (which is a Multipliable18f)
        Decimal18f d1 = Decimal18f.valueOf(10L);
        try {
            d1.multiplyExact();
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they occur, 
            // though in a real scenario, we'd handle them.
        }
        bh.consume(null); // Consume null as the method returns a Multipliable18f, which we don't capture here.
    }
}
