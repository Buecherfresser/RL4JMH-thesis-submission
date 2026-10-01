package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.scale.Scale17f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Decimal17fBenchmark {

    // --- State Fields ---
    private Decimal17f baseDecimal;
    private long longInput;
    private float floatInput;
    private double doubleInput;
    private java.math.BigInteger bigIntInput;
    private java.math.BigDecimal bigDecimalInput;
    private String stringInput;
    private Decimal<?> decimalInput;

    // --- Setup ---
    @Setup
    public void setup() {
        // 1. Initialize base Decimal17f
        this.baseDecimal = Decimal17f.ONE;

        // 2. Initialize various input types
        this.longInput = 123456789012345L;
        this.floatInput = 3.14159f;
        this.doubleInput = 123456789012345.6789;
        this.bigIntInput = new java.math.BigInteger("9876543210987654321");
        this.bigDecimalInput = new java.math.BigDecimal("123456789012345.6789");
        this.stringInput = "123456789012345.6789";
        
        // Create a representative Decimal object for testing valueOf(Decimal<?>)
        // Since Decimal17f implements Decimal, we can cast it.
        this.decimalInput = (Decimal) this.baseDecimal;
    }

    // --- Benchmarks: ValueOf Conversions ---

    @Benchmark
    public void valueOfLong(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfFloat(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf(floatInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDouble(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigInteger(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf(bigIntInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigDecimal(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfString(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDecimal(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf(decimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaledLong(Blackhole bh) {
        long unscaledValue = 123456789012345L;
        Decimal17f result = Decimal17f.valueOfUnscaled(unscaledValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaledLongWithScale(Blackhole bh) {
        long unscaledValue = 123456789012345L;
        int scale = 10;
        Decimal17f result = Decimal17f.valueOfUnscaled(unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaledLongWithRounding(Blackhole bh) {
        long unscaledValue = 123456789012345L;
        int scale = 10;
        java.math.RoundingMode mode = java.math.RoundingMode.HALF_UP;
        Decimal17f result = Decimal17f.valueOfUnscaled(unscaledValue, scale, mode);
        bh.consume(result);
    }

    // --- Benchmarks: Utility and Conversion ---

    @Benchmark
    public void multiplyExact(Blackhole bh) {
        // Tests the Multipliable17f implementation
        org.decimal4j.exact.Multipliable17f product = baseDecimal.multiplyExact();
        bh.consume(product);
    }

    @Benchmark
    public void toMutableDecimal(Blackhole bh) {
        org.decimal4j.mutable.MutableDecimal17f mutableDecimal = baseDecimal.toMutableDecimal();
        bh.consume(mutableDecimal);
    }

    @Benchmark
    public void toImmutableDecimal(Blackhole bh) {
        Decimal17f immutableDecimal = baseDecimal.toImmutableDecimal();
        bh.consume(immutableDecimal);
    }
}
