package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factory4f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.mutable.MutableDecimal4f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
public class Decimal4fBenchmark {

    // --- State Fields for Inputs ---
    private Decimal4f longInput;
    private Decimal4f doubleInput;
    private Decimal4f bigDecimalInput;
    private String stringInput;
    private BigInteger bigIntegerInput;
    private Decimal<?> decimalInput;

    // --- Setup Method ---
    @Setup
    public void setup() {
        // 1. Long input (within bounds)
        long longVal = 123456789L;
        longInput = Decimal4f.valueOf(longVal);

        // 2. Double input
        double doubleVal = 12345.6789;
        doubleInput = Decimal4f.valueOf(doubleVal);

        // 3. BigDecimal input
        BigDecimal bdVal = new BigDecimal("98765.4321");
        bigDecimalInput = Decimal4f.valueOf(bdVal);

        // 4. String input
        stringInput = "123456789.1234";
        
        // 5. BigInteger input
        bigIntegerInput = new BigInteger("1234567890123456789");
        
        // 6. Decimal input (using a known value)
        decimalInput = Decimal4f.valueOf(new BigDecimal("5.1234"));
    }

    // --- Creation Benchmarks ---

    @Benchmark
    public void createFromLong(Blackhole bh) {
        Decimal4f result = Decimal4f.valueOf(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void createFromDouble(Blackhole bh) {
        Decimal4f result = Decimal4f.valueOf(12345.6789);
        bh.consume(result);
    }

    @Benchmark
    public void createFromBigDecimal(Blackhole bh) {
        BigDecimal bdVal = new BigDecimal("98765.4321");
        Decimal4f result = Decimal4f.valueOf(bdVal);
        bh.consume(result);
    }

    @Benchmark
    public void createFromString(Blackhole bh) {
        Decimal4f result = Decimal4f.valueOf("123456789.1234");
        bh.consume(result);
    }

    @Benchmark
    public void createFromBigInteger(Blackhole bh) {
        Decimal4f result = Decimal4f.valueOf(new BigInteger("1234567890123456789"));
        bh.consume(result);
    }

    @Benchmark
    public void createFromDecimal(Blackhole bh) {
        Decimal4f result = Decimal4f.valueOf(decimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void createFromUnscaledLong(Blackhole bh) {
        long unscaled = 123456789L;
        Decimal4f result = Decimal4f.valueOfUnscaled(unscaled);
        bh.consume(result);
    }

    @Benchmark
    public void createFromUnscaledLongWithScale(Blackhole bh) {
        long unscaled = 123456789L;
        int scale = 2;
        Decimal4f result = Decimal4f.valueOfUnscaled(unscaled, scale);
        bh.consume(result);
    }

    @Benchmark
    public void createFromUnscaledLongWithRounding(Blackhole bh) {
        long unscaled = 123456789L;
        int scale = 2;
        RoundingMode mode = RoundingMode.HALF_UP;
        Decimal4f result = Decimal4f.valueOfUnscaled(unscaled, scale, mode);
        bh.consume(result);
    }

    // --- Utility/Arithmetic Benchmarks ---

    @Benchmark
    public void multiplyExact(Blackhole bh) {
        // Multiply longInput by Decimal4f.ONE
        org.decimal4j.exact.Multipliable4f multiplier = longInput.multiplyExact();
        // We consume the multiplier object to ensure the operation is measured
        bh.consume(multiplier);
    }

    @Benchmark
    public void toMutableDecimal(Blackhole bh) {
        MutableDecimal4f mutableResult = longInput.toMutableDecimal();
        bh.consume(mutableResult);
    }

    @Benchmark
    public void toImmutableDecimal(Blackhole bh) {
        Decimal4f immutableResult = longInput.toImmutableDecimal();
        bh.consume(immutableResult);
    }
}
