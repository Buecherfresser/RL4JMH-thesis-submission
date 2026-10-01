package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable16f;
import org.decimal4j.mutable.MutableDecimal16f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal16fBenchmark {

    // --- State Fields for Inputs ---
    private Decimal16f d1;
    private Decimal16f d2;
    private double dDouble;
    private BigDecimal dBigDecimal;
    private String dString;
    private BigInteger dBigInteger;
    private long dLong;
    private int dScale;
    private RoundingMode dRoundingMode;

    // --- Setup ---
    @Setup
    public void setup() {
        // Setup base values
        d1 = Decimal16f.ONE;
        d2 = Decimal16f.TWO;
        dDouble = 3.1415926535;
        dBigDecimal = new BigDecimal("123.4567890123456789");
        dString = "123.4567890123456789";
        dBigInteger = new BigInteger("9876543210");
        dLong = 123456789L;
        dScale = 16;
        dRoundingMode = RoundingMode.HALF_UP;
    }

    // --- Benchmarks for Construction/Conversion ---

    @Benchmark
    public void constructFromLong(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(dLong);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromDouble(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(dDouble);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromBigDecimal(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(dBigDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromString(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(dString);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromBigInteger(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(dBigInteger);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromUnscaledLong(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOfUnscaled(dLong);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromUnscaledLongWithScale(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOfUnscaled(dLong, dScale);
        bh.consume(result);
    }

    @Benchmark
    public void constructFromUnscaledLongWithRounding(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOfUnscaled(dLong, dScale, dRoundingMode);
        bh.consume(result);
    }

    // --- Benchmarks for Arithmetic Operations ---

    @Benchmark
    public void arithmeticAdd(Blackhole bh) {
        Decimal16f result = d1.add(d2);
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticSubtract(Blackhole bh) {
        Decimal16f result = d1.subtract(d2);
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticMultiplyExact(Blackhole bh) {
        // Fixed: Replaced complex Multipliable16f chain with standard multiplication
        Decimal16f result = d1.multiply(d2);
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticDivide(Blackhole bh) {
        Decimal16f result = d1.divide(d2);
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticNegate(Blackhole bh) {
        Decimal16f result = d1.negate();
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticSquare(Blackhole bh) {
        Decimal16f result = d1.square();
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticSqrt(Blackhole bh) {
        Decimal16f result = d1.sqrt();
        bh.consume(result);
    }

    @Benchmark
    public void arithmeticPow(Blackhole bh) {
        // Using pow(int)
        Decimal16f result = d1.pow(2);
        bh.consume(result);
    }

    // --- Benchmarks for Utility/Conversion ---

    @Benchmark
    public void conversionToDouble(Blackhole bh) {
        double result = d1.doubleValue();
        bh.consume(result);
    }

    @Benchmark
    public void conversionToString(Blackhole bh) {
        String result = d1.toString();
        bh.consume(result);
    }

    @Benchmark
    public void conversionToBigDecimal(Blackhole bh) {
        BigDecimal result = d1.toBigDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void toMutableDecimal(Blackhole bh) {
        MutableDecimal16f mutableResult = d1.toMutableDecimal();
        bh.consume(mutableResult);
    }
}
