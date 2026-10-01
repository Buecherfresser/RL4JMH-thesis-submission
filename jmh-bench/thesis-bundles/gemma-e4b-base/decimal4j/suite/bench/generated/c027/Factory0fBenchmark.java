package bench.generated.c027;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory0f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.scale.Scale0f;
import org.decimal4j.scale.ScaleMetrics;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory0fBenchmark {

    private Factory0f factory;

    // Inputs for testing
    private long testLong;
    private float testFloat;
    private double testDouble;
    private BigInteger testBigInteger;
    private BigDecimal testBigDecimal;
    private String testString;
    private Decimal<?> testDecimal;
    private RoundingMode testRoundingMode;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory0f.INSTANCE;

        // Setup inputs
        testLong = 1234567890123L;
        testFloat = 123.456f;
        testDouble = 123.45678901234567;
        testBigInteger = new BigInteger("9876543210987654321");
        testBigDecimal = new BigDecimal("987654321.12345");
        testString = "1234567.89";
        testDecimal = Decimal0f.valueOf(123.456); // A representative Decimal0f instance
        testRoundingMode = RoundingMode.HALF_UP;
    }

    // --- ValueOf(long) ---
    @Benchmark
    public Decimal0f valueOfLong(Blackhole bh) {
        Decimal0f result = factory.valueOf(testLong);
        bh.consume(result);
        return result;
    }

    // --- ValueOf(float) ---
    @Benchmark
    public Decimal0f valueOfFloat(Blackhole bh) {
        Decimal0f result = factory.valueOf(testFloat);
        bh.consume(result);
        return result;
    }

    // --- ValueOf(float, RoundingMode) ---
    @Benchmark
    public Decimal0f valueOfFloatRounding(Blackhole bh) {
        Decimal0f result = factory.valueOf(testFloat, testRoundingMode);
        bh.consume(result);
        return result;
    }

    // --- ValueOf(double) ---
    @Benchmark
    public Decimal0f valueOfDouble(Blackhole bh) {
        Decimal0f result = factory.valueOf(testDouble);
        bh.consume(result);
        return result;
    }

    // --- ValueOf(double, RoundingMode) ---
    @Benchmark
    public Decimal0f valueOfDoubleRounding(Blackhole bh) {
        Decimal0f result = factory.valueOf(testDouble, testRoundingMode);
        bh.consume(result);
        return result;
    }

    // --- ValueOf(BigInteger) ---
    @Benchmark
    public Decimal0f valueOfBigInteger(Blackhole bh) {
        Decimal0f result = factory.valueOf(testBigInteger);
        bh.consume(result);
        return result;
    }

    // --- ValueOf(BigDecimal) ---
    @Benchmark
    public Decimal0f valueOfBigDecimal(Blackhole bh) {
        Decimal0f result = factory.valueOf(testBigDecimal);
        bh.consume(result);
        return result;
    }

    // --- ValueOf(BigDecimal, RoundingMode) ---
    @Benchmark
    public Decimal0f valueOfBigDecimalRounding(Blackhole bh) {
        Decimal0f result = factory.valueOf(testBigDecimal, testRoundingMode);
        bh.consume(result);
        return result;
    }

    // --- ValueOf(Decimal<?>) ---
    @Benchmark
    public Decimal0f valueOfDecimal(Blackhole bh) {
        Decimal0f result = factory.valueOf(testDecimal);
        bh.consume(result);
        return result;
    }

    // --- ValueOf(Decimal<?>, RoundingMode) ---
    @Benchmark
    public Decimal0f valueOfDecimalRounding(Blackhole bh) {
        Decimal0f result = factory.valueOf(testDecimal, testRoundingMode);
        bh.consume(result);
        return result;
    }

    // --- Parse(String) ---
    @Benchmark
    public Decimal0f parseString(Blackhole bh) {
        Decimal0f result = factory.parse(testString);
        bh.consume(result);
        return result;
    }

    // --- Parse(String, RoundingMode) ---
    @Benchmark
    public Decimal0f parseStringRounding(Blackhole bh) {
        Decimal0f result = factory.parse(testString, testRoundingMode);
        bh.consume(result);
        return result;
    }

    // --- ValueOfUnscaled(long) ---
    @Benchmark
    public Decimal0f valueOfUnscaledLong(Blackhole bh) {
        Decimal0f result = factory.valueOfUnscaled(testLong);
        bh.consume(result);
        return result;
    }

    // --- ValueOfUnscaled(long, int scale) ---
    @Benchmark
    public Decimal0f valueOfUnscaledLongScale(Blackhole bh) {
        // Scale 0 is fixed for Factory0f, but we test the method signature
        Decimal0f result = factory.valueOfUnscaled(testLong, 1);
        bh.consume(result);
        return result;
    }

    // --- ValueOfUnscaled(long, int scale, RoundingMode) ---
    @Benchmark
    public Decimal0f valueOfUnscaledLongScaleRounding(Blackhole bh) {
        Decimal0f result = factory.valueOfUnscaled(testLong, 1, testRoundingMode);
        bh.consume(result);
        return result;
    }

    // --- NewArray(int length) ---
    @Benchmark
    public Decimal0f[] newArray(Blackhole bh) {
        int length = 100;
        Decimal0f[] result = factory.newArray(length);
        bh.consume(result);
        return result;
    }

    // --- NewMutable() ---
    @Benchmark
    public MutableDecimal0f newMutable(Blackhole bh) {
        MutableDecimal0f result = factory.newMutable();
        bh.consume(result);
        return result;
    }

    // --- NewMutableArray(int length) ---
    @Benchmark
    public MutableDecimal0f[] newMutableArray(Blackhole bh) {
        int length = 100;
        MutableDecimal0f[] result = factory.newMutableArray(length);
        bh.consume(result);
        return result;
    }
}
