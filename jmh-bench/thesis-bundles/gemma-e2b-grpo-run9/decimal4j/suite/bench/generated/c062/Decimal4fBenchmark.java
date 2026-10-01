package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.decimal4j.immutable.Decimal4f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal4fBenchmark {

    // Since we are benchmarking static methods, no instance state is strictly required,
    // but we keep the class structure clean.

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Test conversion from a standard long
        Decimal4f result = Decimal4f.valueOf(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Test conversion from a double
        Decimal4f result = Decimal4f.valueOf(3.1415926535);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        // Test conversion from a large BigInteger
        BigInteger bigInt = new BigInteger("98765432101234567890");
        Decimal4f result = Decimal4f.valueOf(bigInt);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        // Test conversion from a string (requires parsing)
        try {
            Decimal4f result = Decimal4f.valueOf("123.4567");
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected in some paths
        }
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        // Test internal unscaled conversion path
        Decimal4f result = Decimal4f.valueOfUnscaled(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongZero(Blackhole bh) {
        // Test zero case
        Decimal4f result = Decimal4f.valueOfUnscaled(0L);
        bh.consume(result);
    }
}
