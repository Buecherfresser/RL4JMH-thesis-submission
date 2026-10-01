package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Decimal13fBenchmark {

    // Since we are benchmarking static methods, no instance state is strictly required.
    // We rely on the JVM/JMH to handle the execution environment.

    @Benchmark
    public void valueOf_Long(Blackhole bh) {
        // Test conversion from a long
        Decimal13f result = Decimal13f.valueOf(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_Double(Blackhole bh) {
        // Test conversion from a double
        Decimal13f result = Decimal13f.valueOf(3.1415926535);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_String(Blackhole bh) {
        // Test conversion from a String
        try {
            Decimal13f result = Decimal13f.valueOf("123.4567890123456789");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking if they are expected under certain conditions
        }
    }

    @Benchmark
    public void valueOf_BigInteger(Blackhole bh) {
        // Test conversion from a BigInteger
        Decimal13f result = Decimal13f.valueOf(new BigInteger("98765432101234567890123456789"));
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaled_Long(Blackhole bh) {
        // Test unscaled conversion
        Decimal13f result = Decimal13f.valueOfUnscaled(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyExact(Blackhole bh) {
        // Test an arithmetic operation that returns a Multipliable object
        try {
            Decimal13f d1 = Decimal13f.valueOf(1.0);
            d1.multiplyExact();
            bh.consume(null); // Consume the result of the operation
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}
