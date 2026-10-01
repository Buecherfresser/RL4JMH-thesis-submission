package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factory7f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.scale.Scale7f;
import org.decimal4j.scale.ScaleMetrics;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory7fBenchmark {

    private Factory7f factory7f = Factory7f.INSTANCE;

    // --- Setup Inputs ---
    private long longInput;
    private double doubleInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private BigInteger bigIntegerInput;
    private RoundingMode roundingMode;
    private int scale;
    private int arrayLength;

    @Setup
    public void setup() {
        // Setup common inputs
        longInput = 123456789L;
        doubleInput = 3.1415926535;
        bigDecimalInput = new BigDecimal("9876543210.1234567");
        stringInput = "12345678901234567";
        bigIntegerInput = new BigInteger("12345678901234567890");
        roundingMode = RoundingMode.HALF_UP;
        scale = 7;
        arrayLength = 100;
    }

    // --- Benchmarks for Immutable Decimal7f creation ---

    @Benchmark
    public void valueOf_long(Blackhole bh) {
        Decimal7f result = factory7f.valueOf(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_double(Blackhole bh) {
        Decimal7f result = factory7f.valueOf(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_bigdecimal(Blackhole bh) {
        Decimal7f result = factory7f.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_decimal(Blackhole bh) {
        // Use a simple Decimal<?> instance for testing generic path
        Decimal<?> input = Decimal7f.valueOf(100L);
        Decimal7f result = factory7f.valueOf(input);
        bh.consume(result);
    }

    @Benchmark
    public void parse_string(Blackhole bh) {
        Decimal7f result = factory7f.parse(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_double_with_rounding(Blackhole bh) {
        Decimal7f result = factory7f.valueOf(doubleInput, roundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_bigdecimal_with_rounding(Blackhole bh) {
        Decimal7f result = factory7f.valueOf(bigDecimalInput, roundingMode);
        bh.consume(result);
    }

    // --- Benchmarks for Unscaled Value creation ---

    @Benchmark
    public void valueOfUnscaled_long(Blackhole bh) {
        Decimal7f result = factory7f.valueOfUnscaled(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaled_long_with_scale(Blackhole bh) {
        Decimal7f result = factory7f.valueOfUnscaled(longInput, scale);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaled_long_with_scale_and_rounding(Blackhole bh) {
        Decimal7f result = factory7f.valueOfUnscaled(longInput, scale, roundingMode);
        bh.consume(result);
    }

    // --- Benchmarks for Mutable Decimal7f creation ---

    @Benchmark
    public void newMutable(Blackhole bh) {
        MutableDecimal7f result = factory7f.newMutable();
        bh.consume(result);
    }

    // --- Benchmarks for Array creation ---

    @Benchmark
    public void newArray(Blackhole bh) {
        Decimal7f[] result = factory7f.newArray(arrayLength);
        bh.consume(result);
    }

    @Benchmark
    public void newMutableArray(Blackhole bh) {
        MutableDecimal7f[] result = factory7f.newMutableArray(arrayLength);
        bh.consume(result);
    }
}
