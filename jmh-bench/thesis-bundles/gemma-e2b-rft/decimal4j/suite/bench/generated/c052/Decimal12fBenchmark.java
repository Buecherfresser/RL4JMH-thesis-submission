package bench.generated.c052;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.exact.Multipliable12f;
import org.decimal4j.immutable.Decimal12f;
import org.decimal4j.scale.Scale12f;
import org.decimal4j.factory.Factory12f;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal12fBenchmark {

    // --- State Fields for Inputs ---

    private Decimal12f longInput;
    private Decimal12f bigIntegerInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private float floatInput;
    private double doubleInput;
    private BigInteger bigIntegerValue;

    // --- Setup ---

    @Setup
    public void setup() {
        // Setup Long input
        longInput = Decimal12f.valueOf(1234567890123L);

        // Setup BigInteger input
        bigIntegerValue = new BigInteger("9876543210987654321");
        bigIntegerInput = Decimal12f.valueOf(bigIntegerValue);

        // Setup BigDecimal input
        bigDecimalInput = new BigDecimal("12345.678901234567");
        
        // Setup String input
        stringInput = "1234567890123.4567";

        // Setup Float input
        floatInput = 123.4567f;

        // Setup Double input
        doubleInput = 12345.67890123456789;
    }

    // --- Benchmarks for ValueOf Overloads ---

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOf(1234567890123L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOf(bigIntegerValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOf(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOf(floatInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOf(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOf(doubleInput, RoundingMode.HALF_UP);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfFloatWithRounding(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOf(floatInput, RoundingMode.DOWN);
        bh.consume(result);
    }

    // --- Benchmarks for Unscaled Value Conversions ---

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOfUnscaled(1234567890123L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongZero(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOfUnscaled(0L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongOne(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOfUnscaled(1L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongMinusOne(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOfUnscaled(-1L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongLarge(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOfUnscaled(Long.MAX_VALUE);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongWithScale(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOfUnscaled(1234567890123L, 5);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongWithScaleAndRounding(Blackhole bh) {
        Decimal12f result = Decimal12f.valueOfUnscaled(1234567890123L, 5, RoundingMode.HALF_EVEN);
        bh.consume(result);
    }

    // --- Benchmarks for Arithmetic/Utility ---

    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        Decimal12f d1 = Decimal12f.ONE;
        Decimal12f d2 = Decimal12f.TWO;
        Multipliable12f product = d1.multiplyExact();
        bh.consume(product);
    }

    @Benchmark
    public void benchmarkToMutableDecimal(Blackhole bh) {
        Decimal12f original = Decimal12f.valueOf(100.5);
        org.decimal4j.mutable.MutableDecimal12f mutable = original.toMutableDecimal();
        bh.consume(mutable);
    }
}
