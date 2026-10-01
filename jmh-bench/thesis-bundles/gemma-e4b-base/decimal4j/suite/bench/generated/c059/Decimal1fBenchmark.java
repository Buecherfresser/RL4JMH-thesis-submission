package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.mutable.MutableDecimal1f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable1f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal1fBenchmark {

    private Decimal1f decA;
    private Decimal1f decB;
    private String inputString;
    private long inputLong;
    private double inputDouble;
    private BigDecimal inputBigDecimal;
    private BigInteger inputBigInteger;

    @Setup
    public void setup() {
        // Initialize standard instances for arithmetic tests
        decA = Decimal1f.valueOf(12345L);
        decB = Decimal1f.valueOf(67890L);

        // Initialize inputs for conversion tests
        inputString = "123.456"; // Requires rounding
        inputLong = 987654321L;
        inputDouble = 3.1415926535;
        inputBigDecimal = new BigDecimal("987.654321");
        inputBigInteger = new BigInteger("123456789012345");
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public Decimal1f testValueOfLong(Blackhole bh) {
        Decimal1f result = Decimal1f.valueOf(inputLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal1f testValueOfDouble(Blackhole bh) {
        Decimal1f result = Decimal1f.valueOf(inputDouble);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal1f testValueOfBigDecimal(Blackhole bh) {
        Decimal1f result = Decimal1f.valueOf(inputBigDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal1f testValueOfBigInteger(Blackhole bh) {
        Decimal1f result = Decimal1f.valueOf(inputBigInteger);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal1f testValueOfDecimal(Blackhole bh) {
        // Use decA as the input Decimal<?>
        Decimal1f result = Decimal1f.valueOf(decA);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal1f testValueOfString(Blackhole bh) {
        Decimal1f result = new Decimal1f(inputString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal1f testValueOfStringWithRounding(Blackhole bh) {
        // Test string parsing with explicit rounding mode
        String inputStringRounded = "123.4567";
        Decimal1f result = Decimal1f.valueOf(inputStringRounded, RoundingMode.HALF_DOWN);
        bh.consume(result);
        return result;
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public Decimal1f testAdd(Blackhole bh) {
        Decimal1f result = decA.add(decB);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal1f testMultiply(Blackhole bh) {
        Decimal1f result = decA.multiply(decB);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal1f testDivide(Blackhole bh) {
        Decimal1f result = decA.divide(decB);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal1f testNegate(Blackhole bh) {
        Decimal1f result = decA.negate();
        bh.consume(result);
        return result;
    }

    // --- Utility/Conversion Benchmarks ---

    @Benchmark
    public MutableDecimal1f testToMutableDecimal(Blackhole bh) {
        MutableDecimal1f mutable = decA.toMutableDecimal();
        bh.consume(mutable);
        return mutable;
    }

    @Benchmark
    public Decimal1f testToImmutableDecimal(Blackhole bh) {
        Decimal1f immutable = decA.toImmutableDecimal();
        bh.consume(immutable);
        return immutable;
    }

    @Benchmark
    public Multipliable1f testMultiplyExact(Blackhole bh) {
        Multipliable1f multiplier = decA.multiplyExact();
        bh.consume(multiplier);
        return multiplier;
    }
}
