package bench.generated.c049;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.scale.Scale0f;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal0fBenchmark {

    // --- State Fields for Inputs ---
    private long longInput;
    private double doubleInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private BigInteger bigIntegerInput;
    private float floatInput;

    // Rounding modes for testing
    private final RoundingMode HALF_UP = RoundingMode.HALF_UP;
    private final RoundingMode DOWN = RoundingMode.DOWN;

    @Setup
    public void setup() {
        // Setup long input
        this.longInput = 1234567890123L;

        // Setup double input
        this.doubleInput = 3.1415926535;

        // Setup BigDecimal input
        this.bigDecimalInput = new BigDecimal("1234567890123.456");

        // Setup String input
        this.stringInput = "1234567890123.456";

        // Setup BigInteger input
        this.bigIntegerInput = new BigInteger("9876543210987654321");

        // Setup float input
        this.floatInput = 1.2345f;
    }

    // --- Benchmarks for Long Input ---

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(longInput);
        bh.consume(result);
    }

    // --- Benchmarks for Double Input ---

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(doubleInput, HALF_UP);
        bh.consume(result);
    }

    // --- Benchmarks for BigDecimal Input ---

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimalWithRounding(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(bigDecimalInput, DOWN);
        bh.consume(result);
    }

    // --- Benchmarks for String Input ---

    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfStringWithRounding(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(stringInput, HALF_UP);
        bh.consume(result);
    }

    // --- Benchmarks for BigInteger Input ---

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(bigIntegerInput);
        bh.consume(result);
    }

    // --- Benchmarks for Float Input ---

    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(floatInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfFloatWithRounding(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(floatInput, HALF_UP);
        bh.consume(result);
    }
}
