package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.immutable.Decimal0f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
public class Decimal0fBenchmark {

    // --- State Fields for Inputs ---
    private long longInput;
    private double doubleInput;
    private BigInteger bigIntInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private float floatInput;
    private double doubleInputForRounding;
    private BigDecimal bigDecimalForRounding;
    private String stringInputForRounding;

    // Rounding Modes
    private final RoundingMode HALF_UP = RoundingMode.HALF_UP;

    @Setup
    public void setup() {
        // Setup Long Input (A moderately large number)
        this.longInput = 123456789012345L;

        // Setup Double Input
        this.doubleInput = 3.1415926535;
        this.doubleInputForRounding = 123.456789;

        // Setup BigInteger Input
        this.bigIntInput = new BigInteger("9876543210987654321");

        // Setup BigDecimal Input
        this.bigDecimalInput = new BigDecimal("123456789012345.6789");
        this.bigDecimalForRounding = new BigDecimal("123456789012345.6789");

        // Setup String Input
        this.stringInput = "123456789012345.6789";
        this.stringInputForRounding = "123456789012345.6789";

        // Setup Float Input
        this.floatInput = 1.2345f;
    }

    // --- Benchmarks for Long Conversions ---

    @Benchmark
    public void valueOf_Long(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaled_Long(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOfUnscaled(longInput);
        bh.consume(result);
    }

    // --- Benchmarks for Double Conversions ---

    @Benchmark
    public void valueOf_Double(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_Double_WithRounding(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(doubleInputForRounding, HALF_UP);
        bh.consume(result);
    }

    // --- Benchmarks for BigInteger Conversions ---

    @Benchmark
    public void valueOf_BigInteger(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(bigIntInput);
        bh.consume(result);
    }

    // --- Benchmarks for BigDecimal Conversions ---

    @Benchmark
    public void valueOf_BigDecimal(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_BigDecimal_WithRounding(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(bigDecimalForRounding, HALF_UP);
        bh.consume(result);
    }

    // --- Benchmarks for String Conversions ---

    @Benchmark
    public void valueOf_String(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_String_WithRounding(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(stringInputForRounding, HALF_UP);
        bh.consume(result);
    }

    // --- Benchmarks for Float Conversions ---

    @Benchmark
    public void valueOf_Float(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(floatInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOf_Float_WithRounding(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(floatInput, HALF_UP);
        bh.consume(result);
    }
}
