package bench.generated.c063;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import org.decimal4j.immutable.Decimal5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal5fBenchmark {

    // State fields are not strictly necessary for static methods, 
    // but required by the structure if we were testing instance methods.
    // We keep the class clean as we only test static factory methods here.

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Test conversion from long
        Decimal5f result = Decimal5f.valueOf(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Test conversion from double
        Decimal5f result = Decimal5f.valueOf(123.456);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Test conversion from BigDecimal
        // Using a simple BigDecimal that should fit within Decimal5f limits
        BigDecimal bd = new BigDecimal("123.45678");
        Decimal5f result = Decimal5f.valueOf(bd);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        // Test conversion from String
        Decimal5f result = Decimal5f.valueOf("987.654");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        // Test conversion from BigInteger
        BigInteger bi = new BigInteger("1234567890123456789L");
        Decimal5f result = Decimal5f.valueOf(bi);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        // Test unscaled conversion
        Decimal5f result = Decimal5f.valueOfUnscaled(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongWithScale(Blackhole bh) {
        // Test unscaled conversion with scale
        Decimal5f result = Decimal5f.valueOfUnscaled(1000L, 2);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkConstantAccess(Blackhole bh) {
        // Test access to a static constant (should be extremely fast)
        Decimal5f result = Decimal5f.ONE;
        bh.consume(result);
    }
}
