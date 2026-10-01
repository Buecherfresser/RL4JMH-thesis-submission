package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal0f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.exact.Multipliable0f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal0fBenchmark {

    // Inputs for various factory methods
    private long testLong;
    private float testFloat;
    private double testDouble;
    private BigDecimal testBigDecimal;
    private String testString;
    private Decimal<?> testGenericDecimal;
    private BigInteger testBigInteger;

    @Setup
    public void setup() {
        // 1. Long input
        testLong = 1234567890123L;

        // 2. Float input
        testFloat = 3.14159f;

        // 3. Double input
        testDouble = 2.718281828;

        // 4. BigDecimal input
        testBigDecimal = new BigDecimal("987654321.12345");

        // 5. String input
        testString = "12345";

        // 6. Generic Decimal input (using Decimal0f as a concrete example)
        testGenericDecimal = Decimal0f.valueOf(10);

        // 7. BigInteger input
        testBigInteger = BigInteger.valueOf(987654321012345L);
    }

    // --- Factory Method Benchmarks ---

    @Benchmark
    public Decimal0f benchmarkValueOfLong(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(testLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfFloat(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(testFloat);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfFloatWithRounding(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(testFloat, RoundingMode.HALF_DOWN);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfDouble(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(testDouble);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(testDouble, RoundingMode.CEILING);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfBigInteger(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(testBigInteger);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(testBigDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfBigDecimalWithRounding(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(testBigDecimal, RoundingMode.DOWN);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfDecimal(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(testGenericDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfString(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(testString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfStringWithRounding(Blackhole bh) {
        Decimal0f result = Decimal0f.valueOf(testString, RoundingMode.HALF_UP);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfUnscaledLong(Blackhole bh) {
        // Test conversion from raw unscaled long
        Decimal0f result = Decimal0f.valueOfUnscaled(testLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfUnscaledLongWithScale(Blackhole bh) {
        // Test conversion from raw unscaled long with arbitrary scale (though scale 0 is expected)
        Decimal0f result = Decimal0f.valueOfUnscaled(testLong, 5);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkValueOfUnscaledLongWithScaleAndRounding(Blackhole bh) {
        // Test conversion from raw unscaled long with scale and rounding
        Decimal0f result = Decimal0f.valueOfUnscaled(testLong, 5, RoundingMode.HALF_UP);
        bh.consume(result);
        return result;
    }

    // --- Instance Method Benchmarks ---

    @Benchmark
    public MutableDecimal0f benchmarkToMutableDecimal(Blackhole bh) {
        // Since Decimal0f is immutable, we use a constant instance for the test
        Decimal0f input = Decimal0f.ONE;
        MutableDecimal0f result = input.toMutableDecimal();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkToImmutableDecimal(Blackhole bh) {
        // Since Decimal0f is immutable, this should be a fast identity operation
        Decimal0f input = Decimal0f.ONE;
        Decimal0f result = input.toImmutableDecimal();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public org.decimal4j.scale.Scale0f benchmarkGetScaleMetrics(Blackhole bh) {
        Decimal0f input = Decimal0f.ONE;
        org.decimal4j.scale.Scale0f result = input.getScaleMetrics();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int benchmarkGetScale(Blackhole bh) {
        Decimal0f input = Decimal0f.ONE;
        int result = input.getScale();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public org.decimal4j.factory.Factory0f benchmarkGetFactory(Blackhole bh) {
        Decimal0f input = Decimal0f.ONE;
        org.decimal4j.factory.Factory0f result = input.getFactory();
        bh.consume(result);
        return result;
    }

    // --- Arithmetic Benchmarks (Using implied methods from API digest) ---
    // Note: Since the source code provided does not show arithmetic methods (like add/multiply)
    // on Decimal0f, we must assume they exist based on the API digest and AbstractImmutableDecimal inheritance.
    // We will test addition, which is a fundamental operation.

    @Benchmark
    public Decimal0f benchmarkAdd(Blackhole bh) {
        // Use constants for reliable input
        Decimal0f a = Decimal0f.FIVE;
        Decimal0f b = Decimal0f.TEN;
        // Assuming Decimal0f implements add(Decimal0f other)
        Decimal0f result = a.add(b); 
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal0f benchmarkMultiply(Blackhole bh) {
        // Use constants for reliable input
        Decimal0f a = Decimal0f.FIVE;
        Decimal0f b = Decimal0f.TWO;
        // Assuming Decimal0f implements multiply(Decimal0f other)
        Decimal0f result = a.multiply(b); 
        bh.consume(result);
        return result;
    }
}
