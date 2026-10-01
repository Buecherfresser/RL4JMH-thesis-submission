package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.scale.Scale13f;
import org.decimal4j.exact.Multipliable13f;
import org.decimal4j.mutable.MutableDecimal13f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal13fBenchmark {

    // --- State Fields ---
    private Decimal13f testDecimal;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private double doubleInput;
    private BigInteger bigIntegerInput;
    private long longInput;
    private int scale = Decimal13f.SCALE;
    private RoundingMode roundingMode = RoundingMode.HALF_UP;

    // --- Setup ---
    @Setup
    public void setup() {
        // Setup a base decimal value for arithmetic tests
        this.testDecimal = Decimal13f.valueOf(123.4567890123456789); // Should be rounded/truncated to scale 13
        
        // Setup complex inputs for conversion tests
        this.bigDecimalInput = new BigDecimal("1234567890123.456789");
        this.stringInput = "1234567890123.456789";
        this.doubleInput = 123.4567890123456789;
        this.bigIntegerInput = new BigInteger("9876543210987654321");
        this.longInput = 1234567890123456789L;
    }

    // --- Benchmarks for Conversion ---

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        Decimal13f result = Decimal13f.valueOf(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        Decimal13f result = Decimal13f.valueOf(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal13f result = Decimal13f.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        Decimal13f result = Decimal13f.valueOf(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        Decimal13f result = Decimal13f.valueOf(bigIntegerInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        Decimal13f result = Decimal13f.valueOfUnscaled(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongWithScale(Blackhole bh) {
        Decimal13f result = Decimal13f.valueOfUnscaled(longInput, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongWithRounding(Blackhole bh) {
        Decimal13f result = Decimal13f.valueOfUnscaled(longInput, scale, roundingMode);
        bh.consume(result);
    }

    // --- Benchmarks for Arithmetic ---

    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        Decimal13f result = testDecimal.add(Decimal13f.ONE);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSubtract(Blackhole bh) {
        Decimal13f result = testDecimal.subtract(Decimal13f.TWO);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        // Test multiplication that might widen the scale
        Multipliable13f result = testDecimal.multiplyExact();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkDivide(Blackhole bh) {
        // Test division
        Decimal13f result = testDecimal.divide(Decimal13f.valueOf(Decimal13f.valueOf(1000L)));
        bh.consume(result);
    }
    
    @Benchmark
    public void benchmarkRound(Blackhole bh) {
        // Test rounding functionality
        Decimal13f result = Decimal13f.valueOf(1.234567890123456789, RoundingMode.HALF_UP);
        bh.consume(result);
    }

    // --- Benchmarks for Mutability ---

    @Benchmark
    public void benchmarkToMutableDecimal(Blackhole bh) {
        MutableDecimal13f mutableDecimal = testDecimal.toMutableDecimal();
        bh.consume(mutableDecimal);
    }
}
