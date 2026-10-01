package bench.generated.c037;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.scale.Scale1f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.factory.Factory1f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory1fBenchmark {

    // --- State Fields ---
    private long longInput;
    private double doubleInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private BigInteger bigIntegerInput;
    private RoundingMode roundingMode;
    private int arrayLength;

    // --- Setup ---
    @Setup
    public void setup() {
        // Setup common inputs
        longInput = 123456789L;
        doubleInput = 3.1415926535;
        bigDecimalInput = new BigDecimal("1234567890123456789");
        stringInput = "123456789";
        bigIntegerInput = new BigInteger("9876543210");
        roundingMode = RoundingMode.HALF_UP;
        arrayLength = 100;
    }

    // --- Benchmarks for valueOf(long) ---
    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        Decimal1f result = Factory1f.INSTANCE.valueOf(longInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(double) ---
    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        Decimal1f result = Factory1f.INSTANCE.valueOf(doubleInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(BigDecimal) ---
    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal1f result = Factory1f.INSTANCE.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    // --- Benchmarks for parse(String) ---
    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        Decimal1f result = Factory1f.INSTANCE.parse(stringInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(double, RoundingMode) ---
    @Benchmark
    public void benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        Decimal1f result = Factory1f.INSTANCE.valueOf(doubleInput, roundingMode);
        bh.consume(result);
    }

    // --- Benchmarks for valueOfUnscaled(long) ---
    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        Decimal1f result = Factory1f.INSTANCE.valueOfUnscaled(longInput);
        bh.consume(result);
    }

    // --- Benchmarks for newMutable() ---
    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        MutableDecimal1f result = Factory1f.INSTANCE.newMutable();
        bh.consume(result);
    }

    // --- Benchmarks for newArray(int length) ---
    @Benchmark
    public void benchmarkNewArray(Blackhole bh) {
        Decimal1f[] result = Factory1f.INSTANCE.newArray(arrayLength);
        bh.consume(result);
    }
}
