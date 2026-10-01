package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factory17f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal17fBenchmark {

    // Since Decimal17f is immutable and we are primarily testing static methods
    // or methods that don't mutate state, no complex @State fields are strictly required.

    /**
     * Benchmark for converting a long value to Decimal17f using the static valueOf(long) method.
     */
    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Test a typical long value
        Decimal17f result = Decimal17f.valueOf(123456789012345L);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a double value to Decimal17f using the static valueOf(double) method.
     */
    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Test a typical double value
        Decimal17f result = Decimal17f.valueOf(3.1415926535);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a BigDecimal value to Decimal17f using the static valueOf(BigDecimal) method.
     */
    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Test a complex BigDecimal conversion
        BigDecimal bd = new BigDecimal("123.4567890123456789");
        Decimal17f result = Decimal17f.valueOf(bd);
        bh.consume(result);
    }

    /**
     * Benchmark for converting a String value to Decimal17f using the static valueOf(String) method.
     */
    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        // Test a string conversion
        Decimal17f result = Decimal17f.valueOf("9876543210.123");
        bh.consume(result);
    }

    /**
     * Benchmark for performing an exact multiplication operation.
     * This tests the interaction with the Multipliable17f return type.
     */
    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        // Create a temporary Decimal17f instance to call the method on
        Decimal17f d1 = Decimal17f.valueOf(10L);
        
        // Call the method which returns a Multipliable17f
        org.decimal4j.exact.Multipliable17f multiplier = d1.multiplyExact();
        
        // Consume the result to prevent dead code elimination
        bh.consume(multiplier);
    }
}
