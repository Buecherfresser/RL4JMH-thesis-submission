package bench.generated.c054;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale14f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Decimal14fBenchmark {

    // State fields are not strictly necessary for static method calls,
    // but we keep the class structure clean.

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Test conversion from long
        Decimal14f result = Decimal14f.valueOf(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Test conversion from double
        Decimal14f result = Decimal14f.valueOf(3.1415926535);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        // Test conversion from BigDecimal
        // Using a simple, non-static final BigDecimal instance for input
        BigDecimal bd = new BigDecimal("123.4567890123456789");
        Decimal14f result = Decimal14f.valueOf(bd);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfString(Blackhole bh) {
        // Test conversion from String
        // Using a simple, non-static final String instance for input
        String s = "9876543210.123456789012";
        Decimal14f result = Decimal14f.valueOf(s);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfUnscaledLong(Blackhole bh) {
        // Test conversion from unscaled long
        Decimal14f result = Decimal14f.valueOfUnscaled(1234567890123L);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiplyExact(Blackhole bh) {
        // Test multiplication (uses instance method, requires an instance)
        Decimal14f d1 = Decimal14f.valueOf(1.0);
        try {
            d1.multiplyExact();
        } catch (Exception e) {
            // Ignore exceptions for benchmarking if they are expected in certain modes,
            // but here we expect success.
        }
    }

    @Benchmark
    public void testToMutableDecimal(Blackhole bh) {
        // Test conversion to mutable (uses instance method, requires an instance)
        Decimal14f d1 = Decimal14f.valueOf(1.0);
        try {
            d1.toMutableDecimal();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
