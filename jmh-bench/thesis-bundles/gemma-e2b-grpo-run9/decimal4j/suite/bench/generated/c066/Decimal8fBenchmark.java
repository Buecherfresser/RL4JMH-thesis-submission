package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal8f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Decimal8fBenchmark {

    // Since we are benchmarking static methods, no instance state is strictly required.

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Test conversion from long
        Decimal8f result = Decimal8f.valueOf(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Test conversion from double
        Decimal8f result = Decimal8f.valueOf(3.1415926535);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Test conversion from BigDecimal
        // Using a simple BigDecimal for speed, though complex ones might stress the parser more.
        BigDecimal bd = new BigDecimal("123.45678901");
        Decimal8f result = Decimal8f.valueOf(bd);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        // Test conversion from BigInteger
        BigInteger bi = new BigInteger("9876543210");
        Decimal8f result = Decimal8f.valueOf(bi);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        // Test conversion from unscaled long
        Decimal8f result = Decimal8f.valueOfUnscaled(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledWithScale(Blackhole bh) {
        // Test conversion from unscaled long with scale
        Decimal8f result = Decimal8f.valueOfUnscaled(123456789L, 5);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        // Test conversion from float
        Decimal8f result = Decimal8f.valueOf(1.2345f);
        bh.consume(result);
    }
}
