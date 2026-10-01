package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal16f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable16f;
import org.decimal4j.scale.Scale16f;
import org.decimal4j.factory.Factory16f;
import org.decimal4j.mutable.MutableDecimal16f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal16fBenchmark {

    // --- Inputs for Static Factory Methods ---
    private long largeLongInput;
    private float fractionalFloatInput;
    private double fractionalDoubleInput;
    private BigInteger largeBigIntegerInput;
    private BigDecimal preciseBigDecimalInput;
    private String decimalStringInput;
    private Decimal<?> otherDecimalInput;
    private long unscaledLongInput;
    private RoundingMode roundingModeInput;

    // --- Inputs for Instance Methods ---
    private Decimal16f instanceForMethods;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Long input
        largeLongInput = 9223372036854775807L; // Long.MAX_VALUE

        // 2. Float input
        fractionalFloatInput = 123.456789f;

        // 3. Double input
        fractionalDoubleInput = 123.45678901234567;

        // 4. BigInteger input
        largeBigIntegerInput = new BigInteger("123456789012345678901234567890");

        // 5. BigDecimal input
        preciseBigDecimalInput = new BigDecimal("987.6543210987654321");

        // 6. String input
        decimalStringInput = "123.45678901234567";

        // 7. Decimal<?> input (using a Decimal16f instance)
        otherDecimalInput = Decimal16f.valueOf(123.456789);

        // 8. Unscaled long input
        unscaledLongInput = 123456789L;

        // 9. Rounding Mode input
        roundingModeInput = RoundingMode.HALF_UP;

        // Instance setup
        instanceForMethods = Decimal16f.valueOf(10.5);
    }

    // --- Benchmarks for Static Factory Methods ---

    @Benchmark
    public Decimal16f benchmarkValueOfLong(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(largeLongInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfFloatDefault(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(fractionalFloatInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfFloatCustomRounding(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(fractionalFloatInput, roundingModeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfDoubleDefault(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(fractionalDoubleInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfDoubleCustomRounding(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(fractionalDoubleInput, roundingModeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfBigInteger(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(largeBigIntegerInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfBigDecimalDefault(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(preciseBigDecimalInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfBigDecimalCustomRounding(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(preciseBigDecimalInput, roundingModeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfStringDefault(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(decimalStringInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfStringCustomRounding(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(decimalStringInput, roundingModeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfDecimalDefault(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(otherDecimalInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfDecimalCustomRounding(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOf(otherDecimalInput, roundingModeInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfUnscaledLong(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOfUnscaled(unscaledLongInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfUnscaledLongCustomRounding(Blackhole bh) {
        Decimal16f result = Decimal16f.valueOfUnscaled(unscaledLongInput, Decimal16f.SCALE, roundingModeInput);
        bh.consume(result);
        return result;
    }

    // --- Benchmarks for Instance Methods ---

    @Benchmark
    public Multipliable16f benchmarkMultiplyExact(Blackhole bh) {
        Multipliable16f result = instanceForMethods.multiplyExact();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal16f benchmarkToMutableDecimal(Blackhole bh) {
        MutableDecimal16f result = instanceForMethods.toMutableDecimal();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal16f benchmarkToImmutableDecimal(Blackhole bh) {
        Decimal16f result = instanceForMethods.toImmutableDecimal();
        bh.consume(result);
        return result;
    }
}
