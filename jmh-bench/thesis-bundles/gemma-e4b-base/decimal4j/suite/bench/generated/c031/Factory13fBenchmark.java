package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.factory.Factory13f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.mutable.MutableDecimal13f;
import org.decimal4j.scale.Scale13f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory13fBenchmark {

    private Factory13f factory;

    // Inputs for various methods
    private long testLong;
    private float testFloat;
    private double testDouble;
    private BigInteger testBigInteger;
    private BigDecimal testBigDecimal;
    private String testString;
    private Decimal13f testDecimal;
    private ScaleMetrics testScaleMetrics;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory13f.INSTANCE;

        // 1. Long input
        testLong = 1234567890123L;

        // 2. Float input
        testFloat = 3.14159f;

        // 3. Double input
        testDouble = 2.718281828;

        // 4. BigInteger input
        testBigInteger = new BigInteger("9876543210987654321");

        // 5. BigDecimal input
        testBigDecimal = new BigDecimal("123.456789012345");

        // 6. String input
        testString = "123.456";

        // 7. Decimal<?> input
        testDecimal = Decimal13f.valueOf(10.0);

        // 8. ScaleMetrics input (using the specific scale metrics for 13f)
        testScaleMetrics = Scale13f.INSTANCE;
    }

    // --- Basic Factory/Scale Methods ---

    @Benchmark
    public void testGetScaleMetrics(Blackhole bh) {
        bh.consume(factory.getScaleMetrics());
    }

    @Benchmark
    public void testGetScale(Blackhole bh) {
        bh.consume(factory.getScale());
    }

    @Benchmark
    public void testImmutableType(Blackhole bh) {
        bh.consume(factory.immutableType());
    }

    @Benchmark
    public void testMutableType(Blackhole bh) {
        bh.consume(factory.mutableType());
    }

    @Benchmark
    public void testDeriveFactoryIntScale(Blackhole bh) {
        // Derive factory for a different scale (e.g., scale 5)
        bh.consume(factory.deriveFactory(5));
    }

    @Benchmark
    public void testDeriveFactoryScaleMetrics(Blackhole bh) {
        // Derive factory using a specific scale metrics object
        bh.consume(factory.deriveFactory(testScaleMetrics));
    }

    // --- ValueOf Methods (Primitives) ---

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        bh.consume(factory.valueOf(testLong));
    }

    @Benchmark
    public void testValueOfFloat(Blackhole bh) {
        bh.consume(factory.valueOf(testFloat));
    }

    @Benchmark
    public void testValueOfFloatRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(testFloat, RoundingMode.HALF_UP));
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        bh.consume(factory.valueOf(testDouble));
    }

    @Benchmark
    public void testValueOfDoubleRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(testDouble, RoundingMode.DOWN));
    }

    // --- ValueOf Methods (Objects) ---

    @Benchmark
    public void testValueOfBigInteger(Blackhole bh) {
        bh.consume(factory.valueOf(testBigInteger));
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        bh.consume(factory.valueOf(testBigDecimal));
    }

    @Benchmark
    public void testValueOfBigDecimalRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(testBigDecimal, RoundingMode.CEILING));
    }

    @Benchmark
    public void testValueOfDecimal(Blackhole bh) {
        bh.consume(factory.valueOf(testDecimal));
    }

    @Benchmark
    public void testValueOfDecimalRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(testDecimal, RoundingMode.HALF_EVEN));
    }

    // --- Parsing Methods ---

    @Benchmark
    public void testParseString(Blackhole bh) {
        bh.consume(factory.parse(testString));
    }

    @Benchmark
    public void testParseStringRoundingMode(Blackhole bh) {
        bh.consume(factory.parse(testString, RoundingMode.UP));
    }

    // --- ValueOfUnscaled Methods ---

    @Benchmark
    public void testValueOfUnscaledLong(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(testLong));
    }

    @Benchmark
    public void testValueOfUnscaledLongScale(Blackhole bh) {
        // Using a different scale for the test
        bh.consume(factory.valueOfUnscaled(testLong, 5));
    }

    @Benchmark
    public void testValueOfUnscaledLongScaleRoundingMode(Blackhole bh) {
        // Using a different scale and rounding mode
        bh.consume(factory.valueOfUnscaled(testLong, 5, RoundingMode.DOWN));
    }

    // --- Array/Mutable Methods ---

    @Benchmark
    public void testNewArray(Blackhole bh) {
        // Test creation of an array of size 10
        bh.consume(factory.newArray(10));
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        bh.consume(factory.newMutable());
    }

    @Benchmark
    public void testNewMutableArray(Blackhole bh) {
        // Test creation of an array of size 10
        bh.consume(factory.newMutableArray(10));
    }
}
