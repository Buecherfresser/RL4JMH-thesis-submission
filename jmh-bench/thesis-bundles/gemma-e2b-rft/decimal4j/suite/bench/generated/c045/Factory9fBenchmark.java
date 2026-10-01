package bench.generated.c045;

import org.decimal4j.factory.Factory9f;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.mutable.MutableDecimal9f;
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
public class Factory9fBenchmark {

    // --- State Fields for Inputs ---
    private long longInput;
    private double doubleInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private BigInteger bigIntegerInput;
    private int arrayLength;
    private RoundingMode roundingMode;

    // --- Setup Method ---
    @Setup
    public void setup() {
        // Setup numeric inputs
        longInput = 1234567890123L;
        doubleInput = 3.1415926535;
        bigDecimalInput = new BigDecimal("9876543210.123456789");
        stringInput = "1234567890123";
        bigIntegerInput = new BigInteger("9876543210123456789");
        arrayLength = 1000;
        roundingMode = RoundingMode.HALF_UP;
    }

    // --- Benchmarks for valueOf(long) ---
    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOf(longInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(double) ---
    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOf(doubleInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(BigDecimal) ---
    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(String) / parse(String) ---
    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.parse(stringInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOfUnscaled(long) ---
    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOfUnscaled(longInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOfUnscaled(long, int scale) ---
    @Benchmark
    public void benchmarkValueOfUnscaledLongScale(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOfUnscaled(longInput, 9);
        bh.consume(result);
    }

    // --- Benchmarks for valueOfUnscaled(long, int scale, RoundingMode) ---
    @Benchmark
    public void benchmarkValueOfUnscaledLongScaleRounding(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOfUnscaled(longInput, 9, roundingMode);
        bh.consume(result);
    }

    // --- Benchmarks for Mutable Decimal creation ---
    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        MutableDecimal9f result = Factory9f.INSTANCE.newMutable();
        bh.consume(result);
    }

    // --- Benchmarks for Immutable Array creation ---
    @Benchmark
    public void benchmarkNewArray(Blackhole bh) {
        Decimal9f[] result = Factory9f.INSTANCE.newArray(arrayLength);
        bh.consume(result);
    }

    // --- Benchmarks for Mutable Array creation ---
    @Benchmark
    public void benchmarkNewMutableArray(Blackhole bh) {
        MutableDecimal9f[] result = Factory9f.INSTANCE.newMutableArray(arrayLength);
        bh.consume(result);
    }
}
