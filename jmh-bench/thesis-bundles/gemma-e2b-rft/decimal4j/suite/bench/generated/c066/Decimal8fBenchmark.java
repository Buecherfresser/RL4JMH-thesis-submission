package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable8f;
import org.decimal4j.factory.Factory8f;
import org.decimal4j.immutable.Decimal8f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Decimal8fBenchmark {

    // --- State Fields ---
    private List<Decimal8f> decimal8fPool;
    private long testLongValue;
    private double testDoubleValue;
    private BigDecimal testBigDecimalValue;
    private BigInteger testBigIntegerValue;
    private String testStringValue;

    // --- Setup ---
    @Setup
    public void setup() {
        // 1. Initialize a pool of Decimal8f instances for repeated use
        decimal8fPool = new ArrayList<>();
        
        // Create a few representative values
        decimal8fPool.add(Decimal8f.ONE);
        decimal8fPool.add(Decimal8f.TEN);
        decimal8fPool.add(Decimal8f.HALF);
        decimal8fPool.add(Decimal8f.HUNDREDTH);
        decimal8fPool.add(Decimal8f.MILLIONTH);
        
        // 2. Prepare inputs for conversion benchmarks
        testLongValue = 123456789L;
        testDoubleValue = 123456789.12345678;
        testBigDecimalValue = new BigDecimal("123456789.12345678");
        testBigIntegerValue = new BigInteger("92233720368");
        testStringValue = "123456789.12345678";
    }

    // --- Benchmarks for Static ValueOf Conversions ---

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        Decimal8f result = Decimal8f.valueOf(testLongValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        Decimal8f result = Decimal8f.valueOf(testDoubleValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal8f result = Decimal8f.valueOf(testBigDecimalValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        Decimal8f result = Decimal8f.valueOf(testBigIntegerValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        Decimal8f result = Decimal8f.valueOf(testStringValue);
        bh.consume(result);
    }

    // --- Benchmarks for Unscaled Conversions ---

    @Benchmark
    public void benchmarkValueOfUnscaled(Blackhole bh) {
        Decimal8f result = Decimal8f.valueOfUnscaled(testLongValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledWithScale(Blackhole bh) {
        int scale = 3;
        Decimal8f result = Decimal8f.valueOfUnscaled(testLongValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledWithRounding(Blackhole bh) {
        int scale = 2;
        RoundingMode mode = RoundingMode.HALF_UP;
        Decimal8f result = Decimal8f.valueOfUnscaled(testLongValue, scale, mode);
        bh.consume(result);
    }

    // --- Benchmarks for Arithmetic Operations (Assuming standard Decimal interface methods exist) ---
    
    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        Decimal8f a = decimal8fPool.get(0);
        Decimal8f b = decimal8fPool.get(1);
        Decimal8f result = a.add(b);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiply(Blackhole bh) {
        Decimal8f a = decimal8fPool.get(0);
        Decimal8f b = decimal8fPool.get(2);
        Decimal8f result = a.multiply(b);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkNegate(Blackhole bh) {
        Decimal8f a = decimal8fPool.get(3);
        Decimal8f result = a.negate();
        bh.consume(result);
    }
    
    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        Decimal8f a = decimal8fPool.get(0);
        // Assuming multiplyExact() exists and returns Multipliable8f
        Multipliable8f multiplier = a.multiplyExact(); 
        bh.consume(multiplier);
    }
}
