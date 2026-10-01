package bench.generated.c041;

import org.decimal4j.factory.Factory5f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory5fBenchmark {

    // --- State Fields for Inputs ---
    private long longValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private long unscaledValue;
    private int scale;
    private RoundingMode roundingMode;

    // --- Setup Method ---
    @Setup
    public void setup() {
        // Setup basic numeric values
        this.longValue = 1234567890123L;
        this.doubleValue = 3.1415926535;
        this.bigIntValue = new BigInteger("9876543210987654321");
        this.bigDecimalValue = new BigDecimal("1234567890123.456789");
        this.stringValue = "1234567890123";
        this.unscaledValue = 9876543210L;
        this.scale = 5;
        this.roundingMode = RoundingMode.HALF_UP;
    }

    // --- Benchmarks for valueOf(long) ---

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        Factory5f.INSTANCE.valueOf(longValue);
        bh.consume(null);
    }

    // --- Benchmarks for valueOf(double) ---

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        Factory5f.INSTANCE.valueOf(doubleValue);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        Factory5f.INSTANCE.valueOf(doubleValue, roundingMode);
        bh.consume(null);
    }

    // --- Benchmarks for valueOf(BigInteger) ---

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        Factory5f.INSTANCE.valueOf(bigIntValue);
        bh.consume(null);
    }

    // --- Benchmarks for valueOf(BigDecimal) ---

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        Factory5f.INSTANCE.valueOf(bigDecimalValue);
        bh.consume(null);
    }

    // --- Benchmarks for valueOf(String) / parse(String) ---

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        Factory5f.INSTANCE.parse(stringValue);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkParseStringWithRounding(Blackhole bh) {
        Factory5f.INSTANCE.parse(stringValue, roundingMode);
        bh.consume(null);
    }

    // --- Benchmarks for Unscaled ValueOf ---

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        Factory5f.INSTANCE.valueOfUnscaled(unscaledValue);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongWithScale(Blackhole bh) {
        Factory5f.INSTANCE.valueOfUnscaled(unscaledValue, scale);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongWithScaleAndRounding(Blackhole bh) {
        Factory5f.INSTANCE.valueOfUnscaled(unscaledValue, scale, roundingMode);
        bh.consume(null);
    }

    // --- Benchmarks for Array/Mutable Creation ---

    @Benchmark
    public void benchmarkNewArray(Blackhole bh) {
        Factory5f.INSTANCE.newArray(10);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        Factory5f.INSTANCE.newMutable();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNewMutableArray(Blackhole bh) {
        Factory5f.INSTANCE.newMutableArray(5);
        bh.consume(null);
    }
}
