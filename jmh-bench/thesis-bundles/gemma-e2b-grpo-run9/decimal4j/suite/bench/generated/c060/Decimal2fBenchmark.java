package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale2f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal2fBenchmark {

    // Since we are primarily testing static conversion methods,
    // we don't strictly need instance state, but we keep the class structure clean.

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Test conversion of a standard long value
        Decimal2f result = Decimal2f.valueOf(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfZero(Blackhole bh) {
        // Test conversion of zero
        Decimal2f result = Decimal2f.valueOf(0L);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfOne(Blackhole bh) {
        // Test conversion of one (a constant)
        Decimal2f result = Decimal2f.valueOf(1L);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfNegative(Blackhole bh) {
        // Test conversion of a negative long value
        Decimal2f result = Decimal2f.valueOf(-987654321L);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfFloat(Blackhole bh) {
        // Test conversion from float
        Decimal2f result = Decimal2f.valueOf(1.23f);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Test conversion from double
        Decimal2f result = Decimal2f.valueOf(3.141592653589793);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfBigInteger(Blackhole bh) {
        // Test conversion from BigInteger
        BigInteger bigInt = new BigInteger("1234567890123456789");
        Decimal2f result = Decimal2f.valueOf(bigInt);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        // Test conversion from BigDecimal
        BigDecimal bd = new BigDecimal("123.45");
        Decimal2f result = Decimal2f.valueOf(bd);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfString(Blackhole bh) {
        // Test conversion from String
        String s = "987654321.12";
        Decimal2f result = Decimal2f.valueOf(s);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfUnscaled(Blackhole bh) {
        // Test conversion using the unscaled method
        Decimal2f result = Decimal2f.valueOfUnscaled(12345L, 2);
        bh.consume(result);
    }
}
