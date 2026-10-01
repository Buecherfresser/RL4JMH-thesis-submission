package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal15f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable15f;
import org.decimal4j.scale.Scale15f;
import org.decimal4j.factory.Factory15f;
import org.decimal4j.mutable.MutableDecimal15f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal15fBenchmark {

    private String stringInput;
    private long longInput;
    private float floatInput;
    private double doubleInput;
    private BigInteger bigIntegerInput;
    private BigDecimal bigDecimalInput;
    private Decimal15f decimalInput;
    private long unscaledLongInput;
    private RoundingMode roundingMode;

    @Setup(Level.Trial)
    public void setup() {
        // Inputs for String parsing
        stringInput = "123456789012345.67890123456789";

        // Inputs for Long conversion
        longInput = 9223372036854775807L; // Near MAX_VALUE

        // Inputs for Float conversion
        floatInput = 123.4567f;

        // Inputs for Double conversion
        doubleInput = 123.4567890123456789;

        // Inputs for BigInteger conversion
        bigIntegerInput = BigInteger.valueOf(9223372036854775807L);

        // Inputs for BigDecimal conversion
        bigDecimalInput = new BigDecimal("123.4567890123456789");

        // Input for Decimal<?> conversion (using a known Decimal15f constant)
        decimalInput = Decimal15f.TEN;

        // Input for Unscaled Long conversion
        unscaledLongInput = 1234567890123L;

        // Input for Rounding Mode
        roundingMode = RoundingMode.HALF_UP;
    }

    // --- Factory Method Benchmarks ---

    @Benchmark
    public Decimal15f benchmarkValueOfLong(Blackhole bh) {
        Decimal15f result = Decimal15f.valueOf(longInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkValueOfFloat(Blackhole bh) {
        Decimal15f result = Decimal15f.valueOf(floatInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkValueOfDouble(Blackhole bh) {
        Decimal15f result = Decimal15f.valueOf(doubleInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkValueOfBigInteger(Blackhole bh) {
        Decimal15f result = Decimal15f.valueOf(bigIntegerInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal15f result = Decimal15f.valueOf(bigDecimalInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkValueOfDecimal(Blackhole bh) {
        Decimal15f result = Decimal15f.valueOf(decimalInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkValueOfString(Blackhole bh) {
        Decimal15f result = new Decimal15f(stringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkValueOfStringWithRounding(Blackhole bh) {
        // Using a string that requires rounding if it were longer than 15 digits
        String longString = "12345678901234567"; 
        Decimal15f result = Decimal15f.valueOf(longString, roundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        Decimal15f result = Decimal15f.valueOf(doubleInput, roundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkValueOfBigDecimalWithRounding(Blackhole bh) {
        Decimal15f result = Decimal15f.valueOf(bigDecimalInput, roundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkValueOfDecimalWithRounding(Blackhole bh) {
        Decimal15f result = Decimal15f.valueOf(decimalInput, roundingMode);
        bh.consume(result);
        return result;
    }

    // --- Unscaled Value Benchmarks ---

    @Benchmark
    public Decimal15f benchmarkValueOfUnscaledLong(Blackhole bh) {
        Decimal15f result = Decimal15f.valueOfUnscaled(unscaledLongInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkValueOfUnscaledLongWithScale(Blackhole bh) {
        // Using a different scale (e.g., 5)
        Decimal15f result = Decimal15f.valueOfUnscaled(unscaledLongInput, 5);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkValueOfUnscaledLongWithScaleAndRounding(Blackhole bh) {
        // Using a different scale (e.g., 5) and rounding mode
        Decimal15f result = Decimal15f.valueOfUnscaled(unscaledLongInput, 5, roundingMode);
        bh.consume(result);
        return result;
    }

    // --- Operation Benchmarks ---

    @Benchmark
    public Multipliable15f benchmarkMultiplyExact(Blackhole bh) {
        Multipliable15f result = Decimal15f.TEN.multiplyExact();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal15f benchmarkToMutableDecimal(Blackhole bh) {
        MutableDecimal15f result = Decimal15f.TEN.toMutableDecimal();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal15f benchmarkToImmutableDecimal(Blackhole bh) {
        // Since Decimal15f is immutable, this is a no-op copy/return
        Decimal15f result = Decimal15f.TEN.toImmutableDecimal();
        bh.consume(result);
        return result;
    }
}
