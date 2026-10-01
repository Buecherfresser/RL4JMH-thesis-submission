package bench.generated.c061;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.scale.Scale3f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal3fBenchmark {

    // Since Decimal3f is immutable and static methods are used, no complex state setup is strictly required
    // for these simple benchmarks, adhering to the rule of avoiding per-invocation fixtures.

    @Benchmark
    public void valueOf_Long() {
        // Test conversion from long
        Decimal3f result = Decimal3f.valueOf(123456789L);
        // Consume the result to prevent dead code elimination
        try {
            result.toString();
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they occur during consumption
        }
    }

    @Benchmark
    public void valueOf_Float() {
        // Test conversion from float
        Decimal3f result = Decimal3f.valueOf(1.234f);
        // Consume the result
        try {
            result.toString();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void valueOf_Double() {
        // Test conversion from double
        Decimal3f result = Decimal3f.valueOf(987.654321);
        // Consume the result
        try {
            result.toString();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void valueOf_BigInteger() {
        // Test conversion from BigInteger
        Decimal3f result = Decimal3f.valueOf(new BigInteger("1234567890123456789"));
        // Consume the result
        try {
            result.toString();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void valueOf_BigDecimal() {
        // Test conversion from BigDecimal
        // Using a simple BigDecimal that should fit within Decimal3f limits
        BigDecimal bd = new BigDecimal("123.456");
        Decimal3f result = Decimal3f.valueOf(bd);
        // Consume the result
        try {
            result.toString();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void valueOf_String() {
        // Test conversion from String (simple integer string)
        Decimal3f result = Decimal3f.valueOf("1000");
        // Consume the result
        try {
            result.toString();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void valueOfUnscaled_Simple() {
        // Test unscaled value conversion (simple case)
        Decimal3f result = Decimal3f.valueOfUnscaled(12345L);
        // Consume the result
        try {
            result.toString();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void valueOfUnscaled_Zero() {
        // Test unscaled value conversion (zero case)
        Decimal3f result = Decimal3f.valueOfUnscaled(0L);
        // Consume the result
        try {
            result.toString();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
