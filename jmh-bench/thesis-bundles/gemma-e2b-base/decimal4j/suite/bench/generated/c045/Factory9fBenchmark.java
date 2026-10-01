package bench.generated.c045;

import org.decimal4j.factory.Factory9f;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.scale.Scale9f;
import org.decimal4j.scale.ScaleMetrics;

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
    private float floatInput;
    private double doubleInput;
    private BigInteger bigIntegerInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private long unscaledLongInput;
    private int scaleInput;
    private RoundingMode roundingMode;
    private String parseStringInput;
    private int arrayLength;

    // --- Setup Method ---

    @Setup
    public void setup() {
        // Setup numeric inputs
        longInput = 123456789L;
        floatInput = 3.14159f;
        doubleInput = 123456789.123456789;
        bigIntegerInput = new BigInteger("9876543210");
        bigDecimalInput = new BigDecimal("123456789.123456789");
        stringInput = "123456789";
        unscaledLongInput = 987654321L;
        scaleInput = 9;
        roundingMode = RoundingMode.HALF_UP;
        parseStringInput = "123456789";
        arrayLength = 100;
    }

    // --- Benchmarks for valueOf(long) ---

    @Benchmark
    public void valueOfLong(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOf(longInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(float) ---

    @Benchmark
    public void valueOfFloat(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOf(floatInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfFloatWithRounding(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOf(floatInput, roundingMode);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(double) ---

    @Benchmark
    public void valueOfDouble(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOf(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDoubleWithRounding(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOf(doubleInput, roundingMode);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(BigInteger) ---

    @Benchmark
    public void valueOfBigInteger(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOf(bigIntegerInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(BigDecimal) ---

    @Benchmark
    public void valueOfBigDecimal(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOf(Decimal<?>) ---

    @Benchmark
    public void valueOfDecimal(Blackhole bh) {
        // Create a dummy Decimal<?> for testing the generic overload
        org.decimal4j.api.Decimal<?> dummyDecimal = Factory9f.INSTANCE.valueOf(longInput);
        Decimal9f result = Factory9f.INSTANCE.valueOf(dummyDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDecimalWithRounding(Blackhole bh) {
        org.decimal4j.api.Decimal<?> dummyDecimal = Factory9f.INSTANCE.valueOf(longInput);
        Decimal9f result = Factory9f.INSTANCE.valueOf(dummyDecimal, roundingMode);
        bh.consume(result);
    }

    // --- Benchmarks for parse(String) ---

    @Benchmark
    public void parseString(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.parse(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void parseStringWithRounding(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.parse(stringInput, roundingMode);
        bh.consume(result);
    }

    // --- Benchmarks for valueOfUnscaled(long) ---

    @Benchmark
    public void valueOfUnscaledLong(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOfUnscaled(unscaledLongInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOfUnscaled(long, int scale) ---

    @Benchmark
    public void valueOfUnscaledLongScale(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOfUnscaled(unscaledLongInput, scaleInput);
        bh.consume(result);
    }

    // --- Benchmarks for valueOfUnscaled(long, int scale, RoundingMode) ---

    @Benchmark
    public void valueOfUnscaledLongScaleRounding(Blackhole bh) {
        Decimal9f result = Factory9f.INSTANCE.valueOfUnscaled(unscaledLongInput, scaleInput, roundingMode);
        bh.consume(result);
    }

    // --- Benchmarks for newArray(int length) ---

    @Benchmark
    public void newArray(Blackhole bh) {
        Decimal9f[] result = Factory9f.INSTANCE.newArray(arrayLength);
        bh.consume(result);
    }

    // --- Benchmarks for newMutable() ---

    @Benchmark
    public void newMutable(Blackhole bh) {
        MutableDecimal9f result = Factory9f.INSTANCE.newMutable();
        bh.consume(result);
    }

    // --- Benchmarks for newMutableArray(int length) ---

    @Benchmark
    public void newMutableArray(Blackhole bh) {
        MutableDecimal9f[] result = Factory9f.INSTANCE.newMutableArray(arrayLength);
        bh.consume(result);
    }
}
