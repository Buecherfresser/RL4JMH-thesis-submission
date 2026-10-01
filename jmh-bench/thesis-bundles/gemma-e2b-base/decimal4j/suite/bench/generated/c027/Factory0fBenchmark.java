package bench.generated.c027;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factory0f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.scale.Scale0f;
import org.decimal4j.scale.ScaleMetrics;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory0fBenchmark {

    // --- State Fields for Inputs ---
    private long longInput;
    private double doubleInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private BigInteger bigIntegerInput;
    private Decimal<?> decimalInput;
    private String parseStringInput;

    // --- Setup Method ---
    @Setup
    public void setup() {
        // Initialize inputs using non-literal values where possible, or complex objects
        longInput = 1234567890123L;
        doubleInput = 3.1415926535;
        bigDecimalInput = new BigDecimal("1234567890123.456789");
        stringInput = "1234567890123";
        bigIntegerInput = new BigInteger("9876543210987654321");
        
        // Create a representative Decimal<?> object (using Factory0f.INSTANCE for creation)
        this.decimalInput = Factory0f.INSTANCE.valueOf(longInput);
        
        parseStringInput = stringInput;
    }

    // --- Benchmarks for valueOf(long) ---
    @Benchmark
    public void valueOfLong(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.valueOf(longInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(double) ---
    @Benchmark
    public void valueOfDouble(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.valueOf(doubleInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(BigDecimal) ---
    @Benchmark
    public void valueOfBigDecimal(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(BigInteger) ---
    @Benchmark
    public void valueOfBigInteger(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.valueOf(bigIntegerInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(String) / parse(String) ---
    @Benchmark
    public void parseString(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.parse(parseStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void parseStringWithRounding(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.parse(parseStringInput, RoundingMode.HALF_UP);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(float) ---
    @Benchmark
    public void valueOfFloat(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.valueOf((float) doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfFloatWithRounding(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.valueOf((float) doubleInput, RoundingMode.HALF_DOWN);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(double, RoundingMode) ---
    @Benchmark
    public void valueOfDoubleWithRounding(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.valueOf(doubleInput, RoundingMode.HALF_EVEN);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(Decimal<?>) ---
    @Benchmark
    public void valueOfDecimal(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.valueOf(decimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDecimalWithRounding(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.valueOf(decimalInput, RoundingMode.CEILING);
        bh.consume(result);
    }

    // --- Benchmarks for valueOfUnscaled(long) ---
    @Benchmark
    public void valueOfUnscaledLong(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.valueOfUnscaled(longInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOfUnscaled(long, int scale) ---
    @Benchmark
    public void valueOfUnscaledLongScale(Blackhole bh) {
        Decimal0f result = Factory0f.INSTANCE.valueOfUnscaled(longInput, 5);
        bh.consume(result);
    }

    // --- Benchmarks for newMutable() ---
    @Benchmark
    public void newMutable(Blackhole bh) {
        org.decimal4j.mutable.MutableDecimal0f result = Factory0f.INSTANCE.newMutable();
        bh.consume(result);
    }

    // --- Benchmarks for newArray(int length) ---
    @Benchmark
    public void newArray(Blackhole bh) {
        Decimal0f[] result = Factory0f.INSTANCE.newArray(10);
        bh.consume(result);
    }
}
